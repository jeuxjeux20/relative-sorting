package com.github.jeuxjeux20.relativesorting;

/**
 * Thrown when a element couldn't be resolved by its identifier.
 */
public class UnableToResolveElementException extends RuntimeException {
    private final Object unresolved;

    public UnableToResolveElementException(Object unresolved) {
        super("Cannot resolve element '" + unresolved + "'.");
        this.unresolved = unresolved;
    }

    public UnableToResolveElementException(String message, Object unresolved) {
        super(message);
        this.unresolved = unresolved;
    }

    public UnableToResolveElementException(String message, Throwable cause, Object unresolved) {
        super(message, cause);
        this.unresolved = unresolved;
    }

    public UnableToResolveElementException(Throwable cause, Object unresolved) {
        super(cause);
        this.unresolved = unresolved;
    }

    public Object getUnresolved() {
        return unresolved;
    }
}
