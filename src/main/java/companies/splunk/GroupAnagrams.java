package companies.splunk;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GroupAnagrams {

    public List<List<String>> groupAnagrams(String[] strs) {
        Map<String, List<String>> map = new HashMap<>();
        for (String elem: strs) {
            char[] chars = elem.toCharArray();
            Arrays.sort(chars);
            String sortedElem = String.valueOf(chars);
            map.computeIfAbsent(sortedElem, s -> new ArrayList<>()).add(elem);
        }
        return new ArrayList<>(map.values());
    }

    public static void main(String[] args) {
        var sol = new GroupAnagrams();
        var res = sol.groupAnagrams(List.of("eat","tea","tan","ate","nat","bat").toArray(new String[0]));
        System.out.println(res);
    }
}
