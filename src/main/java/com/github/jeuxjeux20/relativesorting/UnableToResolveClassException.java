package com.github.jeuxjeux20.relativesorting;

/**
 * Thrown when a element couldn't be resolved
 * from the class of the @{@link Order} annotation.
 */
public class UnableToResolveClassException extends RuntimeException {
    private final Class<?> unresolvedClass;

    public UnableToResolveClassException(Class<?> unresolvedClass) {
        super("Cannot resolve class '" + unresolvedClass + "' as a element.");
        this.unresolvedClass = unresolvedClass;
    }

    public UnableToResolveClassException(String message, Class<?> unresolvedClass) {
        super(message);
        this.unresolvedClass = unresolvedClass;
    }

    public UnableToResolveClassException(String message, Throwable cause, Class<?> unresolvedClass) {
        super(message, cause);
        this.unresolvedClass = unresolvedClass;
    }

    public UnableToResolveClassException(Throwable cause, Class<?> unresolvedClass) {
        super(cause);
        this.unresolvedClass = unresolvedClass;
    }

    public Class<?> getUnresolvedClass() {
        return unresolvedClass;
    }
}
