import java.util.*;


/**
 * Kevin Bacon Graph Library
 * PS4
 * @author Godwin Kangor,winter 2024
 *@author Ahmed Elmi, winter 2024
 */
public class GraphLibrary {

    /**
     * BFS to find the shortest path tree for a current center of the universe.
     * Return a path tree as a Graph.
     *
     * @param g      graph to search on
     * @param source starting vertex of the search
     * @return a graph representing the shortest path tree
     */
    public static <V, E> Graph<V, E> bfs(Graph<V, E> g, V source) {
        Graph<V, E> tree = new AdjacencyMapGraph<>();
        Queue<V> queue = new ArrayDeque<>();

        if (!g.hasVertex(source)) return null;//return null if source does not exist

        queue.add(source);
        tree.insertVertex(source);

        while (!queue.isEmpty()) {
            V currentVertex = queue.remove();
            for (V neighbor : g.outNeighbors(currentVertex)) {
                if (!tree.hasVertex(neighbor)) {
                    queue.add(neighbor);
                    tree.insertVertex(neighbor);
                    tree.insertDirected(neighbor, currentVertex, g.getLabel(currentVertex, neighbor));
                }
            }
        }
        return tree;
    }

    /**
     * construct a path from the vertex back to the center of the universe.
     *
     * @param tree shortest path tree to use
     * @param v    vertex to construct the path for
     * @return list of vertices in the path, in order from v back to the root of the tree
     */

    public static <V, E> List<V> getPath(Graph<V, E> tree, V v) {
        // Initialize a list to store the shortest path.
        List<V> shortestPath = new ArrayList<>();

        // Return an empty list if the vertex is not in the graph.
        if (!tree.hasVertex(v)) {
            return shortestPath;
        } else {
            // Start with the given vertex.
            V currentVertex = v;

            // Check if the current vertex has no outgoing neighbors (end of path).
            if (tree.outNeighbors(currentVertex) == null) {
                return shortestPath;
            }
            if (tree.outNeighbors(currentVertex) != null) {
                // Iterate through the graph by following the outgoing neighbors.
                while (tree.outDegree(currentVertex) != 0) {
                    shortestPath.add(currentVertex);
                    // Move to the next vertex in the path.
                    currentVertex = tree.outNeighbors(currentVertex).iterator().next();
                }
                // Add the last vertex to the path.
                shortestPath.add(currentVertex);
            }
        }

        // Return the path found.
        return shortestPath;
    }


    /**
     * Given a graph and a subgraph, determine which vertices are in the graph but not the subgraph.
     *
     * @param graph    the original graph
     * @param subgraph the subgraph
     * @return set of vertices in the graph but not the subgraph
     */
    public static <V, E> Set<V> missingVertices(Graph<V, E> graph, Graph<V, E> subgraph) {
        Set<V> missing = new HashSet<>();//create empty Hashset
        if (graph.numVertices() == 0) {//if no vertices return the empty list
            return missing;
        }
        //iterates over every vertex in the main graph.
        for (V vertex : graph.vertices()) {
            if (!subgraph.hasVertex(vertex)) {//For each vertx checks if that is not present in the subgraph
                missing.add(vertex);//--->if missing add to the missing set
            }
        }
        return missing;
    }

    /**
     * calculate average separation of nodes in graph from a root
     *
     * @param tree
     * @param root
     * @param <V>
     * @param <E>
     * @return value of the average separation between vertices in map and a root
     */
    public static <V, E> double averageSeparation(Graph<V, E> tree, V root) {
        double averageHelper = 0;
        int length = 0;
        double average = 0;

        averageHelper = averageDistanceHelper(tree, root, length);

        //calculating and returning the average distance
        average = averageHelper / (double) (tree.numVertices() - 1);
        return average;
    }

    /**
     * helper method to calculate average separation
     *
     * @param tree
     * @param root
     * @param length
     * @param <V>
     * @param <E>
     * @return the recursive value of average separation
     */
    public static <V, E> double averageDistanceHelper(Graph<V, E> tree, V root, int length) {
        int avgDistance = length; //Keeps count of average separation

        //checks if current node has no neighbor
        if (tree.inNeighbors(root) != null) {
            for (V vertice : tree.inNeighbors(root)) {
                avgDistance += averageDistanceHelper(tree, vertice, length + 1);
            }
        }
        return avgDistance;
    }

    /**
     * Orders vertices in decreasing order by their in-degree
     *
     * @param g graph
     * @return list of vertices sorted by in-degree, decreasing (i.e., largest at index 0)
     */
    public static <V, E> List<V> verticesByInDegree(Graph<V, E> g) {
        Map<V, Integer> inDegree = new HashMap<>();//Vertex Key : Value in-degree
        for (V vertex : g.vertices()) {
            inDegree.put(vertex, g.inDegree(vertex));
        }

        List<V> verticeList = new ArrayList<>(inDegree.keySet());
        verticeList.sort((v1, v2) -> inDegree.get(v2) - inDegree.get(v1)); //sort in descending order of in-degree

        return verticeList;
    }

}

