package companies.doordash.repeat;

public class FairArray {

    public int waysToMakeFair(int[] nums) {
        int totalEvenSum = 0, totalOddSum = 0, res = 0;

        for (int i = 0; i < nums.length; i++) {
            if (i % 2 == 0) {
                totalEvenSum+= nums[i];
            } else {
                totalOddSum += nums[i];
            }
        }

        int oddSum = 0, evenSum = 0;

        for (int i = 0; i < nums.length; i++) {
            int remainingEvenSum = 0, remainingOddSum = 0;

            if (i % 2 == 0) {
                remainingEvenSum = evenSum + (totalOddSum - oddSum);
                remainingOddSum = oddSum + (totalEvenSum - evenSum - nums[i]);
            } else {
                remainingEvenSum = evenSum + (totalOddSum - oddSum - nums[i]);
                remainingOddSum = oddSum + (totalEvenSum - evenSum);
            }

            if (remainingEvenSum == remainingOddSum) {
                res++;
            }

            if (i % 2 == 0) {
                evenSum += nums[i];
            } else {
                oddSum += nums[i];
            }
        }

        return res;
    }

    public int waysToMakeFair_(int[] nums) {
        int res = 0;

        for (int i = 0; i < nums.length; i++) {
            if (isFair(nums, i)) {
                res++;
            }
        }
        return res;
    }

    private boolean isFair(int[] nums, int indexToRemove) {
        int evenSum = 0, oddSum = 0;

        for (int i = 0; i < nums.length; i++) {
            if (i == indexToRemove) {
                continue;
            }
            int index = i > indexToRemove ? i - 1 : i;

            if (index % 2 == 0) {
                evenSum += nums[i];
            } else {
                oddSum += nums[i];
            }
        }

        return evenSum == oddSum;
    }

    public static void main(String[] args) {
        var sol = new FairArray();
        System.out.println(
                sol.waysToMakeFair_(new int[] {2,1,6,4})
        );

        System.out.println(
                sol.waysToMakeFair_(new int[] {1,1,1})
        );

        System.out.println(
                sol.waysToMakeFair_(new int[] {1,2,3})
        );


        System.out.println(
                sol.waysToMakeFair(new int[] {2,1,6,4})
        );

        System.out.println(
                sol.waysToMakeFair(new int[] {1,1,1})
        );

        System.out.println(
                sol.waysToMakeFair(new int[] {1,2,3})
        );
    }
}
