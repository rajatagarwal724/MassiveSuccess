package coding.top75.arrays;

import java.util.ArrayList;
import java.util.List;

public class ZigzagConversion {

    public String convert(String s, int numRows) {

        if (numRows == 1 || numRows >= s.length()) {
            return s;
        }

        List<StringBuilder> list = new ArrayList<>();
        for (int i = 0; i < numRows; i++) {
            list.add(new StringBuilder());
        }

        char[] arr = s.toCharArray();
        boolean goingDown = false;
        int currRow = 0;
        for (int i = 0; i < arr.length; i++) {
            var elem = arr[i];

            list.get(currRow).append(elem);

            if (currRow == 0 || currRow == (numRows - 1)) {
                goingDown = !goingDown;
            }

            if (goingDown) {
                currRow += 1;
            } else {
                currRow -= 1;
            }
        }

        StringBuilder result = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            result.append(list.get(i));
        }
        return result.toString();
    }

    public static void main(String[] args) {
        var sol = new ZigzagConversion();
        System.out.println(sol.convert("HELLOPROGRAMMING", 4));
    }
}
