package com.springmock.exceptions;

/**
 * The `IllegalTypeException` is thrown when a field annotated with <code>@Value</code> has an illegal type, different
 * from primitives or wrapped primitives, or String
 */
public class IllegalTypeException extends RuntimeException {
    /**
     * Constructs a new `IllegalTypeException` with the specified message.
     *
     * @param message the detail message
     */
    public IllegalTypeException(String message) {
        super(message);
    }

    public IllegalTypeException() {
        super();
    }
}
