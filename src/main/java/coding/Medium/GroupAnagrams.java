package coding.Medium;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GroupAnagrams {

    public List<List<String>> groupAnagrams(String[] strs) {
        Map<String, List<String>> groupedAnagramsMap = new HashMap<>();
        for (String str: strs) {
            char[] arr = str.toCharArray();
            Arrays.sort(arr);
            String elem = String.valueOf(arr);
            groupedAnagramsMap.putIfAbsent(elem, new ArrayList<>());
            groupedAnagramsMap.get(elem).add(str);
        }

        return new ArrayList<>(groupedAnagramsMap.values());
    }

    public static void main(String[] args) {
        var sol = new GroupAnagrams();
        System.out.println(sol.groupAnagrams(new String[]{"dog", "god", "hello"}));
    }
}
