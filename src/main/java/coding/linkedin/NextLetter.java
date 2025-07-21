package coding.linkedin;

public class NextLetter {

    public char searchNextLetter(char[] letters, char key) {
        if (key < letters[0] || key > letters[letters.length - 1]) {
            return letters[0];
        }

        int left = 0, right = letters.length - 1;

        while (left <= right) {
            int mid = left + (right - left)/2;

            if (key <= letters[mid]) {
                right = mid - 1;
            } else {
                left = mid + 1;
            }
        }

        return letters[left % letters.length];
    }

    public Character searchPrevLetter(char[] arr, char key) {
        if (key > arr[arr.length - 1]) {
            return arr[arr.length - 1];
        }

        int left = 0, right = arr.length - 1;
        char res = arr[0];

        while (left <= right) {
            int mid = left + (right - left)/2;

            if (arr[mid] == key) {
                return key;
            } else if (arr[mid] < key) {
                res = arr[mid];
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }
        return res;
    }

    public static void main(String[] args) {
        NextLetter sol = new NextLetter();
//        System.out.println(sol.searchNextLetter(
//                new char[] { 'a', 'c', 'f', 'h' }, 'f'));
//        System.out.println(sol.searchNextLetter(
//                new char[] { 'a', 'c', 'f', 'h' }, 'b'));
//        System.out.println(sol.searchNextLetter(
//                new char[] { 'a', 'c', 'f', 'h' }, 'm'));
//        System.out.println(sol.searchNextLetter(
//                new char[] { 'a', 'c', 'f', 'h' }, 'h'));
//
//        System.out.println(sol.searchNextLetter(
//                new char[] { 'a', 'b' }, 'a'));
//
//
//

        System.out.println(sol.searchPrevLetter(
                new char[] { 'a', 'c', 'f', 'h' }, 'f'));
        System.out.println(sol.searchPrevLetter(
                new char[] { 'a', 'c', 'f', 'h' }, 'b'));
        System.out.println(sol.searchPrevLetter(
                new char[] { 'a', 'c', 'f', 'h' }, 'm'));
        System.out.println(sol.searchPrevLetter(
                new char[] { 'a', 'c', 'f', 'h' }, 'h'));

        System.out.println(sol.searchPrevLetter(
                new char[] { 'a', 'b' }, 'a'));

        System.out.println(sol.searchPrevLetter(
                new char[] { 'b', 'c' }, 'a'));
    }
}
