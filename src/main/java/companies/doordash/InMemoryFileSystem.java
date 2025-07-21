package companies.doordash;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryFileSystem {

    class Dir {
        Map<String, Dir> directories;
        Map<String, String> files;

        public Dir() {
            this.directories = new HashMap<>();
            this.files = new HashMap<>();
        }
    }

    Dir root;

    public InMemoryFileSystem() {
        root = new Dir();
    }

    public List<String> ls(String path) {
        Dir cur = root;
        List<String> files = new ArrayList<>();
        if (!path.equals("/")) {
            String[] dirs = path.split("/");
            for (int i = 1; i < dirs.length - 1; i++) {
                cur = cur.directories.get(dirs[i]);
            }
            var lastElem = dirs[dirs.length - 1];

            if (cur.files.containsKey(lastElem)) {
                files.add(lastElem);
                return files;
            } else {
                cur = cur.directories.get(lastElem);
            }
        }

        files.addAll(new ArrayList<>(cur.files.keySet()));
        files.addAll(new ArrayList<>(cur.directories.keySet()));
        Collections.sort(files);
        return files;
    }

    public void mkdir(String path) {
        Dir cur = root;
        String[] dirs = path.split("/");
        for (int i = 1; i < dirs.length; i++) {
            var component = dirs[i];
            if (!cur.directories.containsKey(component)) {
                cur.directories.put(component, new Dir());
            }
            cur = cur.directories.get(component);
        }
    }

    public void addContentToFile(String filePath, String content) {
        Dir cur = root;
        String[] components = filePath.split("/");
        for (int i = 1; i < components.length - 1; i++) {
            cur = cur.directories.get(components[i]);
        }
        cur.files.put(
                components[components.length - 1],
                cur.files.getOrDefault(components[components.length - 1], "") + content
        );
    }

    public String readContentFromFile(String filePath) {
        Dir cur = root;
        String[] components = filePath.split("/");
        for (int i = 1; i < components.length - 1; i++) {
            cur = cur.directories.get(components[i]);
        }

        return cur.files.getOrDefault(components[components.length - 1], "");
    }

    public static void main(String[] args) {
        var obj = new InMemoryFileSystem();
        List<String> param_1 = obj.ls("/");
        System.out.println(param_1);
        obj.mkdir("/a/b/c");
        obj.addContentToFile("/a/b/c/d", "hello");
        param_1 = obj.ls("/");
        System.out.println(param_1);
        String param_4 = obj.readContentFromFile("/a/b/c/d");
        System.out.println(param_4);
    }
}

