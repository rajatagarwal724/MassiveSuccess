package companies.coinbase;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Queue;

public class InterleavingIterator<T> {

    Queue<Iterator<T>> queue;

    public InterleavingIterator(List<Iterator<T>> iteratorList) {
        this.queue = new LinkedList<>();
        for (var iterator: iteratorList) {
            if (iterator.hasNext()) {
                queue.offer(iterator);
            }
        }
    }

    public T getNext() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        var iterator = queue.poll();
        T result = iterator.next();

        if (iterator.hasNext()) {
            queue.offer(iterator);
        }

        return result;
    }

    public boolean hasNext() {
        return !queue.isEmpty();
    }

    public static void main(String[] args) {
        List<Integer> list1 = Arrays.asList(1, 4, 7);
        List<Integer> list2 = Arrays.asList(2, 5, 8, 9);
        List<Integer> list3 = Arrays.asList(3, 6);

        // Create list of iterators
        List<Iterator<Integer>> iterators = Arrays.asList(
                list1.iterator(),
                list2.iterator(),
                list3.iterator()
        );

        // Create interleaving iterator
        InterleavingIterator<Integer> interleavingIter =
                new InterleavingIterator<>(iterators);

        // Output: 1, 2, 3, 4, 5, 6, 7, 8, 9
        while (interleavingIter.hasNext()) {
            System.out.print(interleavingIter.getNext() + " ");
        }
    }
}
