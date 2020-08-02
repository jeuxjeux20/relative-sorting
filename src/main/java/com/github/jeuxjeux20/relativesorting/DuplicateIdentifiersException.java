package com.github.jeuxjeux20.relativesorting;

/**
 * Thrown when multiple elements have the same identifier.
 */
public class DuplicateIdentifiersException extends RuntimeException {
    public DuplicateIdentifiersException() {
    }

    public DuplicateIdentifiersException(String message) {
        super(message);
    }

    public DuplicateIdentifiersException(String message, Throwable cause) {
        super(message, cause);
    }

    public DuplicateIdentifiersException(Throwable cause) {
        super(cause);
    }
}
