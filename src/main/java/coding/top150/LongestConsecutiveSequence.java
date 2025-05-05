package coding.top150;

import lombok.extern.slf4j.Slf4j;

import java.util.HashSet;
import java.util.Set;

@Slf4j
public class LongestConsecutiveSequence {

    public int longestConsecutive(int[] nums) {
        if (nums.length == 0) {
            return 0;
        }

        Set<Integer> allUniqueNums = new HashSet<>();

        for (int i = 0; i < nums.length; i++) {
            allUniqueNums.add(nums[i]);
        }

        int longestStreak = 0;

        for (int num: nums) {
            if (!allUniqueNums.contains(num - 1)) {
                int currentNum = num;
                int currentStreak = 1;

                while (allUniqueNums.contains(currentNum + 1)) {
                    currentNum = currentNum + 1;
                    currentStreak++;
                    longestStreak = Math.max(longestStreak, currentStreak);
                }

            }
        }

        return longestStreak;
    }

    public static void main(String[] args) {

        System.out.println(3 + (1/2));

//        var sol = new LongestConsecutiveSequence();
//        System.out.println(sol.longestConsecutive(new int[] {100,4,200,1,3,2}));
//        System.out.println(sol.longestConsecutive(new int[] {0,3,7,2,5,8,4,6,0,1}));
//        System.out.println(sol.longestConsecutive(new int[] {1,0,1,2}));
    }
}
