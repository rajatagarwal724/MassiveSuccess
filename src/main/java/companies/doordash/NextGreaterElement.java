package companies.doordash;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 0 1 2 3 4 5 6 7 8
 * 1 5 8 4 7 6 5 3 1
 */
public class NextGreaterElement {
    public int nextGreaterElement(int n) {
        char[] arr = ("" + n).toCharArray();
        int pivotIndex = -1;
        for (int i = arr.length - 1; i >= 1; i--) {
            if (arr[i] > arr[i - 1]) {
                pivotIndex = i - 1;
                break;
            }
        }

        if (pivotIndex == -1) {
            return -1;
        }
        int nextGreaterIndex = -1;
        for (int i = pivotIndex + 1; i < arr.length; i++) {
            if (arr[i] > arr[pivotIndex]) {
                nextGreaterIndex = i;
            }
        }

        if (nextGreaterIndex == -1) {
            return -1;
        }

        swap(arr, pivotIndex, nextGreaterIndex);

        reverse(arr, pivotIndex + 1, arr.length - 1);

        try {
            return Integer.parseInt(String.valueOf(arr));
        } catch (Exception ex) {
            return -1;
        }

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


    public int nextGreaterElemRecur(int n) {
        List<String> allPerms = new ArrayList<>();
        permutations("", String.valueOf(n), allPerms);
        Collections.sort(allPerms);
        int idx = 0;
        for (int i = allPerms.size() - 1; i >= 0; i--) {
            var combination = Integer.parseInt(allPerms.get(i));
            if (combination == n) {
                idx = i;
                break;
            }
        }

        return idx == (allPerms.size() - 1) ? -1 : Integer.parseInt(allPerms.get(idx + 1));
    }

    private void permutations(String prefix, String remaining, List<String> allPerms) {
        int n = remaining.length();

        if (n == 0) {
            allPerms.add(prefix);
            return;
        }

        for (int i = 0; i < n; i++) {
            permutations(
                    prefix + remaining.charAt(i),
                    remaining.substring(0, i) + remaining.substring(i + 1),
                    allPerms
            );
        }
    }

    public static void main(String[] args) {
        var sol = new NextGreaterElement();

        System.out.println(sol.nextGreaterElement(2147483476));

        System.out.println(sol.nextGreaterElement(2147483476));

//        2147483476
//
//        2147483674
//        2147483647
//
//        Inte
    }
}
