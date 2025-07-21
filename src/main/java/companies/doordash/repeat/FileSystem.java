package companies.doordash.repeat;

import java.util.HashMap;
import java.util.Map;


/**
 * Time Complexity
 * O(M) where m is the length of the path String
 * 1st O(M) to get the last delimeter /
 * and then
 * 2nd O(M) to get the parent path
 *
 * O(K) Space Complexity to store unique k Strings
 */
public class FileSystem {

    private Map<String, Integer> map;

    public FileSystem() {
        this.map = new HashMap<>();
    }

    public boolean createPath(String path, int value) {
        if (null == path || path.isEmpty() || (path.equals("/"))
            || map.containsKey(path)) {
            return false;
        }

        int lastIndex = path.lastIndexOf("/");
        String parentPath = path.substring(0, lastIndex);

        // Note "/" is a valid parent path
        if (parentPath.length() > 1 && !map.containsKey(parentPath)) {
            return false;
        }

        map.put(path, value);
        return true;
    }

    public int get(String path) {
        return map.getOrDefault(path, -1);
    }

    public static void main(String[] args) {
        var sol = new FileSystem();
        System.out.println(sol.createPath("/a", 1));
        System.out.println(sol.get("/a"));
        System.out.println("------------");
        sol = new FileSystem();
        System.out.println(sol.createPath("/leet", 1));
        System.out.println(sol.createPath("/leet/code", 2));
        System.out.println(sol.get("/leet/code"));
        System.out.println(sol.createPath("/c/d", 3));
        System.out.println(sol.get("/c"));
    }
}
