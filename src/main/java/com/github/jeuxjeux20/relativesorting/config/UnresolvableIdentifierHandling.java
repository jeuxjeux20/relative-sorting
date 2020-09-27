package com.github.jeuxjeux20.relativesorting.config;

import com.github.jeuxjeux20.relativesorting.UnableToResolveElementException;

/**
 * Represents the behavior when an identifier can't be resolved as a element.
 */
public enum UnresolvableIdentifierHandling {
    /**
     * Ignores the identifier.
     */
    IGNORE,
    /**
     * Throws a {@link UnableToResolveElementException}.
     */
    THROW
}
