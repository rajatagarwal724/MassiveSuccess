package coding.warmup;

public class ShortestDistance {
    public int shortestDistance(String[] words, String word1, String word2) {
        int minDistance = Integer.MAX_VALUE;
        int word1Index = -1;
        int word2Index = -1;

        for (int index = 0; index < words.length; index++) {
            String word = words[index];

            if (word1.equals(word)) {
                word1Index = index;
            }

            if (word2.equals(word)) {
                word2Index = index;
            }

            if (word1Index != -1 && word2Index != -1 && Math.abs(word1Index - word2Index) < minDistance) {
                minDistance = Math.abs(word1Index - word2Index);
            }
        }

        return minDistance;
    }

    public static void main(String[] args) {
        var sol = new ShortestDistance();

        System.out.println(sol.shortestDistance(
                new String[]{"the", "quick", "brown", "fox", "jumps", "over", "the", "lazy", "dog"},
                "fox", "dog")
        );

        System.out.println(sol.shortestDistance(
                new String[]{"a", "c", "d", "b", "a"},
                "a", "b")
        );

        System.out.println(sol.shortestDistance(
                new String[]{"a", "b", "c", "d", "e"},
                "a", "e")
        );
    }
}
