package com.springmock.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Indicates that a field is a {@link MultiValue} field and specifies the environment variable that contains the values
 * and the delimiter as default used to separate the values.
 *
 * @see Value
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface MultiValue {
    String value();

    String delimiter() default ",";
}
