package com.springmock.annotations;

import com.springmock.ApplicationContext;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * The {@link Component} annotation is used to mark a class as a bean that should be managed by the
 * {@link ApplicationContext}.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Component {
}
