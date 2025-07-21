package companies.doordash;

import java.util.HashMap;
import java.util.Map;

public class FileSystem {

    private final Map<String, Integer> map;

    public FileSystem() {
        this.map = new HashMap<>();
    }

    public boolean createPath(String path, int value) {
        if (null == path || path.isBlank() || path.equals("/") || map.containsKey(path)) {
            return false;
        }

        var lastDelimeterIdx = path.lastIndexOf("/");
        var parentDir = path.substring(0, lastDelimeterIdx);

        if (parentDir.length() > 1 && !map.containsKey(parentDir)) {
            return false;
        }

        map.put(path, value);
        return true;
    }

    public int get(String path) {
        if (map.containsKey(path)) {
            return map.get(path);
        }
        return -1;
    }

    public static void main(String[] args) {
        var sol = new FileSystem();
        System.out.println(sol.createPath("/leet", 1));
        System.out.println(sol.createPath("/leet/code", 1));
        System.out.println(sol.get("/leet/code"));
        System.out.println(sol.createPath("/leet/code", 3));
        System.out.println(sol.get("/leet/code"));
    }
}
