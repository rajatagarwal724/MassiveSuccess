package coding.SlidingWindow.LongestContiguousSubarrayWith1;

public class LongestSubArrayWithOnes {

    public int findLength(int[] arr, int k) {
        int plusOnes = 0, start = 0, maxLength = Integer.MIN_VALUE;

        for (int end = 0; end < arr.length; end++) {
            if (arr[end] == 1) {
                plusOnes++;
            }

            if ((end - start + 1 - plusOnes) > k) {
                int leftElem = arr[start];
                if (leftElem == 1) {
                    plusOnes--;
                }
                start++;
            }
            maxLength = Math.max(maxLength, end - start + 1);
        }
        return maxLength;
    }

    public static void main(String[] args) {
        var sol = new LongestSubArrayWithOnes();
        System.out.println(sol.findLength(new int[] {0, 1, 1, 0, 0, 0, 1, 1, 0, 1, 1}, 2));
        System.out.println(sol.findLength(new int[] {0, 1, 0, 0, 1, 1, 0, 1, 1, 0, 0, 1, 1}, 3));
        System.out.println(sol.findLength(new int[] {1, 0, 0, 1, 1, 0, 1, 1}, 2));
    }
}
