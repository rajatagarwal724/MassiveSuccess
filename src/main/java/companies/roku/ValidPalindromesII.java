package companies.roku;

public class ValidPalindromesII {

    private boolean checkPalindrome(String s, int i, int j) {
        while (i < j) {
            if (s.charAt(i) != s.charAt(j)) {
                return false;
            }
            i++;
            j--;
        }
        return true;
    }
    public boolean validPalindrome(String s) {
        int i = 0, j = s.length() - 1;
        while (i < j) {
            if (s.charAt(i) != s.charAt(j)) {
                // Found mismatch: try removing either character
                return checkPalindrome(s, i + 1, j) || checkPalindrome(s, i, j - 1);
            }
            i++;
            j--;
        }

        return true;
    }

    public static void main(String[] args) {
        ValidPalindromesII sol = new ValidPalindromesII();
        System.out.println(sol.validPalindrome("aba"));
        System.out.println(sol.validPalindrome("abca"));
        System.out.println(sol.validPalindrome("abc"));
    }
}
