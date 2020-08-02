package com.github.jeuxjeux20.relativesorting;

/**
 * A function that transforms a {@link OrderedElement} into another one, with different values,
 * or not.
 */
@FunctionalInterface
public interface OrderedElementTransformer {
    /**
     * Transforms the given {@link OrderedElement} into another one, which may also be the
     * same as the initial value.
     *
     * @param orderedElement the {@link OrderedElement} to transform
     * @param <T>            the type of the element
     * @return the transformed {@link OrderedElement}
     */
    <T> OrderedElement<T> transform(OrderedElement<T> orderedElement);
}
