package coding.linkedin;

public class ValidParanthesesString {
    public boolean checkValidString(String s) {
        int openCount = 0, closeCount = 0, left = 0, right = s.length() - 1;
        char[] arr = s.toCharArray();

        while(left < right) {
            var leftElem = arr[left];
            var rightElem = arr[right];

            if (leftElem == '(' || leftElem == '*') {
                openCount++;
            } else {
                openCount--;
            }

            if (rightElem == ')' || rightElem == '*') {
                closeCount++;
            } else {
                closeCount--;
            }

            if (openCount < 0 || closeCount < 0) {
                return false;
            }
            left++;
            right--;
        }

        return true;
    }

    public boolean checkValidString_1(String s) {
        int openCount = 0;
        int closeCount = 0;
        int length = s.length() - 1;
        
        // Traverse the string from both ends simultaneously
        for (int i = 0; i <= length; i++) {
            // Count open parentheses or asterisks
            if (s.charAt(i) == '(' || s.charAt(i) == '*') {
                openCount++;
            } else {
                openCount--;
            }
            
            // Count close parentheses or asterisks
            if (s.charAt(length - i) == ')' || s.charAt(length - i) == '*') {
                closeCount++;
            } else {
                closeCount--;
            }
            
            // If at any point open count or close count goes negative, the string is invalid
            if (openCount < 0 || closeCount < 0) {
                return false;
            }
        }
        
        // If open count and close count are both non-negative, the string is valid
        return true;
    }

    public static void main(String[] args) {
        var sol = new ValidParanthesesString();

        System.out.println(
                sol.checkValidString("()")
        );
        System.out.println(
                sol.checkValidString("(*)")
        );
        System.out.println(
                sol.checkValidString("(*))")
        );
    }
}
