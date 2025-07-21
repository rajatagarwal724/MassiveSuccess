package companies.doordash;

import java.util.*;

/**
 * DoorDash Interview Question: Shortest Path Edges (Corrected)
 * 
 * Given a graph, find which edges are part of ANY shortest path from node 1.
 * 
 * Key Insight: An edge (u,v) is part of a shortest path if:
 * distance[1][u] + weight(u,v) + distance[v][target] = shortest_distance[1][target]
 * for some target node.
 */
public class ShortestPathEdges_Corrected {
    
    public List<String> findShortestPathEdges(int gNodes, int[] sources, int[] destinations, int[] weights) {
        // Build graph
        Map<Integer, List<int[]>> graph = new HashMap<>();
        for (int i = 0; i < sources.length; i++) {
            int s = sources[i];
            int d = destinations[i];
            int w = weights[i];
            
            graph.computeIfAbsent(s, k -> new ArrayList<>()).add(new int[]{d, w});
            graph.computeIfAbsent(d, k -> new ArrayList<>()).add(new int[]{s, w});
        }
        
        // Find shortest distances from node 1 to all nodes
        Map<Integer, Integer> distances = dijkstra(graph, 1, gNodes);
        
        // Check each edge
        List<String> result = new ArrayList<>();
        for (int i = 0; i < sources.length; i++) {
            int s = sources[i];
            int d = destinations[i];
            int w = weights[i];
            
            boolean isShortestPath = isEdgeInShortestPath(graph, distances, s, d, w, gNodes);
            result.add(isShortestPath ? "YES" : "NO");
        }
        
        return result;
    }
    
    private boolean isEdgeInShortestPath(Map<Integer, List<int[]>> graph, Map<Integer, Integer> distances, 
                                       int edgeSource, int edgeDest, int edgeWeight, int gNodes) {
        // Check if this edge is part of any shortest path
        // An edge (u,v) is part of a shortest path if:
        // distance[1][u] + weight(u,v) = distance[1][v] OR distance[1][v] + weight(v,u) = distance[1][u]
        
        int distToSource = distances.getOrDefault(edgeSource, Integer.MAX_VALUE);
        int distToDest = distances.getOrDefault(edgeDest, Integer.MAX_VALUE);
        
        // Check if edge is part of shortest path from 1 to source
        if (distToSource != Integer.MAX_VALUE && distToDest != Integer.MAX_VALUE) {
            // Check if edge is used in shortest path from 1 to source
            if (distToDest + edgeWeight == distToSource) {
                return true;
            }
            // Check if edge is used in shortest path from 1 to dest
            if (distToSource + edgeWeight == distToDest) {
                return true;
            }
        }
        
        return false;
    }
    
    private Map<Integer, Integer> dijkstra(Map<Integer, List<int[]>> graph, int start, int gNodes) {
        Map<Integer, Integer> distances = new HashMap<>();
        PriorityQueue<int[]> pq = new PriorityQueue<>((a, b) -> Integer.compare(a[1], b[1]));
        
        // Initialize distances
        for (int i = 1; i <= gNodes; i++) {
            distances.put(i, Integer.MAX_VALUE);
        }
        distances.put(start, 0);
        pq.offer(new int[]{start, 0});
        
        while (!pq.isEmpty()) {
            int[] current = pq.poll();
            int node = current[0];
            int dist = current[1];
            
            if (dist > distances.get(node)) continue;
            
            for (int[] neighbor : graph.getOrDefault(node, new ArrayList<>())) {
                int nextNode = neighbor[0];
                int weight = neighbor[1];
                int newDist = dist + weight;
                
                if (newDist < distances.get(nextNode)) {
                    distances.put(nextNode, newDist);
                    pq.offer(new int[]{nextNode, newDist});
                }
            }
        }
        
        return distances;
    }
    
    // Alternative approach: Check if removing edge increases shortest path distance
    public List<String> findShortestPathEdgesAlternative(int gNodes, int[] sources, int[] destinations, int[] weights) {
        List<String> result = new ArrayList<>();
        
        for (int i = 0; i < sources.length; i++) {
            int s = sources[i];
            int d = destinations[i];
            int w = weights[i];
            
            boolean isShortestPath = isEdgeCritical(gNodes, sources, destinations, weights, s, d, w);
            result.add(isShortestPath ? "YES" : "NO");
        }
        
        return result;
    }
    
