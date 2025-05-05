package coding.top150;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class IsomorphicStrings {

    private String transform(String s) {
        Map<Character, Integer> indexMap = new HashMap<>();
        StringBuilder result = new StringBuilder();
        char[] arr = s.toCharArray();

        for (int i = 0; i < arr.length; i++) {
            if(indexMap.containsKey(arr[i])) {
                result.append(String.valueOf(indexMap.get(arr[i])));
            } else {
                indexMap.put(arr[i], i);
                result.append(String.valueOf(i));
            }
        }
        return result.toString();
    }

    public boolean isIsomorphic_1(String s, String t) {
        int[] mapping_s_t = new int[256];
        int[] mapping_t_s = new int[256];

        Arrays.fill(mapping_s_t, -1);
        Arrays.fill(mapping_t_s, -1);

        for (int i = 0; i < s.length(); i++) {
            char at_s = s.charAt(i);
            char at_t = t.charAt(i);

            if (mapping_s_t[at_s] == -1 && mapping_t_s[at_t] == -1) {
                mapping_s_t[at_s] = at_t;
                mapping_t_s[at_t] = at_s;
            } else if (!(mapping_s_t[at_s] == at_t && mapping_t_s[at_t] == at_s)) {
                return false;
            }
        }
        System.out.println(Arrays.toString(mapping_s_t));
        System.out.println(Arrays.toString(mapping_t_s));
        return true;
    }

    public boolean isIsomorphic_2(String s, String t) {
        Map<Character, Character> mapping_s_t = new HashMap<>();
        Map<Character, Character> mapping_t_s = new HashMap<>();

        for (int i = 0; i < s.length(); i++) {
            char c1 = s.charAt(i);
            char c2 = t.charAt(i);

            if (!mapping_s_t.containsKey(c1) && !mapping_t_s.containsKey(c2)) {
                mapping_s_t.put(c1, c2);
                mapping_t_s.put(c2, c1);
            } else if (!mapping_s_t.containsKey(c1) || !mapping_t_s.containsKey(c2)) {
                return false;
            } else if (!(mapping_s_t.get(c1) == c2 && mapping_t_s.get(c2) == c1)) {
                return false;
            }
        }
        return true;
    }

    public boolean isIsomorphic(String s, String t) {
        return transform(s).equals(transform(t));
    }

    public static void main(String[] args) {
        var sol = new IsomorphicStrings();
//        System.out.println(sol.isIsomorphic("egg", "add"));
//        System.out.println(sol.isIsomorphic("foo", "bar"));
//        System.out.println(sol.isIsomorphic("paper", "title"));

        System.out.println(sol.isIsomorphic_2("egg", "add"));
        System.out.println(sol.isIsomorphic_2("foo", "bar"));
        System.out.println(sol.isIsomorphic_2("paper", "title"));
    }
}
