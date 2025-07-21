package coding.top75;

public class CompareStringsWithBackspaces {
    public static boolean compare(String str1, String str2) {
        // Handle null cases
        if (str1 == null && str2 == null) return true;
        if (str1 == null || str2 == null) return false;

        int i = str1.length() - 1;
        int j = str2.length() - 1;

        while (i >= 0 || j >= 0) {
            // Find next valid character in str1
            i = getNextValidChar(str1, i);
            // Find next valid character in str2
            j = getNextValidChar(str2, j);

            // If both strings are exhausted
            if (i < 0 && j < 0) return true;
            // If one string is exhausted but not the other
            if (i < 0 || j < 0) return false;
            // If characters don't match
            if (str1.charAt(i) != str2.charAt(j)) return false;

            i--;
            j--;
        }
        
        return true;
    }

    private static int getNextValidChar(String str, int index) {
        int backspaceCount = 0;
        
        while (index >= 0) {
            if (str.charAt(index) == '#') {
                backspaceCount++;
                index--;
            } else if (backspaceCount > 0) {
                backspaceCount--;
                index--;
            } else {
                return index;
            }
        }
        return index;
    }

    public static void main(String[] args) {
        // Test cases
        System.out.println("Test 1: xy#z vs xzz# -> " + compare("xy#z", "xzz#")); // true
        System.out.println("Test 2: xy#z vs xyz# -> " + compare("xy#z", "xyz#")); // false
        System.out.println("Test 3: xp# vs xyz## -> " + compare("xp#", "xyz##")); // true
        System.out.println("Test 4: xywrrmp vs xywrrmu#p -> " + compare("xywrrmp", "xywrrmu#p")); // true
        System.out.println("Test 5: abc#### vs abcd### -> " + compare("abc####", "abcd###")); // true
        System.out.println("Test 6: ab## vs c#d# -> " + compare("ab##", "c#d#")); // true
        System.out.println("Test 7: null vs null -> " + compare(null, null)); // true
        System.out.println("Test 8: null vs abc -> " + compare(null, "abc")); // false
        // Additional test cases for backspace logic
        System.out.println("Test 9: a##b vs b -> " + compare("a##b", "b")); // true
        System.out.println("Test 10: ab#c vs ad#c -> " + compare("ab#c", "ad#c")); // true
        System.out.println("Test 11: a#b#c# vs d#e#f# -> " + compare("a#b#c#", "d#e#f#")); // true
    }
}
