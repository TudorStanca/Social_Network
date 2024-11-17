package domain;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Graph {

    private final int numberOfVertices;
    private final ArrayList<LinkedList<Integer>> adjacencyList;

    private final ArrayList<List<Integer>> components = new ArrayList<>();

    /**
     * Constructor for graph
     *
     * @param numberOfVertices The number of vertices in the graph
     */
    public Graph(int numberOfVertices) {
        this.numberOfVertices = numberOfVertices;
        adjacencyList = new ArrayList<>();

        for (int i = 0; i < numberOfVertices; i++)
            adjacencyList.add(new LinkedList<>());
    }

    public void addEdge(int u, int w) {
        adjacencyList.get(u).add(w);
        adjacencyList.get(w).add(u);
    }

    /**
     * DFS for determining all the components of the graph
     *
     * @param v                 Start vertex for DFS
     * @param visited           An array to verify if a vertex has been visited
     * @param componentVertices The vertexes that are in the component
     */
    private void DFSUtil(int v, boolean[] visited, List<Integer> componentVertices) {
        visited[v] = true;
        componentVertices.add(v);
        adjacencyList.get(v).stream().filter(x -> !visited[x]).forEach(x -> DFSUtil(x, visited, componentVertices));
    }

    /**
     * DFS for determining the longest path
     *
     * @param v       Start vertex for DFS
     * @param visited An array to verify if a vertex has been visited
     * @param length  The length of the current path
     * @return The length of the longest path found
     */
    private int DFSUtil(int v, boolean[] visited, int length) {
        visited[v] = true;

        for (int n : adjacencyList.get(v)) {
            if (!visited[n]) {
                length++;
                int i = DFSUtil(n, visited, length);
                return Math.max(i, length);
            }
        }
        return length;
    }

    private void DFS() {
        boolean[] visited = new boolean[numberOfVertices];

        for (int i = 0; i < numberOfVertices; i++) {
            List<Integer> componentVertices = new ArrayList<>();
            if (!visited[i]) {
                DFSUtil(i, visited, componentVertices);
                components.add(componentVertices);
            }
        }
    }

    /**
     * Determines the component with the longest path
     *
     * @return The component with the longest path
     */
    public List<Integer> biggestComponent() {
        DFS();
        int biggestCommunityIndex = 0, longestPath = 0;
        for (int i = 0; i < components.size(); i++) {
            int maxPath = 0;
            for (Integer vertex : components.get(i)) {
                boolean[] visited = new boolean[numberOfVertices];
                int path = DFSUtil(vertex, visited, 0);
                maxPath = Math.max(maxPath, path);
            }
            if (longestPath < maxPath) {
                biggestCommunityIndex = i;
                longestPath = maxPath;
            }
        }
        return components.get(biggestCommunityIndex);
    }

    /**
     * Determines the number of connected components
     *
     * @return The number of connected components
     */
    public int getNumberOfConnectedComponents() {
        DFS();
        return components.size();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        int i = 0;
        for (LinkedList<Integer> list : adjacencyList) {
            sb.append(i).append(": ");
            sb.append(list.toString()).append("\n");
            i++;
        }
        return sb.toString();
    }
}
