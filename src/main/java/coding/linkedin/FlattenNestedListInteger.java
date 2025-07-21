package coding.linkedin;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

public class FlattenNestedListInteger {

    // This is the interface that allows for creating nested lists.
    // You should not implement it, or speculate about its implementation
    public interface NestedInteger {

        // @return true if this NestedInteger holds a single integer, rather than a nested list.
        public boolean isInteger();

        // @return the single integer that this NestedInteger holds, if it holds a single integer
        // Return null if this NestedInteger holds a nested list
        public Integer getInteger();

        // @return the nested list that this NestedInteger holds, if it holds a nested list
        // Return empty list if this NestedInteger holds a single integer
        public List<NestedInteger> getList();
    }

    public class NestedIterator implements Iterator<Integer> {
        private Deque<NestedInteger> dequeue;
        public NestedIterator(List<NestedInteger> nestedList) {
            this.dequeue = new ArrayDeque<>(nestedList);
        }

        @Override
        public Integer next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }

            return dequeue.removeFirst().getInteger();
        }

        @Override
        public boolean hasNext() {
            while (!dequeue.isEmpty() && !dequeue.peekFirst().isInteger()) {
                NestedInteger nestedInteger = dequeue.removeFirst();
                List<NestedInteger> list = nestedInteger.getList();

                for (int i = list.size() - 1; i >= 0; i--) {
                    dequeue.addFirst(list.get(i));
                }
            }

            return !dequeue.isEmpty();
        }
    }
}



