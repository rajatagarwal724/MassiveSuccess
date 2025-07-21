package coding.linkedin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class AccountsMerge_1 {

    private Map<String, List<String>> adjacency;
    private Set<String> visited;

    public List<List<String>> accountsMerge(List<List<String>> accounts) {
        adjacency = new HashMap<>();
        visited = new HashSet<>();

        for (List<String> accountEmailsByName: accounts) {
            var firstEmail = accountEmailsByName.get(1);

            int size = accountEmailsByName.size();

            for (int i = 2; i < size; i++) {
                var email = accountEmailsByName.get(i);

                adjacency.computeIfAbsent(firstEmail, s -> new ArrayList<>()).add(email);
                adjacency.computeIfAbsent(email, s -> new ArrayList<>()).add(firstEmail);
            }
        }

        List<List<String>> allMergedAccounts = new ArrayList<>();

        for (List<String> accountToMerge: accounts) {
            var name = accountToMerge.get(0);
            var firstEmail = accountToMerge.get(1);

            if (!visited.contains(firstEmail)) {
                var mergedAccount = new ArrayList<String>();
                mergedAccount.add(name);
                DFS(mergedAccount, firstEmail);
                Collections.sort(mergedAccount.subList(1, mergedAccount.size()));
                allMergedAccounts.add(mergedAccount);
            }
        }
        return allMergedAccounts;
    }

    private void DFS(List<String> mergedAccount, String firstEmail) {
        visited.add(firstEmail);
        mergedAccount.add(firstEmail);

        if (!adjacency.containsKey(firstEmail)) {
            return;
        }

        for (String neighbour: adjacency.get(firstEmail)) {
            if (!visited.contains(neighbour)) {
                DFS(mergedAccount, neighbour);
            }
        }
    }

    public static void main(String[] args) {
        var input = List.of(
                List.of("John","johnsmith@mail.com","john_newyork@mail.com"),
                List.of("John","johnsmith@mail.com","john00@mail.com"),
                List.of("Mary","mary@mail.com"),
                List.of("John","johnnybravo@mail.com")
        );

        var sol = new AccountsMerge_1();

        sol.accountsMerge(input).stream().forEach(System.out::println);
    }
}
