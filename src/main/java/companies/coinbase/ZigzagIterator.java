package companies.coinbase;


import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class ZigzagIterator {

    static class Pair<L, R>  {
        L left;
        R right;

        public Pair(L left, R right) {
            this.left = left;
            this.right = right;
        }

        public static <L, R> Pair<L, R> of(L left, R right) {
            return new Pair<>(left, right);
        }

        public L getLeft() {
            return left;
        }

        public R getRight() {
            return right;
        }
    }

    List<List<Integer>> allLists = new ArrayList<>();
    Queue<Pair<Integer, Integer>> indexQueue = new LinkedList<>();
    public ZigzagIterator(List<Integer> v1, List<Integer> v2) {
        allLists.add(v1);
        allLists.add(v2);
        for (int i = 0; i < allLists.size(); i++) {
            if (allLists.get(i).size() > 0) {
                indexQueue.offer(Pair.of(i, 0));
            }
        }
    }

    public int next() {
        var pair = indexQueue.poll();
        int allListIdx = pair.getLeft();
        int withinListIdx = pair.getRight();
        if ((withinListIdx + 1) < allLists.get(allListIdx).size()) {
            indexQueue.offer(Pair.of(allListIdx, withinListIdx + 1));
        }
        return allLists.get(allListIdx).get(withinListIdx);
    }

    public boolean hasNext() {
        return !indexQueue.isEmpty();
    }

    public static void main(String[] args) {
        var sol = new ZigzagIterator(List.of(1,2), List.of(3,4,5,6));
        while (sol.hasNext()) {
            System.out.println(sol.next());
        }
        System.out.println("^^^^^^^^");
        sol = new ZigzagIterator(List.of(), List.of(1));
        while (sol.hasNext()) {
            System.out.println(sol.next());
        }
    }
}
