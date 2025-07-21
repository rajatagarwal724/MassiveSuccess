package coding.linkedin;

public class ValidPalindrome {
    public boolean isPalindrome(String s) {
        char[] arr = s.toLowerCase().toCharArray();

        int left = 0, right = arr.length - 1;

        while (left < right) {
            while (left < right && !Character.isLetterOrDigit(arr[left])) {
                left++;
            }

            while (left < right && !Character.isLetterOrDigit(arr[right])) {
                right--;
            }

            if (left < right && arr[left] != arr[right]) {
                return false;
            }

            left++;
            right--;
        }

        return true;
    }

    public static void main(String[] args) {
        var sol = new ValidPalindrome();
        System.out.println(sol.isPalindrome("A man, a plan, a canal, Panama!"));
        System.out.println(sol.isPalindrome("Was it a car or a cat I saw?"));
    }
}
