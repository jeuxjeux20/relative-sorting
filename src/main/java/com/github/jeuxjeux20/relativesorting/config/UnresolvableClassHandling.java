package com.github.jeuxjeux20.relativesorting.config;

import com.github.jeuxjeux20.relativesorting.Order;
import com.github.jeuxjeux20.relativesorting.UnableToResolveClassException;

/**
 * Represents the behavior when a class in @{@link Order} can't be resolved as a element.
 */
public enum UnresolvableClassHandling {
    /**
     * Ignores the class.
     */
    IGNORE,
    /**
     * Throws a {@link UnableToResolveClassException}.
     */
    THROW
}
