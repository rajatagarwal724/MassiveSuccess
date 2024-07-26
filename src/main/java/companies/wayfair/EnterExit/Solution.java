package companies.wayfair.EnterExit;

import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Solution {

    enum BadgeStatus {
        ENTER,
        EXIT,
        UNKNOWN
    }

    public static Pair<Set<String>, Set<String>> getUnbadgedEmployeed(
            List<Pair<String, BadgeStatus>> badgeStatuses
    ) {
        Map<String, List<BadgeStatus>> badgeCount = new HashMap<>();
        var enteredEmployees = new HashSet<String>();
        var exitedEmployees = new HashSet<String>();
        for (Pair<String, BadgeStatus> badgeStatusPair: badgeStatuses) {
            var name = badgeStatusPair.getKey();
            var status = badgeStatusPair.getValue();
            List<BadgeStatus> allStatus = badgeCount.getOrDefault(name, new ArrayList<>());

            if (allStatus.size() > 0 && BadgeStatus.EXIT.equals(status)
                    && BadgeStatus.ENTER.equals(allStatus.get(allStatus.size() - 1))) {
                allStatus.remove(allStatus.size() - 1);
                allStatus.add(BadgeStatus.UNKNOWN);
            } else {
                allStatus.add(status);
            }

            badgeCount.put(name, allStatus);
        }

        for (Map.Entry<String, List<BadgeStatus>> entry: badgeCount.entrySet()) {
            entry.getValue().forEach(badgeStatus -> {
                if (BadgeStatus.ENTER.equals(badgeStatus)) {
                    enteredEmployees.add(entry.getKey());
                } else if (BadgeStatus.EXIT.equals(badgeStatus)){
                    exitedEmployees.add(entry.getKey());
                }
            });
        }

        return Pair.of(enteredEmployees, exitedEmployees);
    }

    public static void main(String[] args) {

        /**
         * ["Raj", "enter"],
         * ["Paul", "enter"],
         * ["Paul", "exit"],
         * ["Paul", "exit"],
         * ["Paul", "enter"],
         * ["Raj", "enter"],
         */
        var status = List.of(
                Pair.of("Raj", BadgeStatus.ENTER),
                Pair.of("Paul", BadgeStatus.ENTER),
                Pair.of("Paul", BadgeStatus.EXIT),
                Pair.of("Paul", BadgeStatus.EXIT),
                Pair.of("Paul", BadgeStatus.ENTER),
                Pair.of("Raj", BadgeStatus.ENTER)
        );
        var res = getUnbadgedEmployeed(status);


        res = getUnbadgedEmployeed(
                List.of(
                        Pair.of("Paul", BadgeStatus.ENTER),
                        Pair.of("Paul", BadgeStatus.ENTER),
                        Pair.of("Paul", BadgeStatus.EXIT),
                        Pair.of("Paul", BadgeStatus.EXIT)
                )
        );

        res = getUnbadgedEmployeed(
                List.of(
                        Pair.of("Paul", BadgeStatus.ENTER),
                        Pair.of("Paul", BadgeStatus.EXIT)
                )
        );

        System.out.println(res);
    }
}
