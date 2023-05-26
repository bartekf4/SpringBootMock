package com.springmock.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * The {@link Value}annotation is used to set the value of a field with the specified value. The specified value is
 * considered as a name of environment variable.
 *
 * @see MultiValue
 */
@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Value {
    /**
     * The value to set.
     *
     * @return the name of the environment variable, which value is injected
     */
    String value();
}
