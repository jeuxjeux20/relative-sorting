package com.github.jeuxjeux20.relativesorting;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;

import java.util.Collection;

public class OrderConstraints {
    public static final OrderConstraints EMPTY = new OrderConstraints(
            ImmutableList.of(), ImmutableList.of(), 0);

    private final ImmutableList<Object> before;
    private final ImmutableList<Object> after;
    private final int position;

    public OrderConstraints(Collection<?> before, Collection<?> after, int position) {
        this.before = ImmutableList.copyOf(before);
        this.after = ImmutableList.copyOf(after);
        this.position = position;
    }

    public ImmutableList<Object> getAfter() {
        return after;
    }

    public ImmutableList<Object> getBefore() {
        return before;
    }

    public int getPosition() {
        return position;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("before", before)
                .add("after", after)
                .add("position", position)
                .toString();
    }
}
