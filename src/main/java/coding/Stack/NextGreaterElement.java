package coding.Stack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;

public class NextGreaterElement {

    public List<Integer> nextLargerElement(List<Integer> arr) {
        List<Integer> res = new ArrayList<>();
        int[] resArray = new int[arr.size()];
        Stack<Integer> stack = new Stack<>();

        stack.push(0);

        for (int i = 1; i < arr.size(); i++) {
            int nextElem = arr.get(i);

            while (!stack.isEmpty() && nextElem > arr.get(stack.peek())) {
                int popIndex = stack.pop();
                resArray[popIndex] = nextElem;
            }
            stack.push(i);
        }

        while (!stack.isEmpty()) {
            resArray[stack.pop()] = -1;
        }
        for (int elem: resArray) {
            res.add(elem);
        }
        return res;
    }

    public static void main(String[] args) {
        var sol = new NextGreaterElement();
        var res = sol.nextLargerElement(List.of(4, 5, 2, 25));
        res = sol.nextLargerElement(List.of(13, 7, 6, 12));
        res = sol.nextLargerElement(List.of(1, 2, 3, 4, 5));
        System.out.println(res);
    }
}
