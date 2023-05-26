package com.springmock.exceptions;

/**
 * Indicates that an environment variable does not exist.
 */
public class NoSuchEnvironmentVariable extends RuntimeException {
    /**
     * Constructs a new NoSuchEnvironmentVariable with the specified message.
     *
     * @param message the detail message
     */
    public NoSuchEnvironmentVariable(String message) {
        super(message);
    }
}

