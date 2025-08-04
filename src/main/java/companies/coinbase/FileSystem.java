package companies.coinbase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileSystem {

    class Dir {
        Map<String, String> files;
        Map<String, Dir> directories;

        public Dir() {
            this.files = new HashMap<>();
            this.directories = new HashMap<>();
        }
    }

    Dir root;

    public FileSystem() {
        this.root = new Dir();
    }

    public List<String> ls(String path) {
        List<String> result = new ArrayList<>();
        if (null == path || path.isBlank()) {
            return result;
        }

        Dir curr = root;
        var components = path.split("/");

        if (components.length != 0) {

            for (int i = 1; i < components.length - 1; i++) {
                curr = curr.directories.get(components[i]);
            }

            var lastComponent = components[components.length - 1];

            if (curr.files.containsKey(lastComponent)) {
                result.add(lastComponent);
                return result;
            } else if (curr.directories.containsKey(lastComponent)) {
                curr = curr.directories.get(lastComponent);
            }
        }

        result.addAll(new ArrayList<>(curr.files.keySet()));
        result.addAll(new ArrayList<>(curr.directories.keySet()));

        Collections.sort(result);
        return result;
    }

    public void mkdir(String path) {
        if (null == path || path.isBlank()) {
            return;
        }
        Dir curr = root;
        String[] components = path.split("/");
        for (int i = 1; i < components.length; i++) {
            var component = components[i];
            if (!curr.directories.containsKey(component)) {
                curr.directories.put(component, new Dir());
            }
            curr = curr.directories.get(component);
        }
    }

    public void addContentToFile(String filePath, String content) {
        if (null == filePath || filePath.isBlank()) {
            return;
        }
        var curr = root;
        var components = filePath.split("/");
        for (int i = 1; i < components.length - 1; i++) {
            curr = curr.directories.get(components[i]);
        }

        curr.files.put(
                components[components.length - 1],
                curr.files.getOrDefault(components[components.length - 1], "") + content
        );
    }

    public String readContentFromFile(String filePath) {
        if (null == filePath || filePath.isBlank()) {
            return null;
        }
        var curr = root;
        var components = filePath.split("/");
        for (int i = 1; i < components.length - 1; i++) {
            curr = curr.directories.get(components[i]);
        }

        return curr.files.get(components[components.length - 1]);
    }

    public static void main(String[] args) {
        var fileSystem = new FileSystem();
        System.out.println(fileSystem.ls("/"));
        fileSystem.mkdir("/a/b/c");
        fileSystem.addContentToFile("/a/b/c/d", "hello");
        System.out.println(fileSystem.ls("/"));
    }
}
