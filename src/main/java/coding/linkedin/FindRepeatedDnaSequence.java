package coding.linkedin;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FindRepeatedDnaSequence {

    public List<String> findRepeatedDnaSequences(String s) {
        int L = 10;
        Set<String> seen = new HashSet<>(), output = new HashSet<>();

        for (int i = 0; i < s.length() - L + 1; i++) {
            var subS = s.substring(i, i + L);
            if (seen.contains(subS)) {
                output.add(subS);
            }
            seen.add(subS);
        }

        return new ArrayList<>(output);
    }

    public static void main(String[] args) {
        var sol = new FindRepeatedDnaSequence();
        System.out.println(
                sol.findRepeatedDnaSequences("AAAAACCCCCAAAAACCCCCCAAAAAGGGTTT")
        );

        System.out.println(
                sol.findRepeatedDnaSequences("AAAAAAAAAAAAA")
        );
    }
}
