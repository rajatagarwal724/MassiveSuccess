package coding.TwoPointers;

import java.util.Arrays;

public class MakeSquares {

    public static int[] makeSquares(int[] arr) {
        int n = arr.length;
        int[] squares = new int[n];
        int left = 0, right = arr.length - 1;
        int index = squares.length - 1;

        while (left <= right) {
            double leftSquare = Math.pow(arr[left], 2);
            double rightSquare = Math.pow(arr[right], 2);

            if (leftSquare <= rightSquare) {
                squares[index--] = (int) rightSquare;
                right--;
            } else {
                squares[index--] = (int) leftSquare;
                left++;
            }
        }

        return squares;
    }

    public static void main(String[] args) {

        Arrays.stream(makeSquares(new int[] {-2, -1, 0, 2, 3})).boxed().forEach(System.out::println);
        System.out.println("###############");
        Arrays.stream(makeSquares(new int[] {-3, -1, 0, 1, 2})).boxed().forEach(System.out::println);
        System.out.println("###############");
        Arrays.stream(makeSquares(new int[] {-3, -2, -1})).boxed().forEach(System.out::println);
    }
}
