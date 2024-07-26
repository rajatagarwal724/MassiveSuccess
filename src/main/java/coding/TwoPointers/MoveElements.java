package coding.TwoPointers;

public class MoveElements {

//    public int removeElements(int[] arr, int key) {
//        int nextNonDuplicate = 1;
//
//        for (int i = 1; i < arr.length; i++) {
//
//        }
//    }

    public int moveElements(int[] arr) {

        int nextNonDuplicate = 1;

        for (int i = 1; i < arr.length; i++) {

            if (arr[i] != arr[nextNonDuplicate - 1]) {
                arr[nextNonDuplicate++] = arr[i];
            }
        }

        return nextNonDuplicate;
    }

    public static void main(String[] args) {
        var solution = new MoveElements();

        System.out.println(solution.moveElements(new int[] {2, 3, 3, 3, 6, 9, 9}));
        System.out.println(solution.moveElements(new int[] {2, 2, 2, 11}));
    }
}
