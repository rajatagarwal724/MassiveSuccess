package companies.doordash;

import java.util.HashMap;
import java.util.Map;

public class FileSystemI {
    class TrieNode {
        String name;
        int val = -1;

        Map<String, TrieNode> map = new HashMap<>();

        public TrieNode(String name) {
            this.name = name;
        }
    }

    TrieNode root;

    public FileSystemI() {
        root = new TrieNode("");
    }

    public boolean createPath(String path, int value) {
        String[] components = path.split("/");
        TrieNode curr = root;

        for (int i = 1; i < components.length; i++) {
            if (!curr.map.containsKey(components[i])) {
                if (i == components.length - 1) {
                    curr.map.put(components[i], new TrieNode(components[i]));
                } else {
                    return false;
                }
            }
            curr = curr.map.get(components[i]);
        }

        if (curr.val != -1) {
            return false;
        }
        curr.val = value;
        return true;
    }

    public int get(String path) {
        var curr = root;
        String[] components = path.split("/");
        for (int i = 1; i < components.length; i++) {
            var currentComponent = components[i];
            if (curr.map.containsKey(currentComponent)) {
                curr = curr.map.get(currentComponent);
            } else {
                return -1;
            }
        }
        return curr.val;
    }

    public static void main(String[] args) {
        var sol = new FileSystemI();
        System.out.println(sol.createPath("/leet", 1));
        System.out.println(sol.createPath("/leet/code", 2));
        System.out.println(sol.get("/leet/code"));
        System.out.println(sol.createPath("/leet/code", 3));
        System.out.println(sol.get("/leet/code"));
    }

}
