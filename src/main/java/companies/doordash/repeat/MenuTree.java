package companies.doordash.repeat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

public class MenuTree {

    class Node {
        String key;
        int value;
        List<Node> children;

        public Node(String key, int value) {
            this.key = key;
            this.value = value;
            this.children = new ArrayList<>();
        }
    }

    public int countChangedNodes(Node oldMenu, Node newMenu) {
        Map<String, Node> oldMenuMap = buildMap(oldMenu);
        Map<String, Node> newMenuMap = buildMap(newMenu);

        Set<String> allKeys = new HashSet<>();
        allKeys.addAll(oldMenuMap.keySet());
        allKeys.addAll(newMenuMap.keySet());
        Set<String> deleted = new HashSet<>();
        Set<String> added = new HashSet<>();
        Set<String> updated = new HashSet<>();
        for (String key: allKeys) {
            var oldNode = oldMenuMap.get(key);
            var newNode = newMenuMap.get(key);

            if (oldNode == null) {
                deleted.add(key);
            } else if (null == newNode) {
                added.add(key);
            } else if (oldNode.value != newNode.value) {
                updated.add(key);
            }
        }

        return deleted.size() + updated.size() + added.size();
    }

    private Map<String, Node> buildMap(Node menu) {
        if (null == menu) {
            return new HashMap<>();
        }
        Map<String, Node> map = new HashMap<>();
        Stack<Node> stack = new Stack<>();
        stack.push(menu);

        while (!stack.isEmpty()) {
            var node = stack.pop();
            map.put(node.key, node);
            List<Node> children = node.children;
            for (int i = children.size() - 1; i>=0; i--) {
                stack.push(children.get(i));
            }
        }

        return map;
    }

    public static void main(String[] args) {

    }
}
