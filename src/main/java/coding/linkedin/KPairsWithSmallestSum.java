package coding.linkedin;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;

public class KPairsWithSmallestSum {

    class Node {
        int sum;
        int num1Idx;
        int num2Idx;

        public Node(int sum, int num1Idx, int num2Idx) {
            this.sum = sum;
            this.num1Idx = num1Idx;
            this.num2Idx = num2Idx;
        }
    }

    public List<List<Integer>> kSmallestPairs(int[] nums1, int[] nums2, int k) {
        Set<String> visited = new HashSet<>();
        PriorityQueue<int[]> minHeap = new PriorityQueue<>((n1, n2) -> n1[0] - n2[0]);
        List<List<Integer>> res = new ArrayList<>();

        int n1 = nums1.length, n2 = nums2.length;
        minHeap.offer(new int[] {nums1[0] + nums2[0], 0, 0});
        visited.add(0 + ":" + 0);

        while (k-- > 0 && !minHeap.isEmpty()) {
            var node = minHeap.poll();
            int i = node[1];
            int j = node[2];

            res.add(List.of(nums1[i], nums2[j]));

            if((i + 1) < n1 && !visited.contains((i + 1) + ":" + j)) {
                visited.add((i + 1) + ":" + j);
                minHeap.offer(new int[] {nums1[i + 1] + nums2[j], i + 1, j});
            }

            if((j + 1) < n2 && !visited.contains(i + ":" + (j + 1))) {
                visited.add(i + ":" + (j + 1));
                minHeap.offer(new int[] {nums1[i] + nums2[j + 1], i, j + 1});
            }

        }

        return res;
    }

    public static void main(String[] args) {
        var sol = new KPairsWithSmallestSum();
        System.out.println(
                sol.kSmallestPairs(
                        new int[] {1,7,11}, new int[] {2,4,6}, 3
                )
        );
    }
}
