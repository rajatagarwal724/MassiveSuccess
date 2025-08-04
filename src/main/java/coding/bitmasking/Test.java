package coding.bitmasking;

public class Test {

    public static void main(String[] args) {
        int keys = 0;

        keys |= (1 << 0);

        keys |= (1 << 0);

        System.out.println(keys);

        int targetState = (1 << (6 / 2)) - 1;
        System.out.println(targetState);

        int k = 3;
        int state = 0;
        // Set the kth bit
        int mask = 1 << k;  // Creates a number with only kth bit as 1
        state |= mask;      // OR operation sets the kth bit in state

        System.out.println(state);

        boolean isSet = (mask & state) != 0;
        System.out.println(isSet);

        // Count total keys and create a bitmask for all keys
        int totalKeys = 6;  // Assuming keys 'a' through 'f'
        int finalKeyState = (1 << totalKeys) - 1;  // Creates a mask with all totalKeys bits set to 1

        System.out.println(finalKeyState);
    }
}
