package com.github.jeuxjeux20.relativesorting.config;

import com.github.jeuxjeux20.relativesorting.OrderedElement;

import java.util.Collection;

public interface DuplicateIdentifierSelector {
    DuplicateIdentifierSelector ARBITRARY = new DuplicateIdentifierSelector() {
        @Override
        public <T> OrderedElement<? extends T> select(Collection<OrderedElement<? extends T>> items) {
            return items.iterator().next();
        }
    };

    <T> OrderedElement<? extends T> select(Collection<OrderedElement<? extends T>> items);
}
