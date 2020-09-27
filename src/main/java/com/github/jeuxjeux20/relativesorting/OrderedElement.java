package com.github.jeuxjeux20.relativesorting;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Set;

import static java.util.Objects.requireNonNull;

/**
 * Represents an ordered element with an identifier, its actual element and order constraints.
 * <p>
 * Two ordered elements are considered equal if their identifier is the same (see {@link #equals(Object)}).
 *
 * @param <T> the type of the element
 */
public final class OrderedElement<T> {
    private final T element;
    private final @Nullable Object identifier;
    private final OrderConstraints orderConstraints;

    /**
     * Constructs a new {@link OrderedElement} instance with
     * the specified identifier, element, and order.
     *
     * @param identifier       the identifier
     * @param element          the element
     * @param orderConstraints the order constraints
     */
    public OrderedElement(@Nullable Object identifier, T element, OrderConstraints orderConstraints) {
        this.identifier = identifier;
        this.element = element;
        this.orderConstraints = orderConstraints;
    }


    /**
     * Creates an equality token, which should only be used in scenarios such as {@link Map#get(Object)},
     * or even {@link Set#contains(Object)}.
     *
     * @param identifier the identifier
     * @param <T>        the type of the element
     * @return an equality token {@link OrderedElement}, with the specified
     * identifier, a dummy element and a null order
     */
    public static <T> OrderedElement<T> equalityToken(Object identifier) {
        return new OrderedElement<>(identifier, null, OrderConstraints.EMPTY);
    }

    /**
     * Gets the identifier that identifies a element.
     *
     * @return the identifier that identifies a element
     */
    public @Nullable Object getIdentifier() {
        return identifier;
    }

    public OrderConstraints getOrderConstraints() {
        return orderConstraints;
    }

    /**
     * Gets the actual element.
     *
     * @return the actual element
     */
    public T getElement() {
        return element;
    }

    /**
     * Returns {@code true} if the specified object meets these requirements:
     * <ul>
     *     <li>it is an instance of {@link OrderedElement}</li>
     *     <li>both instances have an equal, non-null identifier</li>
     * </ul>
     *
     * @param o the object to test for equality
     * @return {@code true} if the objects are considered equal, otherwise {@code false}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderedElement<?> that = (OrderedElement<?>) o;
        return identifier != null && that.identifier != null &&
               Objects.equal(identifier, that.identifier);
    }

    /**
     * Returns the hash code of the identifier.
     *
     * @return the hash code of the identifier
     */
    @Override
    public int hashCode() {
        return Objects.hashCode(identifier);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("identifier", identifier)
                .add("orderConstraints", orderConstraints)
                .add("element", element)
                .toString();
    }
}
