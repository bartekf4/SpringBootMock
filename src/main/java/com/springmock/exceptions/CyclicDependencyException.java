package com.springmock.exceptions;

/**
 * The `CyclicDependencyException` is thrown when a cycle is detected in the dependencies of the beans in the
 * application context.
 */
public class CyclicDependencyException extends RuntimeException {
    /**
     * Constructs a new `CyclicDependencyException` with the specified message.
     *
     * @param message the detail message
     */
    public CyclicDependencyException(String message) {
        super(message);
    }
}
