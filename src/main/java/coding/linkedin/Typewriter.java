package coding.linkedin;

public class Typewriter {

    public int minTimeToType(String word) {
        int totalTime = 0;
        char previousChar = 'a';

        char[] wordArray = word.toCharArray();

        for (int i = 0; i < wordArray.length; i++) {
            var currChar = wordArray[i];
            var distance = Math.abs(previousChar - currChar);
            var time = Math.min(distance, (26 - distance)) + 1;

            totalTime += time;
            previousChar = currChar;
        }
        return totalTime;
    }

    public static void main(String[] args) {
        var sol = new Typewriter();
        System.out.println(
                sol.minTimeToType("abc")
        );
        System.out.println(
                sol.minTimeToType("bza")
        );

    }
}
