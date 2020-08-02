package com.github.jeuxjeux20.relativesorting;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.google.common.reflect.TypeToken;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import static java.util.Objects.requireNonNull;

/**
 * Represents an ordered element with an identifier, its actual element and its @{@link Order} annotation.
 * <p>
 * This is an immutable class, and a builder is available to create a new instance
 * or create a new object based off another instance's values
 * (using {@link #builder(OrderedElement)} or {@link #change(Consumer)}).
 * <p>
 * Two ordered elements are considered equal if their identifier is the same (see {@link #equals(Object)}).
 *
 * @param <T> the type of the element
 */
@SuppressWarnings("UnstableApiUsage")
public final class OrderedElement<T> {
    private final T element;
    private final TypeToken<?> identifier;
    private final @Nullable Order order;

    /**
     * Constructs a new {@link OrderedElement} instance with
     * the specified identifier, element, and order (the latter may be {@code null}).
     *
     * @param identifier the identifier, as a class
     * @param element    the element
     * @param order      the order of the element, which may be {@code null}
     */
    public OrderedElement(Class<?> identifier, T element, @Nullable Order order) {
        this(TypeToken.of(requireNonNull(identifier, "identifier is null")), element, order);
    }

    /**
     * Constructs a new {@link OrderedElement} instance with
     * the specified identifier, element, and order (the latter may be {@code null}).
     *
     * @param identifier the identifier
     * @param element    the element
     * @param order      the order of the element, which may be {@code null}
     */
    public OrderedElement(TypeToken<?> identifier, T element, @Nullable Order order) {
        this.identifier = requireNonNull(identifier, "identifier is null");
        this.element = element;
        this.order = order;
    }

    /**
     * Creates a new builder with the specified identifier, element, and @{@link Order} annotation
     * retrieved from the identifier.
     *
     * @param identifier the identifier
     * @param element    the element
     * @param <T>        the type of the element
     * @return a builder with the specified identifier and element
     */
    public static <T> Builder<T> builder(TypeToken<?> identifier, T element) {
        return new Builder<>(identifier, element);
    }

    /**
     * Creates a new builder with the specified identifier, element, and @{@link Order} annotation
     * retrieved from the identifier.
     *
     * @param clazz   the class, used as an identifier
     * @param element the element
     *                @param <T> the type of the element
     * @return a builder with the specified identifier, element, and the identifier's @{@link Order} annotation
     */
    public static <T> Builder<T> builder(Class<?> clazz, T element) {
        return new Builder<>(TypeToken.of(clazz), element);
    }

    /**
     * Creates a new builder with all the values of the specified ordered element.
     *
     * @param orderedElement the ordered element to copy the values from
     *                       @param <T> the type of the element
     * @return a builder with the values of the specified ordered element
     */
    public static <T> Builder<T> builder(OrderedElement<? extends T> orderedElement) {
        return new Builder<>(orderedElement);
    }

    /**
     * Creates a new ordered element using the specified element and identifier,
     * and the identifier's @{@link Order} annotation,
     * if there isn't any, the order will be {@code null}.
     *
     * @param identifier the identifier, also used to retrieve the @{@link Order} annotation.
     * @param element    the element
     *                   @param <T> the type of the element
     * @return a ordered element with the specified identifier, and, if present, the identifier's @{@link Order} annotation
     */
    public static <T> OrderedElement<T> fromType(TypeToken<?> identifier, T element) {
        return new OrderedElement<>(identifier, element, identifier.getRawType().getAnnotation(Order.class));
    }

    /**
     * Creates a new ordered element using the specified class, which will
     * be wrapped using {@link TypeToken#of(Class)}, and the class's @{@link Order} annotation,
     * if there isn't any, the order will be {@code null}.
     *
     * @param clazz   the class used to identify the element
     * @param element the actual element
     *                @param <T> the type of the element
     * @return a ordered element with the specified class, and, if present, the class' @{@link Order} annotation
     */
    public static <T> OrderedElement<T> fromType(Class<?> clazz, T element) {
        return fromType(TypeToken.of(clazz), element);
    }

