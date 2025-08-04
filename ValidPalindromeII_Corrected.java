public class ValidPalindromeII_Corrected {
    
    /**
     * Check if a substring is a palindrome
     */
    private boolean isPalindrome(String s, int left, int right) {
        while (left < right) {
            if (s.charAt(left) != s.charAt(right)) {
                return false;
            }
            left++;
            right--;
        }
        return true;
    }
    
    /**
     * Valid Palindrome II: Can remove at most one character to make palindrome
     */
    public boolean validPalindrome(String s) {
        int left = 0, right = s.length() - 1;
        
        while (left < right) {
            if (s.charAt(left) != s.charAt(right)) {
                // Found mismatch: try removing either left or right character
                return isPalindrome(s, left + 1, right) ||  // Skip left char
                       isPalindrome(s, left, right - 1);   // Skip right char
            }
            left++;
            right--;
        }
        
        // No mismatches found - already a palindrome
        return true;
    }
    
    /**
     * Alternative approach: Track if we've already used our "one removal"
     */
    public boolean validPalindromeAlternative(String s) {
        return canBePalindrome(s, 0, s.length() - 1, false);
    }
    
    private boolean canBePalindrome(String s, int left, int right, boolean deletedOne) {
        while (left < right) {
            if (s.charAt(left) != s.charAt(right)) {
                if (deletedOne) {
                    // Already used our one deletion
                    return false;
                }
                // Try deleting either left or right character
                return canBePalindrome(s, left + 1, right, true) ||
                       canBePalindrome(s, left, right - 1, true);
            }
            left++;
            right--;
        }
        return true;
    }
    
    public static void main(String[] args) {
        ValidPalindromeII_Corrected solution = new ValidPalindromeII_Corrected();
        
        // Test cases
        System.out.println(solution.validPalindrome("aba"));    // true (already palindrome)
        System.out.println(solution.validPalindrome("abca"));   // true (remove 'c')
        System.out.println(solution.validPalindrome("abc"));    // false (need to remove >1 char)
        System.out.println(solution.validPalindrome("racecar"));// true (already palindrome)
        System.out.println(solution.validPalindrome("raceacar"));// true (remove one 'a')
        
        System.out.println("\nAlternative approach:");
        System.out.println(solution.validPalindromeAlternative("abca"));   // true
        System.out.println(solution.validPalindromeAlternative("abc"));    // false
    }
} 