package companies.doordash;

public class BuddyStrings {

    public boolean buddyStrings(String s, String goal) {
        if (s.length() != goal.length()) {
            return false;
        }

        if (s.equals(goal)) {
            int[] freqArr = new int[26];
            for (int i = 0; i < s.length(); i++) {
                char elem = s.charAt(i);
                freqArr[elem - 'a']++;
                if (freqArr[elem - 'a'] == 2) {
                    return true;
                }
            }
        }

        int firstIdx = -1, secondIdx = -1;

        for (int i = 0; i < s.length(); i++) {
            char sChar = s.charAt(i);
            char gChar = goal.charAt(i);

            if (sChar != gChar) {
                if (firstIdx == -1) {
                    firstIdx = i;
                } else if (secondIdx == -1) {
                    secondIdx = i;
                } else {
                    return false;
                }
            }
        }

        if (firstIdx == -1 || secondIdx == -1) {
            return false;
        }

        return (s.charAt(firstIdx) == goal.charAt(secondIdx))
                &&
                (s.charAt(secondIdx) == goal.charAt(firstIdx));
    }

    public static void main(String[] args) {
        var sol = new BuddyStrings();
        System.out.println(
                sol.buddyStrings("ab", "ba")
        );
        System.out.println(
                sol.buddyStrings("ab", "ab")
        );
        System.out.println(
                sol.buddyStrings("aa", "aa")
        );
    }
}
