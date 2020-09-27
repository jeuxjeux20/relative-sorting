package com.github.jeuxjeux20.relativesorting;

import com.github.jeuxjeux20.relativesorting.config.SortingConfiguration;
import com.github.jeuxjeux20.relativesorting.config.UnresolvableIdentifierHandling;
import com.google.common.collect.*;
import org.jetbrains.annotations.Nullable;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DirectedAcyclicGraph;
import org.jgrapht.traverse.TopologicalOrderIterator;

import java.util.*;

public class ElementSorter<T> {
    private final OrderedElementFactory<T> orderedElementFactory;

    public ElementSorter(OrderedElementFactory<T> orderedElementFactory) {
        this.orderedElementFactory = orderedElementFactory;
    }

    public List<T> sort(List<T> elements) {
        return sort(elements, SortingConfiguration.DEFAULT);
    }

    public List<T> sort(List<T> elements, SortingConfiguration configuration) {
        if (elements.isEmpty()) {
            return elements;
        }

        SortContext context = createSortContext(elements, configuration);

        ElementGraphFactory graphFactory = new ElementGraphFactory(context);
        context.graph = graphFactory.createGraph();

        return sortElements(context);
    }

    private SortContext createSortContext(List<T> elements, SortingConfiguration configuration) {
        ImmutableBiMap<T, OrderedElement<? extends T>> orderedElements = createOrderedElements(elements, configuration);

        return new SortContext(elements, orderedElements, configuration);
    }

    private ImmutableBiMap<T, OrderedElement<? extends T>> createOrderedElements(
            Iterable<T> elements, SortingConfiguration configuration) {
        Multimap<T, OrderedElement<? extends T>> multimap = ArrayListMultimap.create();

        // Gather all elements into a Multimap.
        for (T element : elements) {
            OrderedElement<? extends T> orderedElement = orderedElementFactory.create(element);

            if (orderedElement != null) {
                multimap.put(element, orderedElement);
            }
        }

        // Remove all duplicate identifiers
        for (T key : multimap.keySet()) {
            Collection<OrderedElement<? extends T>> values = multimap.get(key);
            if (values.size() > 1) {
                OrderedElement<? extends T> picked = configuration.getDuplicateIdentifierSelector().select(values);

                values.clear();
                values.add(picked);
            }
        }

        // Flatten all of this into a BiMap.
        ImmutableBiMap.Builder<T, OrderedElement<? extends T>> builder = ImmutableBiMap.builder();
        for (Map.Entry<T, OrderedElement<? extends T>> entry : multimap.entries()) {
            builder.put(entry.getKey(), entry.getValue());
        }

        return builder.build();
    }

    private List<T> sortElements(SortContext context) {
        if (context.elements.size() < 2) {
            // There is no ordering to apply with under two elements.
            return context.elements;
        }

        Comparator<T> listPositionComparator = new ElementListPositionComparator(context);
        TopologicalOrderIterator<T, DefaultEdge> topologicalIterator =
                new TopologicalOrderIterator<>(context.graph, (a, b) -> {
                    int aPosition = findPosition(context, a);
                    int bPosition = findPosition(context, b);

                    int positionComparison = Integer.compare(aPosition, bPosition);
                    if (positionComparison != 0) {
                        return positionComparison;
                    } else {
                        return listPositionComparator.compare(a, b);
                    }
                });

        return ImmutableList.copyOf(topologicalIterator);
    }

    private int findPosition(SortContext context, T element) {
        OrderedElement<? extends T> orderedElement = context.orderedElements.get(element);
        OrderConstraints order = orderedElement.getOrderConstraints();

        int position = 0;

        if (order != null) {
            position = order.getPosition();
        }

        if (position == 0) {
            position = context.configuration.getDefaultPosition().get(orderedElement);
        }
        return position;

    }

    private final class ElementGraph extends DirectedAcyclicGraph<T, DefaultEdge> {
        ElementGraph() {
            super(DefaultEdge.class);
        }
    }

    private final class ElementListPositionComparator implements Comparator<T> {
        private final SortContext context;

