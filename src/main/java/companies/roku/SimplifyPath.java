package companies.roku;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class SimplifyPath {

    public String simplifyPath(String path) {
        String[] components = path.split("/");
        List<String> dirs = new ArrayList<>();

        for (int i = 0; i < components.length; i++) {
            var elem = components[i];
            if (elem.isBlank() || elem.equals(".")) {
                continue;
            } else if (elem.equals("..")) {
                if (!dirs.isEmpty()) {
                    dirs.remove(dirs.size() - 1);
                }
            } else {
                dirs.add(elem);
            }
        }

        System.out.println("/" + String.join("/", dirs));
        return null;
    }

    public static void main(String[] args) {
        var sol = new SimplifyPath();
        System.out.println(sol.simplifyPath("/.../a/../b/c/../d/./"));
    }
}
