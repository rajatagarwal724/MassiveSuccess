package coding.top75;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GroupAnagrams {

    public List<List<String>> groupAnagrams(String[] strs) {
        Map<String, List<String>> groupedAnagramsMap = new HashMap<>();
        for (String word: strs) {
            char[] sortedWordArray = word.toCharArray();
            Arrays.sort(sortedWordArray);
            String key = String.valueOf(sortedWordArray);
            groupedAnagramsMap.computeIfAbsent(key, s -> new ArrayList<>()).add(word);
        }
        return new ArrayList<>(groupedAnagramsMap.values());
    }

    public static void main(String[] args) {
        var sol = new GroupAnagrams();
        System.out.println(
                StringUtils.join(sol.groupAnagrams(new String[] {"dog", "god", "hello"}))
        );

        System.out.println(
                StringUtils.join(sol.groupAnagrams(new String[] {"listen", "silent", "enlist"}))
        );

        System.out.println(
                StringUtils.join(sol.groupAnagrams(new String[] {"abc", "cab", "bca", "xyz", "zxy"}))
        );
    }
}
