package com.github.jeuxjeux20.relativesorting;

import com.google.common.reflect.TypeToken;

public final class TestOrderedBindings {
    private TestOrderedBindings() {
    }

    public static <T> OrderedElement<T> createOrderedBinding(TypeToken<?> type) {
        return OrderedElement.fromType(type, null);
    }

    public static <T> OrderedElement<T> createOrderedBinding(TypeToken<?> type, Order order) {
        return new OrderedElement<>(type, null, order);
    }

    public static <T> OrderedElement<T> createOrderedBinding(Class<?> clazz) {
        return OrderedElement.fromType(clazz, null);
    }

    public static <T> OrderedElement<T> createOrderedBinding(Class<?> clazz, Order order) {
        return new OrderedElement<>(clazz, null, order);
    }
}
