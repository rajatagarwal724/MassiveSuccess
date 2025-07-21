package companies.doordash.repeat;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DeliveryPatterns {

    private List<String> getAllPatterns(int tasks) {
        Set<String> picked = new HashSet<>();
        Set<String> delivered = new HashSet<>();
        List<String> currentPattern = new ArrayList<>();
        List<String> patterns = new ArrayList<>();

        recurse(picked, delivered, currentPattern, patterns, tasks);
        return patterns;
    }

    private void recurse(Set<String> picked, Set<String> delivered, List<String> currentPattern, List<String> patterns, int tasks) {
        if (currentPattern.size() == tasks * 2) {
            patterns.add(String.join("->", currentPattern));
            return;
        }

        for (int i = 1; i <= tasks; i++) {
            String pick = "P" + i;
            String deliver = "D" + i;

            if (!picked.contains(pick)) {
                picked.add(pick);
                currentPattern.add(pick);

                recurse(picked, delivered, currentPattern, patterns, tasks);

                picked.remove(pick);
                currentPattern.remove(currentPattern.size() - 1);
            }

            if (!delivered.contains(deliver) && picked.contains(pick)) {
                delivered.add(deliver);
                currentPattern.add(deliver);

                recurse(picked,delivered, currentPattern, patterns, tasks);

                delivered.remove(deliver);
                currentPattern.remove(currentPattern.size() - 1);
            }
        }
    }

    public static void main(String[] args) {
        var sol = new DeliveryPatterns();

        sol.getAllPatterns(2).forEach(System.out::println);

    }
}
