package coding.linkedin;

public class CanPlaceFlowers {

    public boolean canPlaceFlowers(int[] flowerbed, int n) {
        int count = 0;

        for (int i = 0; i < flowerbed.length; i++) {
            boolean leftPlotEmpty = false;
            boolean rightPlotEmpty = false;
            if (flowerbed[i] == 0) {
                if (i == 0 || flowerbed[i - 1] == 0) {
                    leftPlotEmpty = true;
                }

                if (i == (flowerbed.length - 1) || flowerbed[i + 1] == 0) {
                    rightPlotEmpty = true;
                }

                if (leftPlotEmpty && rightPlotEmpty) {
                    flowerbed[i] = 1;
                    count++;

                    if (count >= n) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static void main(String[] args) {
        var sol = new CanPlaceFlowers();
        System.out.println(sol.canPlaceFlowers(new int[] {1,0,0,0,1}, 1));
    }
}
