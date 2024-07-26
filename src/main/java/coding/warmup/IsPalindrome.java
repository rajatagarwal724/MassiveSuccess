package coding.warmup;

public class IsPalindrome {
    public boolean isPalindrome(String s) {
        char[] sentence = s.toUpperCase().toCharArray();
        StringBuilder word = new StringBuilder();
        for (char ch: sentence) {
            if (Character.isLetterOrDigit(ch)) {
                word.append(ch);
            }
        }

        int left = 0, right = word.length() - 1;

        while (left < right) {
            if (word.charAt(left) != word.charAt(right)) {
                return false;
            }
            left++;
            right--;
        }

        return true;
    }

    public static void main(String[] args) {
        var sol = new IsPalindrome();
        System.out.println(sol.isPalindrome("A man, a plan, a canal, Panama!"));
        System.out.println(sol.isPalindrome("Was it a car or a cat I saw?"));
    }
}
