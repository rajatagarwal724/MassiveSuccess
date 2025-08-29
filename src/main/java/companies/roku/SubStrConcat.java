package companies.roku;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SubStrConcat {

    public List<Integer> findSubstring(String s, String[] words) {
        if (null == s || s.length() == 0 || null == words || words.length == 0) {
            return new ArrayList<>();
        }

        List<Integer> result = new ArrayList<>();

        int wordLen = words[0].length();
        int totalWords = words.length;
        int totalLen = totalWords * wordLen;

        Map<String, Integer> wordFreqMap = new HashMap<>();
        for (String word: words) {
            wordFreqMap.put(word, wordFreqMap.getOrDefault(word, 0) + 1);
        }


        for (int i = 0; i < wordLen; i++) {
            int left = i;
            int count = 0;
            Map<String, Integer> seen = new HashMap<>();

            for (int right = i; right <= s.length() - wordLen; right = right + wordLen) {
                String sub = s.substring(right, right + wordLen);

                if (wordFreqMap.containsKey(sub)) {

                    seen.put(sub, seen.getOrDefault(sub, 0) + 1);
                    count++;

                    while (seen.getOrDefault(sub, 0) > wordFreqMap.getOrDefault(sub, 0)) {
                        String leftPart = s.substring(left, left + wordLen);
                        if (seen.containsKey(leftPart)) {
                            seen.put(leftPart, seen.get(leftPart) - 1);
                            count--;
                        }
                        left = left + wordLen;
                    }

                    if (count == totalWords) {
                        result.add(left);
                        String leftPart = s.substring(left, left + wordLen);
                        seen.put(leftPart, seen.get(leftPart) - 1);
                        count--;
                        left = left + wordLen;
                    }
                } else {
                    seen.clear();
                    count = 0;
                    left = right + wordLen;
                }
            }
        }

        return result;
    }

    public static void main(String[] args) {
        var sol = new SubStrConcat();
        System.out.println(sol.findSubstring("barfoothefoobarman", new String[]{"foo","bar"}));
        System.out.println(sol.findSubstring("wordgoodgoodgoodbestword", new String[]{"word","good","best","word"}));
        System.out.println(sol.findSubstring("barfoofoobarthefoobarman", new String[]{"bar","foo","the"}));
    }
}
