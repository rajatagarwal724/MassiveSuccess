package coding.top75;

public class JumpGameI {

    public boolean canJump(int[] nums) {
        int maxReach = 0;

        for (int i = 0; i < nums.length; i++) {
            if (i > maxReach) {
                return false;
            }
            maxReach = Math.max(maxReach, (i + nums[i]));
        }
        return true;
    }

    public static void main(String[] args) {
        var sol = new JumpGameI();
        System.out.println(sol.canJump(new int[] {1, 2, 3, 4, 5}));
        System.out.println(sol.canJump(new int[] {2, 0, 2, 0, 1}));
        System.out.println(sol.canJump(new int[] {1, 0, 1, 0}));
    }
}
