package companies.doordash.repeat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;


/**
 * Complexity Analysis
Time Complexity:

Sorting the visits takes O(n log n), where n is the number of visits.

Grouping visits by user takes O(n).

For each user with k visits, generating all 3-sequences takes O(k³) in the worst case. Given the overall constraint on n (which is small), this is acceptable.

Counting and comparing patterns takes additional linear time relative to the number of unique patterns.

Space Complexity:

O(n) is needed to store the grouped visits and the frequency map.
 */
public class AnalyzeUserVisitPattern {

    class Visit {
        String user;
        int time;
        String website;

        public Visit(String user, int time, String website) {
            this.user = user;
            this.time = time;
            this.website = website;
        }
    }
    
    // Optimized version
    public List<String> mostVisitedPatternOptimized(String[] username, int[] timestamp, String[] website) {
        Map<String, List<Visit>> userVisits = new HashMap<>();

        // Group visits by user
        for (int i = 0; i < username.length; i++) {
            userVisits.computeIfAbsent(username[i], s -> new ArrayList<>())
                    .add(new Visit(username[i], timestamp[i], website[i]));
        }

        Map<List<String>, Integer> patternFreqCount = new HashMap<>();
        int maxCount = 0;
        List<String> bestPattern = null;
        
        int processedUsers = 0;
        int totalUsers = userVisits.size();

        for (Map.Entry<String, List<Visit>> entry : userVisits.entrySet()) {
            List<Visit> visits = entry.getValue();
            visits.sort((v1, v2) -> v1.time - v2.time);
            
            if (visits.size() < 3) {
                processedUsers++;
                continue;
            }

            Set<List<String>> userPatterns = new HashSet<>();
            
            // Generate all 3-website patterns for this user
            for (int i = 0; i < visits.size() - 2; i++) {
                for (int j = i + 1; j < visits.size() - 1; j++) {
                    for (int k = j + 1; k < visits.size(); k++) {
                        List<String> pattern = Arrays.asList(
                            visits.get(i).website,
                            visits.get(j).website,
                            visits.get(k).website
                        );
                        userPatterns.add(pattern);
                    }
                }
            }

            // Update frequency count and track best pattern
            for (List<String> pattern : userPatterns) {
                int newCount = patternFreqCount.getOrDefault(pattern, 0) + 1;
                patternFreqCount.put(pattern, newCount);
                
                if (newCount > maxCount || 
                    (newCount == maxCount && (bestPattern == null || isLexicographicallySmaller(pattern, bestPattern)))) {
                    maxCount = newCount;
                    bestPattern = new ArrayList<>(pattern);
                }
            }
            
            processedUsers++;
            
            // Early termination: if current best can't be beaten by remaining users
            if (maxCount > totalUsers - processedUsers) {
                break;
            }
        }

        return bestPattern != null ? bestPattern : new ArrayList<>();
    }
    
    private boolean isLexicographicallySmaller(List<String> pattern1, List<String> pattern2) {
        for (int i = 0; i < pattern1.size(); i++) {
            int comparison = pattern1.get(i).compareTo(pattern2.get(i));
            if (comparison < 0) return true;
            if (comparison > 0) return false;
        }
        return false;
    }

    // Original version for comparison
    public List<String> mostVisitedPattern(String[] username, int[] timestamp, String[] website) {
        Map<String, List<Visit>> userVisits = new HashMap<>();

        for (int i = 0; i < username.length; i++) {
            userVisits.computeIfAbsent(username[i], s -> new ArrayList<>())
                    .add(new Visit(username[i], timestamp[i], website[i]));
        }

        Map<String, Integer> patternFreqCount = new HashMap<>();

        for (Map.Entry<String, List<Visit>> visitEntry: userVisits.entrySet()) {
            List<Visit> visits = visitEntry.getValue();
            visits.sort((v1, v2) -> v1.time - v2.time);
            if (visits.size() < 3) {
                continue;
            }
            Set<String> pattern = new HashSet<>();
            for (int i = 0; i < visits.size() - 2; i++) {
                for (int j = i + 1; j < visits.size() - 1; j++) {
                    for (int k = j + 1; k < visits.size(); k++) {
                        pattern.add(visits.get(i).website + ":" + visits.get(j).website + ":" + visits.get(k).website);
                    }
                }
            }

            for (String pat: pattern) {
                patternFreqCount.put(pat, patternFreqCount.getOrDefault(pat, 0) + 1);
            }
        }

        int maxCount = 0;
        String result = "";
        for (Map.Entry<String, Integer> entry: patternFreqCount.entrySet()) {
            String pattern = entry.getKey();
            int count = entry.getValue();

            if (count > maxCount || (count == maxCount && pattern.compareTo(result) < 0)) {
                maxCount = count;
                result = pattern;
            }
        }
        return Arrays.stream(result.split(":")).toList();
    }

    // Corrected Sliding Window Median Solution
    class SlidingWindowMedian {
        TreeMap<Integer, Integer> left, right;
        int leftSize, rightSize;

        public SlidingWindowMedian() {
            left = new TreeMap<>(Collections.reverseOrder()); // Safe comparator
            right = new TreeMap<>();
            leftSize = 0;
            rightSize = 0;
        }

        public double[] medianSlidingWindow(int[] nums, int k) {
            double[] res = new double[nums.length - k + 1];
            int resIdx = 0;
            
            for(int i = 0; i < nums.length; i++) {
                addNumber(nums[i]);
                
                if(i >= k - 1) {
                    if(i >= k) {
                        removeNum(nums[i - k]);
                    }
                    res[resIdx++] = findMedian(k);
                }
            }
            return res;
        }

        private double findMedian(int k) {
            if(k % 2 == 0) {
                return ((long) left.firstKey() + (long) right.firstKey()) / 2.0;
            }
            return left.firstKey();
        }

        private void removeNum(int num) {
            if (left.containsKey(num)) {
                left.put(num, left.get(num) - 1);
                if(left.get(num) == 0) {
                    left.remove(num);
                }
                leftSize--;
            } else {
                right.put(num, right.get(num) - 1);
                if(right.get(num) == 0) {
                    right.remove(num);
                }
                rightSize--;
            }
            balance();
        }

        private void addNumber(int num) {
            if(leftSize == 0 || left.isEmpty() || num <= left.firstKey()) {
                left.put(num, left.getOrDefault(num, 0) + 1);
                leftSize++;
            } else {
                right.put(num, right.getOrDefault(num, 0) + 1);
                rightSize++;
            }
            balance();
        }

        private void balance() {
            if(leftSize > rightSize + 1) {
                int elem = left.firstKey();
                left.put(elem, left.get(elem) - 1);
                if(left.get(elem) == 0) {
                    left.remove(elem);
                }
                right.put(elem, right.getOrDefault(elem, 0) + 1);
                leftSize--;
                rightSize++;
            } else if(rightSize > leftSize) {
                int elem = right.firstKey();
                right.put(elem, right.get(elem) - 1);
                if(right.get(elem) == 0) {
                    right.remove(elem);
                }
                left.put(elem, left.getOrDefault(elem, 0) + 1);
                leftSize++;
                rightSize--;
            }
        }
    }
}
