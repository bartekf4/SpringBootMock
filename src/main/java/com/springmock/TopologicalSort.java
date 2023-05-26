package com.springmock;

import com.springmock.exceptions.CyclicDependencyException;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


/**
 * <p>The {@link TopologicalSort} class is responsible for performing topological sort on a graph.</p>
 * The resulting sorted list will be in an order such that if bean A depends on bean B, then bean B will come before
 * bean A in the list.
 */
class TopologicalSort {


    /**
     * Performs depth-first search on the graph to construct the topologically sorted list of elements.
     *
     * @param graph            the graph of elements and their neighbors
     * @param sortedComponents the list to store the sorted elements
     * @param visited          a map indicating which elements have been visited
     * @param vertex           the current vertex being visited
     * @param recStack         a set used to keep track of visited nodes during the dfs traversal to detect cycles
     * @throws CyclicDependencyException if a cycle is detected in the graph
     */
    private static <T> void dfs(Map<T, Set<T>> graph, List<T> sortedComponents, Map<T, Boolean> visited, T vertex, Set<T> recStack) {
        if (recStack.contains(vertex)) {
            throw new CyclicDependencyException("Cyclic dependencies discovered");
        }
        recStack.add(vertex);
        for (T neighbor : graph.get(vertex)) {
            if (!visited.get(vertex)) {
                dfs(graph, sortedComponents, visited, neighbor, recStack);
            }
        }
        recStack.remove(vertex);
        visited.put(vertex, true);
        sortedComponents.add(vertex);

    }

    /**
     * The TopologicalSort class is responsible for performing topological sort on a graph of elements.
     *
     * @param <T>   The type of the elements in the graph
     * @param graph The graph on which te sorting will be performed. The {@code graph} is constructed in a way, that
     *              keys of the map represents the vertices. For given vertex X, the {@code Set<T>} represents the
     *              vertices such that for any vertex Y from {@code Set<T>}, the edge X->Y exists. Simply the
     *              neighbors.
     * @return The resulting sorted list of elements will be in an order such that if element A depends on element B,
     * then element B will come before element A in the list.
     * @throws CyclicDependencyException if a cycle is detected in the dependencies of the elements
     */
    public static <T> List<T> getSorted(@NotNull Map<T, Set<T>> graph) {
        List<T> sortedComponents = new ArrayList<>();
        Map<T, Boolean> visited = graph.keySet()
                .stream()
                .collect(Collectors.toMap(key -> key, key -> false));
        for (var vertex : graph.keySet()) {
            if (!visited.get(vertex)) {
                dfs(graph, sortedComponents, visited, vertex, new HashSet<>());
            }
        }
        return sortedComponents;
    }


}