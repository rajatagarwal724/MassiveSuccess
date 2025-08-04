package companies.coinbase;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.UnaryOperator;

/**
 * An iterator that generates a sequence of values from a start value to an end value (exclusive)
 * using a provided stepper function.
 *
 * @param <T> The type of number for the range (e.g., Integer, Double).
 */
public class RangeIterator<T extends Comparable<T>> implements Iterator<T> {

    private T current;
    private final T end;
    private final UnaryOperator<T> stepper;

    /**
     * Constructs a generic RangeIterator.
     *
     * @param start   The starting value of the range (inclusive).
     * @param end     The ending value of the range (exclusive).
     * @param stepper A function that defines how to get from one value to the next.
     */
    public RangeIterator(T start, T end, UnaryOperator<T> stepper) {
        this.current = start;
        this.end = end;
        this.stepper = stepper;
    }

    /**
     * Returns {@code true} if the iteration has more elements.
     *
     * @return {@code true} if the current value is less than the end value.
     */
    @Override
    public boolean hasNext() {
        return current.compareTo(end) < 0;
    }

    /**
     * Returns the next value in the iteration.
     *
     * @return the next value in the iteration.
     * @throws NoSuchElementException if the iteration has no more elements.
     */
    @Override
    public T next() {
        if (!hasNext()) {
            throw new NoSuchElementException("No more elements in the range.");
        }
        T valueToReturn = current;
        current = stepper.apply(current);
        return valueToReturn;
    }

    public static void main(String[] args) {
        System.out.println("Testing Integer RangeIterator(1, 10, i -> i + 2):");
        RangeIterator<Integer> intIterator = new RangeIterator<>(1, 10, i -> i + 2);
        while (intIterator.hasNext()) {
            System.out.print(intIterator.next() + " ");
        }
        System.out.println("\nExpected: 1 3 5 7 9");

        System.out.println("\nTesting Double RangeIterator(0.5, 5.0, d -> d + 1.5):");
        RangeIterator<Double> doubleIterator = new RangeIterator<>(0.5, 5.0, d -> d + 1.5);
        while (doubleIterator.hasNext()) {
            System.out.print(doubleIterator.next() + " ");
        }
        System.out.println("\nExpected: 0.5 2.0 3.5");

        System.out.println("\nTesting with no elements RangeIterator(5, 5, i -> i + 1):");
        RangeIterator<Integer> emptyIt = new RangeIterator<>(5, 5, i -> i + 1);
        System.out.println("Has next? " + emptyIt.hasNext());
        System.out.println("Expected: false");
    }
}
