package companies.doordash;

import java.util.*;

/**
 * DoorDash Interview Question: Shortest Path Edges
 * 
 * Given a graph, find which edges are part of ANY shortest path from node 1.
 * If there are multiple shortest paths to a node, an edge is considered part of 
 * a shortest path if it's used in ANY of the shortest paths to that node.
 * 
 * Algorithm:
 * 1. Run Dijkstra's algorithm to find shortest distances
 * 2. Track all parent nodes for each node (multiple parents possible)
 * 3. DFS to find all edges that are part of any shortest path
 * 4. Check if each input edge is part of any shortest path
 */
public class ShortestPathEdges {
    
    public List<String> findShortestPathEdges(int gNodes, int[] sources, int[] destinations, int[] weights) {
        // Build adjacency list and weight map
        Map<Integer, List<Integer>> graph = new HashMap<>();
        Map<String, Integer> weightBetween = new HashMap<>();
        
        for (int i = 0; i < sources.length; i++) {
            int s = sources[i];
            int d = destinations[i];
            int w = weights[i];
            
            graph.computeIfAbsent(s, k -> new ArrayList<>()).add(d);
            graph.computeIfAbsent(d, k -> new ArrayList<>()).add(s);
            
            weightBetween.put(s + "," + d, w);
            weightBetween.put(d + "," + s, w);
        }
        
        // Dijkstra's algorithm with multiple parents tracking
        Map<Integer, Set<Integer>> parents = new HashMap<>();
        Map<Integer, Integer> distances = new HashMap<>();
        
        // Initialize distances
        for (int i = 1; i <= gNodes; i++) {
            distances.put(i, Integer.MAX_VALUE);
            parents.put(i, new HashSet<>());
        }
        distances.put(1, 0);
        
        // Priority queue for Dijkstra's
        PriorityQueue<int[]> pq = new PriorityQueue<>((a, b) -> Integer.compare(a[1], b[1]));
        pq.offer(new int[]{1, 0});
        Set<Integer> relaxed = new HashSet<>();
        
        while (!pq.isEmpty()) {
            int[] current = pq.poll();
            int node = current[0];
            int dist = current[1];
            
            if (relaxed.contains(node)) continue;
            relaxed.add(node);
            
            // Relax all neighbors
            for (int neighbor : graph.getOrDefault(node, new ArrayList<>())) {
                if (relaxed.contains(neighbor)) continue;
                
                int weight = weightBetween.get(node + "," + neighbor);
                int newDist = dist + weight;
                
                if (newDist < distances.get(neighbor)) {
                    // Found shorter path - clear previous parents
                    distances.put(neighbor, newDist);
                    parents.get(neighbor).clear();
                    parents.get(neighbor).add(node);
                    pq.offer(new int[]{neighbor, newDist});
                } else if (newDist == distances.get(neighbor)) {
                    // Found equal length path - add to parents
                    parents.get(neighbor).add(node);
                }
            }
        }
        
        // Find all edges that are part of any shortest path
        Set<String> shortestEdges = new HashSet<>();
        Set<Integer> visited = new HashSet<>();
        dfs(1, parents, visited, shortestEdges);
        
        // Check each input edge
        List<String> result = new ArrayList<>();
        for (int i = 0; i < sources.length; i++) {
            int s = sources[i];
            int d = destinations[i];
            
            String edge1 = s + "," + d;
            String edge2 = d + "," + s;
            
            if (shortestEdges.contains(edge1) || shortestEdges.contains(edge2)) {
                result.add("YES");
            } else {
                result.add("NO");
            }
        }
        
        return result;
    }
    
    private void dfs(int node, Map<Integer, Set<Integer>> parents, Set<Integer> visited, Set<String> shortestEdges) {
        if (visited.contains(node)) return;
        visited.add(node);
        
        Set<Integer> nodeParents = parents.get(node);
        if (nodeParents != null) {
            for (int parent : new HashSet<>(nodeParents)) {
                // Add edge from node to parent
                shortestEdges.add(node + "," + parent);
                dfs(parent, parents, visited, shortestEdges);
            }
        }
    }
    
    // Alternative approach: Check each edge individually
    public List<String> findShortestPathEdgesAlternative(int gNodes, int[] sources, int[] destinations, int[] weights) {
        List<String> result = new ArrayList<>();
        
        for (int i = 0; i < sources.length; i++) {
            int s = sources[i];
            int d = destinations[i];
            int w = weights[i];
            
            // Check if this edge is part of any shortest path
            boolean isShortestPath = isEdgeInShortestPath(gNodes, sources, destinations, weights, s, d, w);
            result.add(isShortestPath ? "YES" : "NO");
        }
        
        return result;
    }
    
    private boolean isEdgeInShortestPath(int gNodes, int[] sources, int[] destinations, int[] weights, 
                                       int edgeSource, int edgeDest, int edgeWeight) {
        // Build graph without this edge
        Map<Integer, List<int[]>> graph = new HashMap<>();
        
        for (int i = 0; i < sources.length; i++) {
            int s = sources[i];
            int d = destinations[i];
            int w = weights[i];
            
            // Skip the edge we're testing
            if ((s == edgeSource && d == edgeDest) || (s == edgeDest && d == edgeSource)) {
                continue;
            }
            
            graph.computeIfAbsent(s, k -> new ArrayList<>()).add(new int[]{d, w});
            graph.computeIfAbsent(d, k -> new ArrayList<>()).add(new int[]{s, w});
        }
        
        // Find shortest distance without this edge
        Map<Integer, Integer> distancesWithoutEdge = dijkstra(graph, 1);
        
        // Find shortest distance with this edge
        Map<Integer, List<int[]>> graphWithEdge = new HashMap<>(graph);
        graphWithEdge.computeIfAbsent(edgeSource, k -> new ArrayList<>()).add(new int[]{edgeDest, edgeWeight});
        graphWithEdge.computeIfAbsent(edgeDest, k -> new ArrayList<>()).add(new int[]{edgeSource, edgeWeight});
        
        Map<Integer, Integer> distancesWithEdge = dijkstra(graphWithEdge, 1);
        
        // Check if this edge improves any shortest path
        for (int node = 1; node <= gNodes; node++) {
            int distWithout = distancesWithoutEdge.getOrDefault(node, Integer.MAX_VALUE);
            int distWith = distancesWithEdge.getOrDefault(node, Integer.MAX_VALUE);
            
            if (distWith < distWithout) {
                return true; // This edge improves shortest path
            }
        }
        
        return false;
    }
    
    private Map<Integer, Integer> dijkstra(Map<Integer, List<int[]>> graph, int start) {
        Map<Integer, Integer> distances = new HashMap<>();
        PriorityQueue<int[]> pq = new PriorityQueue<>((a, b) -> Integer.compare(a[1], b[1]));
        
        for (int node : graph.keySet()) {
            distances.put(node, Integer.MAX_VALUE);
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
    
    public static void main(String[] args) {
        ShortestPathEdges solution = new ShortestPathEdges();
        
        // Test case from the problem
        int gNodes = 5;
        int[] sources = {1, 2, 3, 4, 5, 1, 5};
        int[] destinations = {2, 3, 4, 5, 1, 3, 3};
        int[] weights = {1, 1, 1, 1, 3, 2, 1};
        
        System.out.println("=== DoorDash: Shortest Path Edges ===");
        System.out.println("Graph: " + gNodes + " nodes");
        System.out.println("Edges:");
        for (int i = 0; i < sources.length; i++) {
            System.out.printf("  %d -> %d (weight: %d)\n", sources[i], destinations[i], weights[i]);
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