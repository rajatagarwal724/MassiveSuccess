package coding.top75;

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
        for (int i = 0; i < s.length(); i++) {
            char current = s.charAt(i);
            if (i < (s.length() - 1) && romanMap.get(current) < romanMap.get(s.charAt(i + 1))) {
                result -= romanMap.get(current);
            } else {
                result += romanMap.get(current);
            }
        }
        return result;
    }

    public static void main(String[] args) {
        RomanToInteger romanToInteger = new RomanToInteger();
        System.out.println(romanToInteger.romanToInt("XLII"));
        System.out.println(romanToInteger.romanToInt("CXCIV"));
        System.out.println(romanToInteger.romanToInt("MMMCDXLIV"));
    }
}
