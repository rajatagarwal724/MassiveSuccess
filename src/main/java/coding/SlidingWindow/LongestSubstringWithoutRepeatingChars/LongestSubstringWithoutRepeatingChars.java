package coding.SlidingWindow.LongestSubstringWithoutRepeatingChars;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class LongestSubstringWithoutRepeatingChars {

    /**
     * Solution using HashMap to track the most recent index of each character
     * Time Complexity: O(n)
     * Space Complexity: O(min(m, n)) where m is the size of the character set
     */
    public int lengthOfLongestSubstring(String s) {
        if (s == null || s.length() == 0) return 0;
        
        Map<Character, Integer> charIndexMap = new HashMap<>();
        int maxLength = 0;
        int windowStart = 0;
        
        for (int windowEnd = 0; windowEnd < s.length(); windowEnd++) {
            char currentChar = s.charAt(windowEnd);
            
            // If we've seen this character before and its index is within the current window,
            // move the window start to the position after the last occurrence
            if (charIndexMap.containsKey(currentChar)) {
                // Math.max ensures we don't move window start backwards
                windowStart = Math.max(windowStart, charIndexMap.get(currentChar) + 1);
            }
            
            // Update the character's most recent position
            charIndexMap.put(currentChar, windowEnd);
            
            // Update max length found so far
            maxLength = Math.max(maxLength, windowEnd - windowStart + 1);
        }
        
        return maxLength;
    }
    
    /**
     * Alternative solution using a HashSet for better readability
     */
    public int lengthOfLongestSubstringSet(String s) {
        if (s == null || s.length() == 0) return 0;
        
        Set<Character> charSet = new HashSet<>();
        int maxLength = 0;
        int windowStart = 0;
        
        for (int windowEnd = 0; windowEnd < s.length(); windowEnd++) {
            char currentChar = s.charAt(windowEnd);
            
            // Shrink the window from the left until we remove the duplicate
            while (charSet.contains(currentChar)) {
                charSet.remove(s.charAt(windowStart));
                windowStart++;
            }
            
            // Add current character to the set
            charSet.add(currentChar);
            
            // Update max length
            maxLength = Math.max(maxLength, windowEnd - windowStart + 1);
        }
        
        return maxLength;
    }

    public static void main(String[] args) {
        var sol = new LongestSubstringWithoutRepeatingChars();

        System.out.println("HashMap solution results:");
        System.out.println(sol.lengthOfLongestSubstring("abcabcbb")); // 3
        System.out.println(sol.lengthOfLongestSubstring("bbbbb"));    // 1
        System.out.println(sol.lengthOfLongestSubstring("pwwkew"));   // 3
        
        System.out.println("\nHashSet solution results:");
        System.out.println(sol.lengthOfLongestSubstringSet("abcabcbb")); // 3
        System.out.println(sol.lengthOfLongestSubstringSet("bbbbb"));    // 1
        System.out.println(sol.lengthOfLongestSubstringSet("pwwkew"));   // 3
    }
}
