package com.springmock.exceptions;

/**
 * Indicates that a string cannot be mapped to an object.
 */
public class UnmappableStringException extends RuntimeException {

    /**
     * Constructs a new UnmappableStringException with the specified message.
     *
     * @param message the detail message
     */
    public UnmappableStringException(String message) {
        super(message);
    }
}
