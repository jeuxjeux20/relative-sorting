package com.github.jeuxjeux20.relativesorting.config;

import com.github.jeuxjeux20.relativesorting.Order;
import com.github.jeuxjeux20.relativesorting.OrderedElement;
import com.github.jeuxjeux20.relativesorting.Placeholders;
import org.junit.jupiter.api.Test;

import static com.github.jeuxjeux20.relativesorting.TestOrderedBindings.createOrderedBinding;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ClosestDefaultPositionProviderTests {
    private static final DefaultPositionProvider DEFAULT_POSITION_PROVIDER
            = DefaultPositionProvider.CLOSEST;

    @Test
    void zero_with_no_order() {
        OrderedElement<?> orderedElement = createOrderedBinding(NoOrder.class);

        int position = DEFAULT_POSITION_PROVIDER.get(orderedElement);

        assertEquals(0, position);
    }

    @Test
    void zero_with_both_empty() {
        OrderedElement<?> orderedElement = createOrderedBinding(BothEmpty.class);

        int position = DEFAULT_POSITION_PROVIDER.get(orderedElement);

        assertEquals(0, position);
    }

    @Test
    void zero_with_both_non_empty() {
        OrderedElement<?> orderedElement = createOrderedBinding(BothNonEmpty.class);

        int position = DEFAULT_POSITION_PROVIDER.get(orderedElement);

        assertEquals(0, position);
    }

    @Test
    void positive_1_with_before_only() {
        OrderedElement<?> orderedElement = createOrderedBinding(BeforeOnly.class);

        int position = DEFAULT_POSITION_PROVIDER.get(orderedElement);

        assertEquals(1, position);
    }

    @Test
    void negative_1_with_after_only() {
        OrderedElement<?> orderedElement = createOrderedBinding(AfterOnly.class);

        int position = DEFAULT_POSITION_PROVIDER.get(orderedElement);

        assertEquals(-1, position);
    }


    static class NoOrder {}

    @Order
    static class BothEmpty {}

    @Order(before = Placeholders.Cat.class, after = Placeholders.Dog.class)
    static class BothNonEmpty {}

    @Order(before = Placeholders.Cat.class)
    static class BeforeOnly {}

    @Order(after = Placeholders.Dog.class)
    static class AfterOnly {}
}
