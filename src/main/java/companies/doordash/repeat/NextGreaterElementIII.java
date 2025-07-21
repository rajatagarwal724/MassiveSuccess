package companies.doordash.repeat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NextGreaterElementIII {

    private void permutations(String prefix, String remaining, List<String> allPermutations) {
        int n = remaining.length();
        if (n == 0) {
            allPermutations.add(prefix);
            return;
        }

        for (int i = 0; i < n; i++) {
            permutations(
                    prefix + remaining.charAt(i),
                    remaining.substring(0, i) + remaining.substring(i + 1),
                    allPermutations
            );
        }
    }

    public int nextGreaterElement(int n) {
        List<String> allPerms = new ArrayList<>();
        permutations("", String.valueOf(n), allPerms);

        Collections.sort(allPerms);
        int idx = -1;
        for (int i = allPerms.size() - 1; i >= 0; i--) {
            if (n == Integer.parseInt(allPerms.get(i))) {
                idx = i;
                break;
            }
        }

        return idx == allPerms.size() - 1 ? -1 : Integer.parseInt(allPerms.get(idx + 1));
    }

    public int nextGreaterElement_(int n) {
        int pivotIdx = -1;

        char[] numberArr = ("" + n).toCharArray();
        int len = numberArr.length - 1;

        for (int i = len; i > 0; i--) {
            if (numberArr[i - 1] < numberArr[i]) {
                pivotIdx = i - 1;
                break;
            }
        }

        if (pivotIdx == -1) {
            return -1;
        }
        int nextGreaterElemIdx = -1;
        for (int i = pivotIdx + 1; i <= len; i++) {
            if (numberArr[i] > numberArr[pivotIdx]) {
                nextGreaterElemIdx = i;
            }
        }

        if (nextGreaterElemIdx == -1) {
            return -1;
        }

        swap(numberArr, pivotIdx, nextGreaterElemIdx);
        reverse(numberArr, pivotIdx + 1, len);
        return Integer.parseInt(String.valueOf(numberArr));
    }

    private void reverse(char[] arr, int i, int j) {
        while (i < j) {
            swap(arr, i, j);
            i++;
            j--;
        }
    }

    private void swap(char[] arr, int i, int j) {
        char temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
    }

    public static void main(String[] args) {
        var sol = new NextGreaterElementIII();
        System.out.println(
                sol.nextGreaterElement(12)
        );

        System.out.println(
                sol.nextGreaterElement_(158476531)
        );
    }
}
