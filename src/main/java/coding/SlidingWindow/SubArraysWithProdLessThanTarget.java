package coding.SlidingWindow;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class SubArraysWithProdLessThanTarget {

    public List<List<Integer>> findSubarrays(int[] arr, int target) {
        List<List<Integer>> result = new ArrayList<>();
        int right = 0, left = 0;
        double product = 1;

        for (right = 0; right < arr.length; right++) {
            product = product * arr[right];

            while (product >= target && left < arr.length) {
                product = product / arr[left];
                left++;
            }

            List<Integer> tempList = new LinkedList<>();
            for (int i = right; i >= left; i--) {
                tempList.add(0, arr[i]);
                result.add(new ArrayList<>(tempList));
            }
        }

        return result;
    }


    public static void main(String[] args) {
        var sol = new SubArraysWithProdLessThanTarget();
        System.out.println(StringUtils.join(sol.findSubarrays(new int[] {2, 5, 3, 10}, 30)));
    }
}
