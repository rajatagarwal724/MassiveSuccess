package companies.roku.repeat;

import java.util.*;

public class StringConcat {

    public List<Integer> findSubstring(String s, String[] words) {
        Map<String, Integer> freqMap = new HashMap<>();
        for (String word: words) {
            freqMap.put(word, freqMap.getOrDefault(word, 0) + 1);
        }
        List<Integer> result = new ArrayList<>();
        int wordLen = words[0].length();
        int totalWords = words.length;

        for (int i = 0; i < wordLen; i++) {
            int left = i;
            Map<String, Integer> seen = new HashMap<>();
            int totalCount = 0;

            for (int right = i; right <= s.length() - wordLen; right = right + wordLen) {
                var rightSubStr = s.substring(right, right + wordLen);

                if (freqMap.containsKey(rightSubStr)) {
                    seen.put(rightSubStr, seen.getOrDefault(rightSubStr, 0) + 1);
                    totalCount++;

                    while (seen.getOrDefault(rightSubStr, 0) > freqMap.getOrDefault(rightSubStr, 0)) {
                        var leftPart = s.substring(left, left + wordLen);
                        if (seen.containsKey(leftPart)) {
                            seen.put(leftPart, seen.get(leftPart) - 1);
                            totalCount--;
                        }
                        left = left + wordLen;
                    }

                    if (totalCount == totalWords) {
                        result.add(left);
                        var leftPart = s.substring(left, left + wordLen);
                        if (seen.containsKey(leftPart)) {
                            seen.put(leftPart, seen.get(leftPart) - 1);
                            totalCount--;
                        }
                        left = left + wordLen;
                    }


                } else {
                    totalCount = 0;
                    seen.clear();
                    left = right + wordLen;
                }
            }
        }

        return result;
    }

    public static void main(String[] args) {
        var sol = new StringConcat();
        System.out.println(
                sol.findSubstring("barfoothefoobarman", new String[] {"foo","bar"})
        );

        System.out.println(
                sol.findSubstring("wordgoodgoodgoodbestword", new String[] {"word","good","best","word"})
        );

        System.out.println(
                sol.findSubstring("barfoofoobarthefoobarman", new String[] {"bar","foo","the"})
        );
    }
}
