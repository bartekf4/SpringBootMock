package com.springmock.annotations.resolvers;

import com.springmock.annotations.Value;
import static com.springmock.annotations.resolvers.ValueParser.convert;
import static com.springmock.annotations.resolvers.ValueParser.getContent;
import static com.springmock.annotations.resolvers.ValueParser.isTypeOfWrapperSupported;
import static com.springmock.annotations.resolvers.ValueParser.primitiveToWrapper;
import com.springmock.exceptions.IllegalTypeException;

import java.lang.reflect.Field;

public class ValueResolver {
    public static Object resolve(Field field) {
        Class<?> fieldType = field.getType();
        System.out.println(fieldType);
        if (fieldType.isPrimitive()) {
            fieldType = primitiveToWrapper(fieldType);
        }
        if (!isTypeOfWrapperSupported(field.getType())) {
            throw new IllegalTypeException("Illegal type of field: " + fieldType);
        }
        Value fieldAnnotation = field.getAnnotation(Value.class);
        String content = getContent(fieldAnnotation.value());

        return convert(content, fieldType);
    }


}
