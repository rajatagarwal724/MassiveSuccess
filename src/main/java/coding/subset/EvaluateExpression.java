package coding.subset;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EvaluateExpression {

    Map<String, List<Integer>> map = new HashMap<>();

    public List<Integer> diffWaysToEvaluateExpression(String input) {
        if (map.containsKey(input)) {
            return map.get(input);
        }
        List<Integer> result = new ArrayList<>();
        if (!input.contains("+") && !input.contains("*") && !input.contains("-")) {
            result.add(Integer.parseInt(input));
        } else {
            for (int i = 0; i < input.length(); i++) {
                char ch = input.charAt(i);
                if (!Character.isDigit(ch)) {
                    List<Integer> leftParts = diffWaysToEvaluateExpression(input.substring(0, i));
                    List<Integer> rightParts = diffWaysToEvaluateExpression(input.substring(i + 1));
                    for (int leftPart: leftParts) {
                        for (int rightPart: rightParts) {
                            if (ch == '*') {
                                result.add(leftPart * rightPart);
                            } else if (ch == '+') {
                                result.add(leftPart + rightPart);
                            } else if (ch == '-') {
                                result.add(leftPart - rightPart);
                            }
                        }
                    }
                }
            }
        }

        map.put(input, result);
        return result;
    }

    public static void main(String[] args) {
        var ee = new EvaluateExpression();
        List<Integer> result = ee.diffWaysToEvaluateExpression("1+2*3");
        System.out.println("Expression evaluations: " + result);

        ee = new EvaluateExpression();
        result = ee.diffWaysToEvaluateExpression("2*3-4-5");
        System.out.println("Expression evaluations: " + result);
    }
}
