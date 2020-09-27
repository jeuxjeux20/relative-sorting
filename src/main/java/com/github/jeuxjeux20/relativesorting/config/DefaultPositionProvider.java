package com.github.jeuxjeux20.relativesorting.config;

import com.github.jeuxjeux20.relativesorting.OrderConstraints;
import com.github.jeuxjeux20.relativesorting.OrderedElement;

/**
 * Gives a default position value for an {@link OrderConstraints} when it has after or before constraints or
 * when its {@link OrderConstraints#getPosition()} value is 0.
 */
public interface DefaultPositionProvider {
    /**
     * Returns 0.
     */
    DefaultPositionProvider ZERO = b -> 0;
    /**
     * Tries to put the element closer to its order constraints with the following behavior:
     * <ul>
     *     <li>When there is no {@link OrderConstraints}, returns 0.</li>
     *     <li>When {@link OrderConstraints#getAfter()} and {@link OrderConstraints#getBefore()} are both
     *     not empty or both empty, returns 0.</li>
     *     <li>When only {@link OrderConstraints#getBefore()}} is specified, returns 1.</li>
     *     <li>When only {@link OrderConstraints#getAfter()}} is specified, returns -1.</li>
     * </ul>
     */
    DefaultPositionProvider CLOSEST = element -> {
        OrderConstraints constraints = element.getOrderConstraints();
        if (constraints == null) {
            return 0;
        }

        if ((constraints.getBefore().size() != 0 && constraints.getAfter().size() != 0) ||
            (constraints.getBefore().size() == 0 && constraints.getAfter().size() == 0)) {
            return 0;
        } else if (constraints.getBefore().size() != 0) {
            return 1;
        } else {
            return -1;
        }
    };

    int get(OrderedElement<?> element);
}
