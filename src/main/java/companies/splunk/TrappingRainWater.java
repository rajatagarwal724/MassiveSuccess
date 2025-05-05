package companies.splunk;

import java.util.LinkedHashMap;

public class TrappingRainWater {

    public int trap(int[] height) {
        int left = 0, right = height.length - 1, left_max = 0, right_max = 0;
        int ans = 0;
        while (left < right) {
            if (height[left] < height[right]) {
                left_max = Math.max(left_max, height[left]);
                ans = ans + (left_max - height[left]);
                ++left;
            } else {
                right_max = Math.max(right_max, height[right]);
                ans = ans + (right_max - height[right]);
                --right;
            }
        }
        return ans;
    }

    public static void main(String[] args) {
        var sol = new TrappingRainWater();
        System.out.println(sol.trap(new int[] {0,1,0,2,1,0,1,3,2,1,2,1}));
        System.out.println(sol.trap(new int[] {4,2,0,3,2,5}));
    }
}
