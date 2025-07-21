package coding.linkedin;

import java.util.ArrayList;
import java.util.List;

public class LockerProblem {

    /**
     * Finds all lockers that will be open after n students have toggled them.
     * A locker is toggled by student i if the locker number is a multiple of i.
     * Lockers that are perfect squares will remain open.
     *
     * @param n The total number of students and lockers.
     * @return A list of integers representing the numbers of the open lockers.
     */
    public List<Integer> findOpenLockers(int n) {
        if (n < 0) {
            throw new IllegalArgumentException("Number of lockers cannot be negative.");
        }
        List<Integer> openLockers = new ArrayList<>();
        for (int i = 1; i * i <= n; i++) {
            openLockers.add(i * i);
        }
        return openLockers;
    }

    public static void main(String[] args) {
        LockerProblem solution = new LockerProblem();
        int numberOfLockers = 100;

        System.out.println("For " + numberOfLockers + " students and lockers:");
        List<Integer> openLockers = solution.findOpenLockers(numberOfLockers);
        System.out.println("Open lockers are: " + openLockers);

        numberOfLockers = 20;
        System.out.println("\nFor " + numberOfLockers + " students and lockers:");
        openLockers = solution.findOpenLockers(numberOfLockers);
        System.out.println("Open lockers are: " + openLockers);

        numberOfLockers = 1;
        System.out.println("\nFor " + numberOfLockers + " students and lockers:");
        openLockers = solution.findOpenLockers(numberOfLockers);
        System.out.println("Open lockers are: " + openLockers);

        numberOfLockers = 0;
        System.out.println("\nFor " + numberOfLockers + " students and lockers:");
        openLockers = solution.findOpenLockers(numberOfLockers);
        System.out.println("Open lockers are: " + openLockers);

        // Example with a larger number
        numberOfLockers = 1000;
        System.out.println("\nFor " + numberOfLockers + " students and lockers:");
        openLockers = solution.findOpenLockers(numberOfLockers);
        System.out.println("Open lockers are: " + openLockers);
    }
}
