package coding.warmup;

import java.util.HashMap;
import java.util.Map;

public class GoodPairs {

//    public int numGoodPairs(int[] nums) {
//        int pairCount = 0;
//
//        for (int i = 0; i < nums.length - 1; i++) {
//            for (int j = i + 1; j < nums.length; j++) {
//                if (nums[i] == nums[j]) {
//                    pairCount++;
//                }
//            }
//        }
//
//        return pairCount;
//    }

    public int numGoodPairs(int[] nums) {
        int pairCount = 0;
        Map<Integer, Integer> map = new HashMap<>();
        for (int num: nums) {
            map.put(num, map.getOrDefault(num, 0) + 1);
            pairCount = pairCount + map.get(num) - 1;
        }
        return pairCount;
    }

    public static void main(String[] args) {
        var sol = new GoodPairs();
        System.out.println(sol.numGoodPairs(new int[] {1,2,3,1,1,3}));
        System.out.println(sol.numGoodPairs(new int[] {1,1,1,1}));
        System.out.println(sol.numGoodPairs(new int[] {1,2,3}));
    }
}
