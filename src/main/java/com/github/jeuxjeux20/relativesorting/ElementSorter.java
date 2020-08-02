package com.github.jeuxjeux20.relativesorting;

import com.github.jeuxjeux20.relativesorting.config.SortingConfiguration;
import com.github.jeuxjeux20.relativesorting.config.UnresolvableClassHandling;
import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableList;
import com.google.common.reflect.TypeToken;
import com.sun.istack.internal.Nullable;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DirectedAcyclicGraph;
import org.jgrapht.traverse.TopologicalOrderIterator;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        ImmutableBiMap<T, OrderedElement<T>> orderedElements = createOrderedElements(elements);

        return new SortContext(elements, orderedElements, configuration);
    }

    private ImmutableBiMap<T, OrderedElement<T>> createOrderedElements(Iterable<T> elements) {
        ImmutableBiMap.Builder<T, OrderedElement<T>> builder = ImmutableBiMap.builder();

        for (T element : elements) {
            OrderedElement<T> orderedElement = orderedElementFactory.create(element);

            if (orderedElement != null) {
                builder.put(element, orderedElement);
            }
        }

        try {
            return builder.build();
        } catch (IllegalArgumentException e) {
            throw new DuplicateIdentifiersException("Multiple elements have the same identifier.", e);
        }
    }

    private List<T> sortElements(SortContext context) {
        if (context.elements.size() < 2) {
            // There is no ordering to apply with under two elements.
            return context.elements;
        }

        Comparator<T> listPositionComparator = new ElementListPositionComparator(context);
        TopologicalOrderIterator<T, ElementEdge> topologicalIterator =
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
        OrderedElement<T> orderedElement = context.orderedElements.get(element);
        Order order = orderedElement.getOrder();

        int position = 0;

        if (order != null) {
            position = order.position();
        }

        if (position == 0) {
            position = context.configuration.getDefaultPosition().get(orderedElement);
        }
        return position;

    }

    private final class ElementGraph extends DirectedAcyclicGraph<T, ElementEdge> {
        ElementGraph(SortContext context) {
            super(null, () -> new ElementEdge(context), false);
        }
    }

    private final class ElementEdge extends DefaultEdge {
        private final SortContext context;

        private boolean isExplicit = true;

        public ElementEdge(SortContext context) {
            this.context = context;
        }

        public boolean isExplicit() {
            return isExplicit;
        }

        public void setExplicit(boolean explicit) {
            isExplicit = explicit;
        }

        @SuppressWarnings("unchecked")
        @Override
        public String toString() {
            return getIdentifier((T) getSource()) + " " +
                   getIdentifier((T) getTarget()) + (isExplicit ? " [explicit]" : " [implicit]");
        }

        private String getIdentifier(T element) {
            return context.orderedElements.get(element).getIdentifier().getRawType().getSimpleName();
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
        final BiMap<T, OrderedElement<T>> orderedElements;
        final Map<T, Integer> elementPositions;
        final SortingConfiguration configuration;
        ElementGraph graph;

        private SortContext(List<T> elements, BiMap<T, OrderedElement<T>> orderedElements,
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
            ElementGraph graph = new ElementGraph(context);

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
                        ElementEdge edge = graph.addEdge(lastImplicitElement, element);
                        edge.setExplicit(false);
                    }

                    lastImplicitElement = element;
                }
            }
        }

        private boolean isImplicitCandidate(T element) {
            OrderedElement<T> orderedElement = context.orderedElements.get(element);
            Order order = orderedElement.getOrder();

            return order == null ||
                   (order.before().length == 0 && order.after().length == 0 && order.position() == 0);
        }

        private void createExplicitEdges(ElementGraph graph) {
            for (T element : context.elements) {
                Order order = context.orderedElements.get(element).getOrder();
                if (order == null) {
                    continue;
                }

                for (Class<?> beforeClass : order.before()) {
                    T succeedingElement = findByClassOrHandle(beforeClass);
                    if (succeedingElement != null) {
                        addExplicitEdge(graph, element, succeedingElement);
                    }
                }

                for (Class<?> afterClass : order.after()) {
                    T precedingElement = findByClassOrHandle(afterClass);
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

        private @Nullable
        T findByClassOrHandle(Class<?> clazz) {
            T element = context.orderedElements.inverse()
                    .get(OrderedElement.equalityToken(TypeToken.of(clazz)));

            if (element != null) {
                return element;
            } else {
                UnresolvableClassHandling handling = context.configuration.getUnresolvableClassHandling();

                switch (handling) {
                    case THROW:
                        throw new UnableToResolveClassException(clazz);
                    case IGNORE:
                        return null;
                    default:
                        throw new UnsupportedOperationException("Unknown handling: " + handling);
                }
            }
        }

        private CycleDetectedException cycleDetectedException(T element, T otherElement) {
            OrderedElement<T> orderedElement = context.orderedElements.get(element);
            OrderedElement<T> otherOrderedElement = context.orderedElements.get(otherElement);

            return new CycleDetectedException(
                    "Cycle detected between " + orderedElement.getIdentifier() +
                    " and " + otherOrderedElement.getIdentifier() + ".");
        }
    }
}
