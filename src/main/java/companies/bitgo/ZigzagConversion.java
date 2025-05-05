package companies.bitgo;

import java.util.ArrayList;
import java.util.List;

public class ZigzagConversion {

    public String convert(String s, int numRows) {
        if (numRows == 1 || numRows >= s.length()) {
            return s;
        }
        List<StringBuilder> rows = new ArrayList<>();

        for (int row = 0; row < numRows; row++) {
            rows.add(new StringBuilder());
        }

        char[] elems = s.toCharArray();

        int currentRow = 0;
        int step = 1;

        for (char elem: elems) {
            rows.get(currentRow).append(elem);

            currentRow += step;

            if ((currentRow == 0) || (currentRow == (numRows - 1))) {
                step = -step;
            }
        }

        var result = new StringBuilder();
        rows.forEach(result::append);
        return result.toString();
    }

    public static void main(String[] args) {
        var sol = new ZigzagConversion();
        System.out.println(sol.convert("PAYPALISHIRING", 3));
        System.out.println(sol.convert("PAYPALISHIRING", 4));
    }
}
