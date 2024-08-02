package coding.TopologicalSort.ReconstructSequence;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.stream.Collectors;

public class ReconstructSequence {

    public boolean canConstruct(int[] originalSeq, int[][] sequences) {
        Map<Integer, Integer> inDegree = new HashMap<>();
        Map<Integer, List<Integer>> graph = new HashMap<>();
        List<Integer> sortedOrder = new ArrayList<>();

        for (int[] sequence: sequences) {
            for (int i = 0; i < sequence.length; i++) {
                inDegree.putIfAbsent(sequence[i], 0);
                graph.putIfAbsent(sequence[i], new ArrayList<>());
            }
        }

        if (inDegree.size() != originalSeq.length) {
            return false;
        }

        for (int[] sequence: sequences) {
            for (int i = 1; i < sequence.length; i++) {
                int parent = sequence[i - 1];
                int child = sequence[i];
                graph.get(parent).add(child);
                inDegree.put(child, inDegree.get(child) + 1);
            }
        }

        Queue<Integer> sources = inDegree.entrySet()
                .stream()
                .filter(entry -> entry.getValue() == 0)
                .map(entry -> entry.getKey())
                .collect(Collectors.toCollection(LinkedList::new));

        while (!sources.isEmpty()) {
            if (sources.size() > 1) {
                return false;
            }
            int vertex = sources.poll();
            if (originalSeq[sortedOrder.size()] != vertex) {
                return false;
            }
            sortedOrder.add(vertex);

            List<Integer> children = graph.get(vertex);
            for (int child: children) {
                inDegree.put(child, inDegree.get(child) - 1);
                if (inDegree.get(child) == 0) {
                    sources.add(child);
                }
            }
        }

        if (sortedOrder.size() == originalSeq.length) {
            return true;
        }
        return false;
    }

    public static void main(String[] args) {

    }
}
