package companies.doordash.repeat;

import java.util.HashMap;
import java.util.Map;

public class FileSystemII {

    class TrieNode {
        String name;
        int val;

        Map<String, TrieNode> map;

        public TrieNode(String name) {
            this.name = name;
            this.val = -1;
            this.map = new HashMap<>();
        }
    }

    TrieNode root;

    public FileSystemII() {
        root = new TrieNode("");
    }

    public boolean createPath(String path, int value) {
        if (path == null || path.isEmpty()) {
            return false;
        }

        String[] components = path.split("/");
        TrieNode curr = root;

        for (int i = 1; i < components.length; i++) {
            var elem = components[i];

            if (!curr.map.containsKey(elem)) {
                if (i == components.length - 1) {
                    TrieNode newNode = new TrieNode(elem);
                    curr.map.put(elem, newNode);
                } else {
                    return false;
                }
            }
            curr = curr.map.get(elem);
        }

        // Value already exists in the Map
        if (curr.val != -1) {
            return false;
        }

        curr.val = value;
        return true;
    }

    public int get(String path) {
        if (null == path || path.isEmpty()) {
            return -1;
        }

        String[] components = path.split("/");
        TrieNode curr = root;

        for (int i = 1; i < components.length; i++) {
            var elem = components[i];

            if (!curr.map.containsKey(elem)) {
                return -1;
            }
            curr = curr.map.get(elem);
        }

        return curr.val;
    }

    public static void main(String[] args) {
        var sol = new FileSystemII();
        System.out.println(sol.createPath("/a", 1));
        System.out.println(sol.get("/a"));
        System.out.println("------------");

        sol = new FileSystemII();
        System.out.println(sol.createPath("/leet", 1));
        System.out.println(sol.createPath("/leet/code", 2));
        System.out.println(sol.get("/leet/code"));
        System.out.println(sol.createPath("/c/d", 3));
        System.out.println(sol.get("/c"));
    }
}
