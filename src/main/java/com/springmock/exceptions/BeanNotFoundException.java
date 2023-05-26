package com.springmock.exceptions;


/**
 * The `BeanNotFoundException` is thrown when a required bean is not found in the application context.
 */
public class BeanNotFoundException extends RuntimeException {

    /**
     * Constructs a new `BeanNotFoundException` with the specified message.
     *
     * @param message the detail message
     */
    public BeanNotFoundException(String message) {
        super(message);
    }
}
