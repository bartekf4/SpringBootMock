package com.springmock.exceptions;

/**
 * Indicates that a bean cannot be created.
 */
public class UnableToCreateBeanException extends RuntimeException {
    /**
     * Constructs a new UnableToCreateBeanException with the specified message.
     *
     * @param message the detail message
     */
    public UnableToCreateBeanException(String message) {
        super(message);
    }
}
