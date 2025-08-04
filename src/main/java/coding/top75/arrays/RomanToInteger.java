package coding.top75.arrays;

import java.util.HashMap;
import java.util.Map;

public class RomanToInteger {

    public int romanToInt(String s) {
        Map<Character, Integer> romanMap = new HashMap<>();
        romanMap.put('I', 1);
        romanMap.put('V', 5);
        romanMap.put('X', 10);
        romanMap.put('L', 50);
        romanMap.put('C', 100);
        romanMap.put('D', 500);
        romanMap.put('M', 1000);

        int result = 0;

        for (int i = 0;  i < s.length(); i++) {
            var elem = s.charAt(i);

            if (i < (s.length() - 1) && romanMap.get(elem) < romanMap.get(s.charAt(i + 1))) {
                result -= romanMap.get(elem);
            } else {
                result += romanMap.get(elem);
            }
        }

        return result;
    }

    public static void main(String[] args) {
        var sol = new RomanToInteger();
        System.out.println(sol.romanToInt("XLII"));
        System.out.println(sol.romanToInt("XLII"));
        System.out.println(sol.romanToInt("XLII"));
    }
}
