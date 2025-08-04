package coding.top75.graphs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

public class CloneGraph {

    public static class GraphNode {
        public int val;
        public List<GraphNode> neighbours;

        public GraphNode() {
            this.val = 0;
            this.neighbours = new ArrayList<>();
        }

        public GraphNode(int val, List<GraphNode> neighbours) {
            this.val = val;
            this.neighbours = neighbours;
        }

        public GraphNode(int val) {
            this.val = val;
            this.neighbours = new ArrayList<>();
        }
    }

    private Map<GraphNode, GraphNode> visited = new HashMap<>();

    public GraphNode cloneGraph(GraphNode node) {
        if (null == node) {
            return null;
        }

        if (visited.containsKey(node)) {
            return visited.get(node);
        }

        GraphNode cloneNode = new GraphNode(node.val);
        visited.put(node, cloneNode);

        for (GraphNode neighbour: node.neighbours) {
            cloneNode.neighbours.add(cloneGraph(neighbour));
        }

        return cloneNode;
    }

    // Utility function to print the structure of the graph
    public static void printGraph(GraphNode node) {
        Set<GraphNode> printed = new HashSet<>();
        Queue<GraphNode> queue = new LinkedList<>();
        queue.add(node);

        while(!queue.isEmpty()) {
            GraphNode curr = queue.poll();
            if(!printed.contains(curr)) {
                System.out.print(curr.val + "-->");
                for(GraphNode n : curr.neighbours) {
                    queue.add(n);
                    System.out.print(n.val + " ");
                }
                System.out.println();
                printed.add(curr);
            }
        }
    }

    public static void main(String[] args) {
        var sol = new CloneGraph();

        GraphNode node1 = new GraphNode(1);
        GraphNode node2 = new GraphNode(2);
        node1.neighbours = Arrays.asList(node2);
        node2.neighbours = Arrays.asList(node1);

        var cloneNode = sol.cloneGraph(node1);

        printGraph(cloneNode);

    }
}
