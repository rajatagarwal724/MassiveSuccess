package coding.top150;

import java.util.HashSet;
import java.util.Set;

public class HappyNumber {

    public boolean isHappy(int n) {
        Set<Integer> seen = new HashSet<>();
        seen.add(n);
        while (n != 1) {

        }
        return true;
    }

    public static void main(String[] args) {
        int n = 19;
        int nextNum = 0;
        while (n != 0) {
            int firstNum = n / 10;
            System.out.println(firstNum);
            nextNum += (firstNum * firstNum);
            System.out.println(firstNum);
            n = n % 10;
            System.out.println(n);
        }
    }
}