        public ElementListPositionComparator(SortContext context) {
            this.context = context;
        }

        @Override
        public int compare(T a, T b) {
            return context.elementPositions.get(a).compareTo(context.elementPositions.get(b));
        }
    }

    private final class SortContext {
        final List<T> elements;
        final BiMap<T, OrderedElement<? extends T>> orderedElements;
        final Map<T, Integer> elementPositions;
        final SortingConfiguration configuration;
        ElementGraph graph;

        private SortContext(List<T> elements, BiMap<T, OrderedElement<? extends T>> orderedElements,
                            SortingConfiguration configuration) {
            this.elements = elements;
            this.orderedElements = orderedElements;

            this.elementPositions = createElementPositions(elements);
            this.configuration = configuration;
        }

        private Map<T, Integer> createElementPositions(List<T> elements) {
            Map<T, Integer> map = new HashMap<>();
            for (int i = 0; i < elements.size(); i++) {
                map.put(elements.get(i), i);
            }

            return map;
        }
    }

    @SuppressWarnings("UnstableApiUsage")
    private final class ElementGraphFactory {
        private final SortContext context;

        ElementGraphFactory(SortContext context) {
            this.context = context;
        }

        public ElementGraph createGraph() {
            ElementGraph graph = new ElementGraph();

            addVertexes(graph);
            createImplicitEdges(graph);
            createExplicitEdges(graph);

            return graph;
        }

        private void addVertexes(ElementGraph graph) {
            for (T element : context.elements) {
                graph.addVertex(element);
            }
        }

        private void createImplicitEdges(ElementGraph graph) {
            List<T> elements = context.elements;

            T lastImplicitElement = null;
            for (T element : elements) {
                if (isImplicitCandidate(element)) {
                    if (lastImplicitElement != null) {
                        graph.addEdge(lastImplicitElement, element);
                    }

                    lastImplicitElement = element;
                }
            }
        }

        private boolean isImplicitCandidate(T element) {
            OrderedElement<? extends T> orderedElement = context.orderedElements.get(element);
            OrderConstraints order = orderedElement.getOrderConstraints();

            return order == null ||
                   (order.getBefore().isEmpty() && order.getAfter().isEmpty() && order.getPosition() == 0);
        }

        private void createExplicitEdges(ElementGraph graph) {
            for (T element : context.elements) {
                OrderConstraints order = context.orderedElements.get(element).getOrderConstraints();
                if (order == null) {
                    continue;
                }

                for (Object before : order.getBefore()) {
                    T succeedingElement = findOrHandle(before);
                    if (succeedingElement != null) {
                        addExplicitEdge(graph, element, succeedingElement);
                    }
                }

                for (Object after : order.getAfter()) {
                    T precedingElement = findOrHandle(after);
                    if (precedingElement != null) {
                        addExplicitEdge(graph, precedingElement, element);
                    }
                }
            }
        }

        private void addExplicitEdge(ElementGraph graph, T element, T succeedingElement) {
            try {
                graph.addEdge(element, succeedingElement);
            } catch (IllegalArgumentException e) {
                throw cycleDetectedException(element, succeedingElement);
            }
        }

        private @Nullable T findOrHandle(Object identifier) {
            T element = context.orderedElements.inverse()
                    .get(OrderedElement.equalityToken(identifier));

            if (element != null) {
                return element;
            } else {
                UnresolvableIdentifierHandling handling = context.configuration.getUnresolvableIdentifierHandling();

                switch (handling) {
                    case THROW:
                        throw new UnableToResolveElementException(identifier);
                    case IGNORE:
                        return null;
                    default:
                        throw new UnsupportedOperationException("Unknown handling: " + handling);
                }
            }
        }

        private CycleDetectedException cycleDetectedException(T element, T otherElement) {
            OrderedElement<? extends T> orderedElement = context.orderedElements.get(element);
            OrderedElement<? extends T> otherOrderedElement = context.orderedElements.get(otherElement);

            return new CycleDetectedException(
                    "Cycle detected between " + orderedElement.getIdentifier() +
                    " and " + otherOrderedElement.getIdentifier() + ".");
        }
    }
}
