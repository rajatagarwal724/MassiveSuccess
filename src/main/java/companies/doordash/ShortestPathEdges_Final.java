package companies.doordash;

import java.util.*;

/**
 * DoorDash Interview Question: Shortest Path Edges (Final Solution)
 * 
 * Problem: Given a graph, find which edges are part of ANY shortest path from node 1.
 * 
 * Algorithm:
 * 1. Run Dijkstra's to find shortest distances from node 1
 * 2. For each edge (u,v), check if it's part of any shortest path by verifying:
 *    - distance[1][u] + weight(u,v) = distance[1][v] OR
 *    - distance[1][v] + weight(v,u) = distance[1][u]
 * 3. If either condition is true, the edge is part of a shortest path
 */
public class ShortestPathEdges_Final {
    
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
        
        // Find shortest distances from node 1
        Map<Integer, Integer> distances = dijkstra(graph, 1, gNodes);
        
        // Check each edge
        List<String> result = new ArrayList<>();
        for (int i = 0; i < sources.length; i++) {
            int s = sources[i];
            int d = destinations[i];
            int w = weights[i];
            
            boolean isShortestPath = isEdgeInShortestPath(distances, s, d, w);
            result.add(isShortestPath ? "YES" : "NO");
        }
        
        return result;
    }
    
    private boolean isEdgeInShortestPath(Map<Integer, Integer> distances, int u, int v, int weight) {
        int distToU = distances.getOrDefault(u, Integer.MAX_VALUE);
        int distToV = distances.getOrDefault(v, Integer.MAX_VALUE);
        
        // Edge (u,v) is part of shortest path if:
        // distance[1][u] + weight(u,v) = distance[1][v] OR
        // distance[1][v] + weight(v,u) = distance[1][u]
        
        if (distToU != Integer.MAX_VALUE && distToV != Integer.MAX_VALUE) {
            // Check if edge is used in shortest path from 1 to v
            if (distToU + weight == distToV) {
                return true;
            }
            // Check if edge is used in shortest path from 1 to u
            if (distToV + weight == distToU) {
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
    
    public static void main(String[] args) {
        ShortestPathEdges_Final solution = new ShortestPathEdges_Final();
        
        // Test case from the problem
        int gNodes = 5;
        int[] sources = {1, 2, 3, 4, 5, 1, 5};
        int[] destinations = {2, 3, 4, 5, 1, 3, 3};
        int[] weights = {1, 1, 1, 1, 3, 2, 1};
        
        System.out.println("=== DoorDash: Shortest Path Edges (Final) ===");
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
            int s = sources[i];
            int d = destinations[i];
            int w = weights[i];
            String answer = result.get(i);
            
            System.out.printf("Edge %d->%d (weight %d): %s", s, d, w, answer);
            
            // Show reasoning
            int distToS = distances.get(s);
            int distToD = distances.get(d);
            System.out.printf(" [dist(1,%d)=%d, dist(1,%d)=%d]", s, distToS, d, distToD);
            
            if (answer.equals("YES")) {
                if (distToS + w == distToD) {
                    System.out.printf(" ✓ %d + %d = %d", distToS, w, distToD);
                } else if (distToD + w == distToS) {
                    System.out.printf(" ✓ %d + %d = %d", distToD, w, distToS);
                }
            }
            System.out.println();
        }
        
        // Expected results based on analysis
        System.out.println("\nExpected results:");
        System.out.println("Edge 1->2 (weight 1): YES (dist(1,1)=0, dist(1,2)=1, 0+1=1)");
        System.out.println("Edge 2->3 (weight 1): YES (dist(1,2)=1, dist(1,3)=2, 1+1=2)");
        System.out.println("Edge 3->4 (weight 1): YES (dist(1,3)=2, dist(1,4)=3, 2+1=3)");
        System.out.println("Edge 4->5 (weight 1): NO (dist(1,4)=3, dist(1,5)=3, 3+1≠3)");
        System.out.println("Edge 5->1 (weight 3): YES (dist(1,5)=3, dist(1,1)=0, 3+3≠0 but 0+3=3)");
        System.out.println("Edge 1->3 (weight 2): YES (dist(1,1)=0, dist(1,3)=2, 0+2=2)");
        System.out.println("Edge 5->3 (weight 1): YES (dist(1,5)=3, dist(1,3)=2, 3+1≠2 but 2+1=3)");
    }
} 