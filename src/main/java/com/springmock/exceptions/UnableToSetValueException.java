package com.springmock.exceptions;

/**
 * Indicates that a value cannot be set for a field.
 */
public class UnableToSetValueException extends RuntimeException {
    /**
     * Constructs a new UnableToSetValueException with the specified message.
     *
     * @param message the detail message
     */
    public UnableToSetValueException(String message) {
        super(message);
    }
}
