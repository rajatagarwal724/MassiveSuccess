package companies.coinbase;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.function.Predicate;

public class FilteredInterleavingIterator<T> implements Iterator<T> {
    private final Queue<Iterator<T>> queue;
    private final Predicate<T> filter;
    private T nextElement;
    private boolean nextElementComputed = false;

    public FilteredInterleavingIterator(List<Iterator<T>> iteratorList, Predicate<T> filter) {
        this.queue = new LinkedList<>();
        for (var iterator: iteratorList) {
            if (null != iterator && iterator.hasNext()) {
                queue.offer(iterator);
            }
        }
        this.filter = filter;
    }

    @Override
    public boolean hasNext() {
        if (nextElementComputed) {
            return true;
        }

        return findNextElement();
    }

    private boolean findNextElement() {
        if (queue.isEmpty()) {
            return false;
        }

        int size = queue.size();

        for (int i = 0; i < size; i++) {
            var iterator = queue.poll();

            while (iterator.hasNext()) {
                T next = iterator.next();

                if (filter.test(next)) {
                    nextElementComputed = true;
                    nextElement = next;

                    if (iterator.hasNext()) {
                        queue.offer(iterator);
                    }
                    return true;
                }
            }
        }
        return false;
    }

    public T next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        nextElementComputed = false;
        return nextElement;
    }

    public static void main(String[] args) {
        // Create some test data
        List<Integer> list1 = Arrays.asList(1, 4, 7, 10);
        List<Integer> list2 = Arrays.asList(2, 5, 8, 11);
        List<Integer> list3 = Arrays.asList(3, 6, 9, 12);

        List<Iterator<Integer>> iterators = Arrays.asList(
                list1.iterator(),
                list2.iterator(),
                list3.iterator()
        );

        // Example 1: Filter even numbers only
        System.out.println("Even numbers only:");
        FilteredInterleavingIterator<Integer> evenIter =
                new FilteredInterleavingIterator<>(iterators, x -> x % 2 == 0);

        while (evenIter.hasNext()) {
            System.out.print(evenIter.next() + " "); // Output: 4 2 6 10 8 12
        }

        // Example 2: Filter numbers > 5
        System.out.println("\nNumbers > 5:");
        List<Iterator<Integer>> iterators2 = Arrays.asList(
                Arrays.asList(1, 4, 7, 10).iterator(),
                Arrays.asList(2, 5, 8, 11).iterator(),
                Arrays.asList(3, 6, 9, 12).iterator()
        );

        FilteredInterleavingIterator<Integer> greaterThan5 =
                new FilteredInterleavingIterator<>(iterators2, x -> x > 5);

        while (greaterThan5.hasNext()) {
            System.out.print(greaterThan5.next() + " "); // Output: 7 8 6 10 11 9 12
        }
    }
}