    private boolean isEdgeCritical(int gNodes, int[] sources, int[] destinations, int[] weights, 
                                 int edgeSource, int edgeDest, int edgeWeight) {
        // Build graph without this edge
        Map<Integer, List<int[]>> graphWithoutEdge = new HashMap<>();
        
        for (int i = 0; i < sources.length; i++) {
            int s = sources[i];
            int d = destinations[i];
            int w = weights[i];
            
            // Skip the edge we're testing
            if ((s == edgeSource && d == edgeDest) || (s == edgeDest && d == edgeSource)) {
                continue;
            }
            
            graphWithoutEdge.computeIfAbsent(s, k -> new ArrayList<>()).add(new int[]{d, w});
            graphWithoutEdge.computeIfAbsent(d, k -> new ArrayList<>()).add(new int[]{s, w});
        }
        
        // Find shortest distances without this edge
        Map<Integer, Integer> distancesWithoutEdge = dijkstra(graphWithoutEdge, 1, gNodes);
        
        // Build graph with this edge
        Map<Integer, List<int[]>> graphWithEdge = new HashMap<>(graphWithoutEdge);
        graphWithEdge.computeIfAbsent(edgeSource, k -> new ArrayList<>()).add(new int[]{edgeDest, edgeWeight});
        graphWithEdge.computeIfAbsent(edgeDest, k -> new ArrayList<>()).add(new int[]{edgeSource, edgeWeight});
        
        // Find shortest distances with this edge
        Map<Integer, Integer> distancesWithEdge = dijkstra(graphWithEdge, 1, gNodes);
        
        // Check if this edge improves any shortest path
        for (int node = 1; node <= gNodes; node++) {
            int distWithout = distancesWithoutEdge.getOrDefault(node, Integer.MAX_VALUE);
            int distWith = distancesWithEdge.getOrDefault(node, Integer.MAX_VALUE);
            
            if (distWith < distWithout) {
                return true; // This edge is critical for shortest path
            }
        }
        
        return false;
    }
    
    public static void main(String[] args) {
        ShortestPathEdges_Corrected solution = new ShortestPathEdges_Corrected();
        
        // Test case from the problem
        int gNodes = 5;
        int[] sources = {1, 2, 3, 4, 5, 1, 5};
        int[] destinations = {2, 3, 4, 5, 1, 3, 3};
        int[] weights = {1, 1, 1, 1, 3, 2, 1};
        
        System.out.println("=== DoorDash: Shortest Path Edges (Corrected) ===");
        System.out.println("Graph: " + gNodes + " nodes");
        System.out.println("Edges:");
        for (int i = 0; i < sources.length; i++) {
            System.out.printf("  %d -> %d (weight: %d)\n", sources[i], destinations[i], weights[i]);
        }
        
        // Build and display the graph structure
        System.out.println("\nGraph structure:");
        Map<Integer, List<int[]>> graph = new HashMap<>();
        for (int i = 0; i < sources.length; i++) {
            int s = sources[i];
            int d = destinations[i];
            int w = weights[i];
            
            graph.computeIfAbsent(s, k -> new ArrayList<>()).add(new int[]{d, w});
            graph.computeIfAbsent(d, k -> new ArrayList<>()).add(new int[]{s, w});
        }
        
        for (int node = 1; node <= gNodes; node++) {
            System.out.printf("Node %d: ", node);
            for (int[] neighbor : graph.getOrDefault(node, new ArrayList<>())) {
                System.out.printf("->%d(w=%d) ", neighbor[0], neighbor[1]);
            }
            System.out.println();
        }
        
        // Find shortest distances
        Map<Integer, Integer> distances = solution.dijkstra(graph, 1, gNodes);
        System.out.println("\nShortest distances from node 1:");
        for (int node = 1; node <= gNodes; node++) {
            System.out.printf("  Node %d: %d\n", node, distances.get(node));
        }
        
        List<String> result = solution.findShortestPathEdges(gNodes, sources, destinations, weights);
        
        System.out.println("\nResults:");
        for (int i = 0; i < sources.length; i++) {
            System.out.printf("Edge %d->%d (weight %d): %s\n", 
                            sources[i], destinations[i], weights[i], result.get(i));
        }
        
        // Test alternative approach
        List<String> resultAlt = solution.findShortestPathEdgesAlternative(gNodes, sources, destinations, weights);
        System.out.println("\nAlternative approach results:");
        for (int i = 0; i < sources.length; i++) {
            System.out.printf("Edge %d->%d (weight %d): %s\n", 
                            sources[i], destinations[i], weights[i], resultAlt.get(i));
        }
        
        System.out.println("\nResults match: " + result.equals(resultAlt));
    }
} 