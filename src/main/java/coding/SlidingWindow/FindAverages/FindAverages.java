package coding.SlidingWindow.FindAverages;

import java.util.Arrays;

public class FindAverages {

    public static double[] findAverages(int K, int[] arr) {
        double[] result = new double[arr.length - K + 1];
        int resultIndex = 0;
        int windowStart = 0, windowEnd = 0;
        double windowSum = 0;

        for (windowEnd = 0; windowEnd < arr.length; windowEnd++) {
            windowSum += arr[windowEnd];

            if ((windowEnd - windowStart + 1) >= K) {
                result[resultIndex++] = windowSum / K;
                windowSum -= arr[windowStart++];
            }
        }
        return result;
    }

    public static void main(String[] args) {
        System.out.println(Arrays.toString(findAverages(5, new int[]{1, 3, 2, 6, -1, 4, 1, 8, 2})));
    }
}
