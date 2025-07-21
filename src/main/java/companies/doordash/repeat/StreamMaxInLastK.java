package companies.doordash.repeat;

import java.util.ArrayDeque;
import java.util.Deque;


/**
 * O(K) for each operation
 */
public class StreamMaxInLastK {

    class Node {
        int time;
        int value;

        public Node(int time, int value) {
            this.time = time;
            this.value = value;
        }
    }

    int X;
    Deque<Node> deque;
    public StreamMaxInLastK(int x) {
        X = x;
        deque = new ArrayDeque<>();
    }

    public void set_value(int time, int value) {
        while (!deque.isEmpty() && deque.peekFirst().time < (time - X)) {
            deque.pollFirst();
        }

        while (!deque.isEmpty() && value >= deque.peekLast().value) {
            deque.pollLast();
        }

        deque.offer(new Node(time, value));
    }

    public int max_value(int time) {
        while (!deque.isEmpty() && deque.peekFirst().time < (time - X)) {
            deque.pollFirst();
        }
        return deque.isEmpty() ? -1 : deque.peekFirst().value;
    }

    public static void main(String[] args) {
        var sol = new StreamMaxInLastK(5);
        sol.set_value(0, 5);
        sol.set_value(1, 6);
        sol.set_value(2, 4);
        sol.set_value(5, 5);
        sol.set_value(9, 19);
        sol.set_value(15, 4);
        sol.set_value(16, 25);
        sol.set_value(19, 6);
        sol.set_value(20, 4);

        System.out.println(sol.max_value(22));
    }
}
