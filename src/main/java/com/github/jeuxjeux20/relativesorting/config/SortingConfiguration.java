package com.github.jeuxjeux20.relativesorting.config;

import com.github.jeuxjeux20.relativesorting.OrderedElement;

import java.util.Objects;

/**
 * Defines the configuration for sorting elements.
 */
public final class SortingConfiguration {
    public static final DefaultPositionProvider DEFAULT_DEFAULT_POSITION
            = DefaultPositionProvider.CLOSEST;

    public static final UnresolvableIdentifierHandling DEFAULT_UNRESOLVABLE_IDENTIFIER_HANDLING =
            UnresolvableIdentifierHandling.THROW;

    public static final DuplicateIdentifierSelector DEFAULT_DUPLICATE_IDENTIFIER_SELECTOR =
            DuplicateIdentifierSelector.ARBITRARY;

    /**
     * The default configuration. Default values are specified on every getter.
     */
    public static final SortingConfiguration DEFAULT = new SortingConfiguration();

    private final DefaultPositionProvider defaultPosition;
    private final UnresolvableIdentifierHandling unresolvableIdentifierHandling;
    private final DuplicateIdentifierSelector duplicateIdentifierSelector;

    private SortingConfiguration() {
        this(DEFAULT_DEFAULT_POSITION,
                DEFAULT_UNRESOLVABLE_IDENTIFIER_HANDLING,
                DEFAULT_DUPLICATE_IDENTIFIER_SELECTOR);
    }

    private SortingConfiguration(DefaultPositionProvider defaultPosition,
                                 UnresolvableIdentifierHandling unresolvableIdentifierHandling,
                                 DuplicateIdentifierSelector duplicateIdentifierSelector) {
        this.defaultPosition = defaultPosition;
        this.unresolvableIdentifierHandling = unresolvableIdentifierHandling;
        this.duplicateIdentifierSelector = duplicateIdentifierSelector;
    }

    /**
     * Creates a new builder with the default values.
     *
     * @return a new builder
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Creates a new builder with the values from the given configuration
     *
     * @param configuration the configuration to copy the values from
     * @return a new builder
     */
    public static Builder builder(SortingConfiguration configuration) {
        return new Builder(configuration);
    }

    /**
     * Gets the default position, which can be used on a specific {@link OrderedElement}.
     * <p>
     * The default value is {@link DefaultPositionProvider#CLOSEST}.
     *
     * @return the default position provider
     */
    public DefaultPositionProvider getDefaultPosition() {
        return defaultPosition;
    }

    /**
     * Gets the behavior when an identifier can't be resolved as a element.
     * <p>
     * The default value is {@link UnresolvableIdentifierHandling#THROW}.
     *
     * @return the handling for an unresolvable identifier
     */
    public UnresolvableIdentifierHandling getUnresolvableIdentifierHandling() {
        return unresolvableIdentifierHandling;
    }

    public DuplicateIdentifierSelector getDuplicateIdentifierSelector() {
        return duplicateIdentifierSelector;
    }

    public static class Builder {
        private DefaultPositionProvider defaultPositionProvider;
        private UnresolvableIdentifierHandling unresolvableIdentifierHandling;
        private DuplicateIdentifierSelector duplicateIdentifierSelector;

        public Builder() {
            this(DEFAULT);
        }

        public Builder(SortingConfiguration configuration) {
            this.defaultPositionProvider = configuration.defaultPosition;
            this.unresolvableIdentifierHandling = configuration.unresolvableIdentifierHandling;
            this.duplicateIdentifierSelector = configuration.duplicateIdentifierSelector;
        }

        /**
         * Sets the default position strategy.
         *
         * @param defaultPositionProvider the default position
         * @return the same builder
         * @throws NullPointerException when the given value is null
         */
        public Builder defaultPosition(DefaultPositionProvider defaultPositionProvider) {
            this.defaultPositionProvider = Objects.requireNonNull(defaultPositionProvider);
            return this;
        }

        /**
         * Sets how unresolvable identifiers should be handled.
         *
         * @param unresolvableIdentifierHandling the handling for unresolvable identifiers
         * @return the same builder
         * @throws NullPointerException when the given value is null
         */
        public Builder unresolvableIdentifierHandling(UnresolvableIdentifierHandling unresolvableIdentifierHandling) {
            this.unresolvableIdentifierHandling = Objects.requireNonNull(unresolvableIdentifierHandling);
            return this;
        }

        public Builder duplicateIdentifierSelector(DuplicateIdentifierSelector duplicateIdentifierSelector) {
            this.duplicateIdentifierSelector = duplicateIdentifierSelector;
            return this;
        }

        /**
         * Builds a configuration using the values this builder has.
         *
         * @return a {@link SortingConfiguration} with the values of this builder
         */
        public SortingConfiguration build() {
            return new SortingConfiguration(defaultPositionProvider,
                    unresolvableIdentifierHandling,
                    duplicateIdentifierSelector);
        }
    }
}
