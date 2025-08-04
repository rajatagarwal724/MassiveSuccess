package coding.top75.graphs;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class TownJudge {

    public int findTownJudgeEfficient(int N, int[][] trust) {
        if (trust.length < N - 1) {
            return -1;
        }

        int[] scores = new int[N + 1];

        for (int[] relation: trust) {
            scores[relation[0]]--;
            scores[relation[1]]++;
        }

        for (int i = 1; i < scores.length; i++) {
            if (scores[i] == N -1) {
                return i;
            }
        }

        return -1;
    }

    public int findTownJudge(int N, int[][] trust) {
        Map<Integer, Set<Integer>> adjacencyMap = new HashMap<>();

        for (int[] mapping: trust) {
            adjacencyMap.computeIfAbsent(mapping[0], s -> new HashSet<>()).add(mapping[1]);
        }

        int judge = 1;
        for (int i = 1; i <= N; i++) {
            if (i != judge) {
                if (adjacencyMap.containsKey(judge) && adjacencyMap.get(judge).contains(i)) {
                    judge = i;
                }
            }
        }

        if (adjacencyMap.containsKey(judge)) {
            return -1;
        }

        for (int i = 1; i <= N; i++) {
            if (i != judge) {
                var knowns = adjacencyMap.get(i);
                if (!knowns.contains(judge)) {
                    return -1;
                }
            }
        }

        return judge;
    }


    public static void main(String[] args) {
        var sol = new TownJudge();
//        System.out.println(sol.findTownJudge(4, new int[][]{
//                {1, 2},
//                {3, 2},
//                {4, 2}
//        }));
//
//        System.out.println(sol.findTownJudge(3, new int[][]{
//                {1, 3},
//                {2, 3}
//        }));
//
//        System.out.println(sol.findTownJudge(3, new int[][]{
//                {1, 3},
//                {2, 1},
//                {3, 1}
//        }));

        System.out.println(sol.findTownJudgeEfficient(4, new int[][]{
                {1, 2},
                {3, 2},
                {4, 2}
        }));

        System.out.println(sol.findTownJudgeEfficient(3, new int[][]{
                {1, 3},
                {2, 3}
        }));

        System.out.println(sol.findTownJudgeEfficient(3, new int[][]{
                {1, 3},
                {2, 1},
                {3, 1}
        }));
    }
}
