package com.springmock.annotations.resolvers;

import com.springmock.annotations.MultiValue;
import static com.springmock.annotations.resolvers.ValueParser.convert;
import static com.springmock.annotations.resolvers.ValueParser.getContent;
import static com.springmock.annotations.resolvers.ValueParser.isCollectionFieldOfSupportedParametrizedType;
import static com.springmock.annotations.resolvers.ValueParser.isFieldOfSupportedCollectionType;
import static com.springmock.annotations.resolvers.ValueParser.primitiveToWrapper;
import com.springmock.exceptions.IllegalTypeException;
import com.springmock.exceptions.UnmappableStringException;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * The {@code MultiValueResolver} class is used to resolve multi-value fields annotated with {@link MultiValue}.
 */
public class MultiValueResolver {

    /**
     * Resolves the values for the specified {@code field}.
     *
     * @param field the field to resolve values for
     * @return the resolved values for the field
     * @throws IllegalArgumentException if the field is not of a supported type or is not properly parametrized
     */

    public static Object resolve(@NotNull Field field) {
        validateFieldType(field);
        MultiValue multiValue = field.getAnnotation(MultiValue.class);
        String content = getContent(multiValue.value());
        String[] contentArray = content.split(multiValue.delimiter());
        Class<?> type = field.getType();

        if (type.isArray() || type == List.class || type == Set.class || type == Map.class) {
            if (type.isArray()) {
                Class<?> arrayType = type.getComponentType();
                return parseArray(contentArray, arrayType, type);
            } else {
                ParameterizedType parameterizedTypeField = (ParameterizedType) field.getGenericType();
                Class<?> actualTypeArgument = (Class<?>) parameterizedTypeField.getActualTypeArguments()[0];
                if (type == List.class) {
                    return parseList(contentArray, actualTypeArgument);
                } else if (type == Set.class) {
                    return parseSet(contentArray, actualTypeArgument);
                } else {
                    Class<?> firstArgument = actualTypeArgument;
                    Class<?> secondArgument = (Class<?>) parameterizedTypeField.getActualTypeArguments()[1];
                    return parseMap(contentArray, firstArgument, secondArgument);
                }
            }
        } else {
            throw new IllegalTypeException();
        }
    }

    private static <T, K> T parseArray(String[] contentArray, Class<K> arrayType, Class<T> type) {
        return type.cast(getArray(contentArray, arrayType));
    }

    private static <T> List<T> parseList(String[] contentArray, Class<T> actualTypeArgument) {
        return Arrays.stream(contentArray).map(o -> convert(o, actualTypeArgument)).toList();
    }

    private static <T> Set<T> parseSet(String[] contentArray, Class<T> actualTypeArgument) {
        return Arrays.stream(contentArray).map(o -> convert(o, actualTypeArgument)).collect(Collectors.toSet());
    }

    private static <K, V> Map<K, V> parseMap(String[] contentArray, Class<K> firstArgument, Class<V> secondArgument) {
        return getMapFromString(contentArray, firstArgument, secondArgument);
    }

    /**
     * Converts an array of strings into an array of objects of a specified type.
     *
     * @param contentArray the array of strings to be converted
     * @param arrayType    the type of the resulting array
     * @return the resulting array of objects
     * @throws RuntimeException if the conversion fails
     */
    private static Object getArray(String[] contentArray, Class<?> arrayType) {
        Object array = Array.newInstance(arrayType, contentArray.length);
        try {
            Class<?> wrapType = arrayType.isPrimitive() ? primitiveToWrapper(arrayType) : arrayType;
            Constructor<?> ctor = wrapType.getConstructor(String.class);
            for (int i = 0; i < contentArray.length; ++i) {
                Array.set(array, i, ctor.newInstance(contentArray[i]));
            }
            return array;
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException
                 | InstantiationException ignored) {
            System.out.printf("An error occurred while trying to create an array for of type: %s for %s%s",
                    arrayType,
                    Arrays.toString(contentArray),
                    System.lineSeparator());
        }
        return array;
    }

    /**
     * Converts an array of strings into a map where each string represents a key-value pair.
     *
     * @param content    the array of strings to be converted
     * @param firstType  the type of the keys in the map
     * @param secondType the type of the values in the map
     * @return the resulting map
     * @throws UnmappableStringException if the conversion of any key or value fails
     */
    public static <T, K> Map<T, K> getMapFromString(String[] content, Class<T> firstType, Class<K> secondType) {
        Map<T, K> newMap = new HashMap<>(content.length);
        for (String pair : content) {
            String[] keyValue = pair.split(":");
            if (keyValue.length < 2) {
                throw new UnmappableStringException("Cannot map: " + Arrays.toString(content));
            }
            newMap.put(convert(keyValue[0], firstType), convert(keyValue[1], secondType));
        }
        return newMap;
    }

    /**
     * Verifies that a field's type is a collection or an array.
     *
     * @param field the field to be checked
     * @throws IllegalTypeException if the field's type is not a collection or an array
     */
    private static void validateFieldType(Field field) {
        if (!isFieldOfSupportedCollectionType(field)) {
            throw new IllegalTypeException(
                    String.format("Illegal type of collection: %s%s", field.getType(), System.lineSeparator()));
        }
        if (!isCollectionFieldOfSupportedParametrizedType(field)) {
            throw new IllegalTypeException(
                    String.format("Illegal type parametrized: %s%s", field.getType(), System.lineSeparator()));
        }
    }
}