    /**
     * Creates an equality token, which should only be used in scenarios such as {@link Map#get(Object)},
     * or even {@link Set#contains(Object)}.
     *
     * @param identifier the identifier
     *                   @param <T> the type of the element
     * @return an equality token {@link OrderedElement}, with the specified
     * identifier, a dummy element and a null order
     */
    public static <T> OrderedElement<T> equalityToken(TypeToken<?> identifier) {
        return new OrderedElement<>(identifier, null, null);
    }

    /**
     * Gets the identifier that identifies a element.
     * <p>
     * This value is used, for example, to match elements
     * with their class values in {@link Order}.
     *
     * @return the identifier that identifies a element
     */
    public TypeToken<?> getIdentifier() {
        return identifier;
    }

    /**
     * Gets the @{@link Order} annotation of this ordered element, which may be {@code null}.
     *
     * @return the @{@link Order} annotation of this ordered element, which may be {@code null}.
     */
    public @Nullable Order getOrder() {
        return order;
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
     * Creates a new builder based off this ordered element, then runs the specified
     * consumer with the builder created earlier, and returns
     * the result of the builder.
     * <p>
     * <b>Example: </b>
     * <pre>OrderedElement newElement = orderedElement.change(its -&gt; its.identifier(someType));</pre>
     *
     * @param builderConsumer the consumer which will be run to apply modifications to the builder
     * @return the result of the builder
     */
    public OrderedElement<T> change(Consumer<? super Builder<T>> builderConsumer) {
        Builder<T> builder = builder(this);

        builderConsumer.accept(builder);

        return builder.build();
    }

    /**
     * Returns {@code true} if the specified object meets these requirements:
     * <ul>
     *     <li>it is an instance of {@link OrderedElement}</li>
     *     <li>both instances have an equal identifier</li>
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
        return Objects.equal(identifier, that.identifier);
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
                .add("order", order)
                .add("element", element)
                .toString();
    }

    /**
     * The builder for {@link OrderedElement}.
     * @param <T> the type of the element
     */
    public static class Builder<T> {
        private TypeToken<?> type;
        private T element;
        private @Nullable Order order;

        /**
         * Creates a new {@link Builder} with the specified identifier and element, and
         * retrieves the @{@link Order} annotation from the given identifier.
         *
         * @param identifier the identifier that identifies a element
         * @param element    the actual element
         */
        public Builder(TypeToken<?> identifier, T element) {
            this(OrderedElement.fromType(identifier, element));
        }

        /**
         * Creates a new {@link Builder} with all properties copied
         * from the specified ordered element.
         *
         * @param orderedElement the ordered element to copy the properties from
         */
        public Builder(OrderedElement<? extends T> orderedElement) {
            this.type = orderedElement.identifier;
            this.element = orderedElement.element;
            this.order = orderedElement.order;
        }

        /**
         * Sets the identifier to the specified class, wrapped using {@link TypeToken#of(Class)}.
         *
         * @param clazz the class to set as a identifier
         * @return the same builder
         */
        @CanIgnoreReturnValue
        public Builder<T> identifier(Class<?> clazz) {
            this.type = TypeToken.of(requireNonNull(clazz, "clazz is null"));
            return this;
        }

        /**
         * Sets the identifier to the specified one.
         *
         * @param type the identifier
         * @return the same builder
         */
        @CanIgnoreReturnValue
        public Builder<T> identifier(TypeToken<?> type) {
            this.type = requireNonNull(type, "identifier is null");
            return this;
        }

        /**
         * Sets the element to the specified one.
         *
         * @param element the element
         * @return the same builder
         */
        @CanIgnoreReturnValue
        public Builder<T> element(T element) {
            this.element = requireNonNull(element, "element is null");
            return this;
        }

        /**
         * Sets the order to the specified one, it may be {@code null}.
         *
         * @param order the order, which may be {@code null}
         * @return the same builder
         */
        @CanIgnoreReturnValue
        public Builder<T> order(@Nullable Order order) {
            this.order = order;
            return this;
        }

        /**
         * Creates an instance of {@link OrderedElement} using the values of this builder.
         *
         * @return an instance of {@link OrderedElement} with the values of this builder
         */
        public OrderedElement<T> build() {
            return new OrderedElement<T>(type, element, order);
        }
    }
}
