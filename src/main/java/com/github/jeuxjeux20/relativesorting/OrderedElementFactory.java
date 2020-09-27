package com.github.jeuxjeux20.relativesorting;

import org.jetbrains.annotations.Nullable;

/**
 * A factory to create an {@link OrderedElement} from the element value.
 */
public interface OrderedElementFactory<T> {
    /**
     * Creates an {@link OrderedElement} from the specified element.
     * <p>
     * This may return {@code null} if the creation has failed.
     *
     * @param element the element
     * @return the ordered element, or {@code null} if it has failed
     */
    @Nullable OrderedElement<? extends T> create(T element);
}
