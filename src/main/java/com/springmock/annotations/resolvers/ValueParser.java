package com.springmock.annotations.resolvers;

import com.springmock.EnvironmentVariables;
import com.springmock.exceptions.NoSuchEnvironmentVariable;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The ValueAndMultiValueHelper class contains two static final sets, COLLECTION_TYPE_MAP and FIELD_TYPE_MAP. The
 * COLLECTION_TYPE_MAP set contains the Class objects for the following types: Array, List, Set, and Map. This set can
 * be used to check if a given class object represents a collection type. The FIELD_TYPE_MAP set contains the Class
 * objects for the following types: Byte, Short, Integer, Long, Float, Double, Boolean, Character, and String. This set
 * can be used to check if a given class object represents a field type.
 */
public class ValueParser {
    public static final Set<Class<?>> COLLECTION_TYPES = Set.of(
            Array.class,
            List.class,
            Set.class,
            Map.class);
    public static final Set<Class<?>> FIELD_PARAMETER_TYPES = Set.of(
            Byte.class,
            Short.class,
            Integer.class,
            Long.class,
            Float.class,
            Double.class,
            Boolean.class,
            Character.class,
            String.class);


    /**
     * The method {@code isCollectionFieldOfSupportedParametrizedType} checks if the given field is of a collection type
     * that is supported by the application. It first checks if the field is of an array type, and if it is, it gets the
     * component type of the array and checks if it is a supported type using the {@code FieldParameterTypes} set or
     * primitive type. If the field is not an array, it retrieves the generic type of the field and checks each type
     * argument in the actual type arguments using the FieldParameterTypes set. If all the type arguments are supported,
     * it returns true, otherwise it returns false.
     *
     * @param field The field to be checked
     * @return true if the field is of a supported collection type, false otherwise.
     */
    public static boolean isCollectionFieldOfSupportedParametrizedType(Field field) {
        Class<?> fieldType = field.getType();
        if (fieldType.isArray()) {
            Class<?> arrayType = fieldType.getComponentType();
            return FIELD_PARAMETER_TYPES.contains(arrayType) || arrayType.isPrimitive();
        } else {
            boolean areAllTypesAllowed = true;
            ParameterizedType genericType = (ParameterizedType) field.getGenericType();
            for (Type type : genericType.getActualTypeArguments()) {
                areAllTypesAllowed = areAllTypesAllowed && isTypeOfWrapperSupported((Class<?>) type);
            }
            return areAllTypesAllowed;
        }
    }

    public static boolean isTypeOfWrapperSupported(Class<?> clazz) {
        return FIELD_PARAMETER_TYPES.contains(clazz) || clazz.isPrimitive();
    }


    /**
     * The method isFieldOfSupportedCollectionType checks if the given field is of a collection type that is supported
     * by the application. It retrieves the type of the field and checks if it is in the CollectionTypes set or if it is
     * an array. If the field is of a supported collection type, it returns true, otherwise it returns false.
     *
     * @param collection The field to be checked
     * @return true if the field is of a supported collection type, false otherwise.
     */
    public static boolean isFieldOfSupportedCollectionType(@NotNull Field collection) {
        Class<?> fieldType = collection.getType();
        return COLLECTION_TYPES.contains(fieldType) || fieldType.isArray();
    }

    /**
     * Gets the value of a specified environment variable.
     *
     * @param value the name of the environment variable
     * @return the value of the environment variable
     * @throws NoSuchEnvironmentVariable if the environment variable does not exist
     */
    public static String getContent(String value) {
        String content;
        content = System.getenv(value);
        if (content != null) {
            return content;
        } else {
            try {
                Field fieldEnvironmentVariable = EnvironmentVariables.class.getField(value);
                return (String) fieldEnvironmentVariable.get(null);
            } catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException e) {
                throw new NoSuchEnvironmentVariable("Cannot find variable: " + value);
            }
        }
    }

    /**
     * Converts a primitive class to its corresponding wrapper class.
     *
     * @param primitiveClass the primitive class to be converted
     * @return the corresponding wrapper class
     * @throws IllegalArgumentException if the class is not a primitive class
     */
    public static Class<?> primitiveToWrapper(Class<?> primitiveClass) {
        if (primitiveClass.equals(boolean.class)) {
            return Boolean.class;
        } else if (primitiveClass.equals(char.class)) {
            return Character.class;
        } else if (primitiveClass.equals(byte.class)) {
            return Byte.class;
        } else if (primitiveClass.equals(short.class)) {
            return Short.class;
        } else if (primitiveClass.equals(int.class)) {
            return Integer.class;
        } else if (primitiveClass.equals(long.class)) {
            return Long.class;
        } else if (primitiveClass.equals(float.class)) {
            return Float.class;
        } else if (primitiveClass.equals(double.class)) {
            return Double.class;
        } else {
            throw new IllegalArgumentException("Invalid primitive class");
        }
    }

    /**
     * Converts a string into an object of a specified type.
     *
     * @param content      the string to be converted
     * @param wrapperClass the type of the resulting object
     * @return the resulting object
     * @throws RuntimeException if the conversion fails
     */
    public static <T> T convert(String content, Class<T> wrapperClass) {
        try {
            String typeName = wrapperClass.getSimpleName();
            if (typeName.equals("Integer")) { //lovely convention
                typeName = "Int";
            } else if (typeName.equals("String")) { //nothing to parse if target type is String, content is already string
                return wrapperClass.cast(content);
            }
            Method method = wrapperClass.getMethod("parse" + typeName, String.class);
            method.invoke(null, content);
            return wrapperClass.cast(method.invoke(null, content));
        } catch (NoSuchMethodException | IllegalAccessException
                 | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

}
