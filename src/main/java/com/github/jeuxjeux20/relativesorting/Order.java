package com.github.jeuxjeux20.relativesorting;

import java.lang.annotation.*;

/**
 * Specifies the relative constraints on the position of elements
 * using this class as an identifier.
 */
@Inherited
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Order {
    /**
     * Returns the classes that this element should precede.
     *
     * @return the classes that this element should precede
     */
    Class<?>[] before() default {};

    /**
     * Returns the classes that this element should succeed.
     *
     * @return the classes that this element should succeed
     */
    Class<?>[] after() default {};

    /**
     * Defines the position of the element when its
     * exact position cannot be determined.
     * <p>
     * A higher value will put this element further in the list,
     * while a lower value will put it closer.
     * <p>
     * Note that a value of 0 is considered default and will get processed by
     * a {@link DefaultPositionProvider}.
     *
     * @return the position of the element
     */
    int position() default 0;
}
