package coding.top75;

import java.util.ArrayList;
import java.util.List;

public class ZigZagConversion {

    public String convert(String s, int numRows) {
        if (numRows == 1 || numRows >= s.length()) {
            return s;
        }
        List<StringBuilder> resBuilder = new ArrayList<>();
        for (int i = 0; i < numRows; i++) {
            resBuilder.add(new StringBuilder());
        }

        int currentRow = 0;
        int dir = 0;
        for (char ch: s.toCharArray()) {
            resBuilder.get(currentRow).append(ch);
            if (currentRow == 0) {
                dir = 1;
            } else if (currentRow == (numRows - 1)) {
                dir = -1;
            }
            currentRow = currentRow + dir;
        }

        StringBuilder result = new StringBuilder();
        for (StringBuilder row: resBuilder) {
            result.append(row);
        }
        return result.toString();
    }

    public static void main(String[] args) {
        var sol = new ZigZagConversion();
        System.out.println(sol.convert("HELLOPROGRAMMING", 4));
    }
}
