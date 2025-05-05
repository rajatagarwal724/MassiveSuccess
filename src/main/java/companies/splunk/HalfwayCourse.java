package companies.splunk;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class HalfwayCourse {
    public static String halfwayCourse(List<String[]> pairs) {
        // Step 1: Build adjacency list and in-degree map
        Map<String, String> prerequisiteMap = new HashMap<>();
        Set<String> allCourses = new HashSet<>();

        for (String[] pair : pairs) {
            String prerequisite = pair[0];
            String course = pair[1];
            prerequisiteMap.put(prerequisite, course);
            allCourses.add(prerequisite);
            allCourses.add(course);
        }

        // Step 2: Find the starting course (the one with no prerequisite)
        String startCourse = null;
        for (String course : allCourses) {
            if (!prerequisiteMap.containsValue(course)) {
                startCourse = course;
                break;
            }
        }

        // Step 3: Traverse the sequence to determine the order of courses
        List<String> courseOrder = new ArrayList<>();
        String currentCourse = startCourse;
        while (currentCourse != null) {
            courseOrder.add(currentCourse);
            currentCourse = prerequisiteMap.get(currentCourse);
        }

        // Step 4: Determine the halfway course
        int halfwayIndex = courseOrder.size() / 2;
        return courseOrder.get(halfwayIndex);
    }

    public static void main(String[] args) {
        List<String[]> pairs1 = Arrays.asList(
                new String[]{"Foundations of Computer Science", "Operating Systems"},
                new String[]{"Data Structures", "Algorithms"},
                new String[]{"Computer Networks", "Computer Architecture"},
                new String[]{"Algorithms", "Foundations of Computer Science"},
                new String[]{"Computer Architecture", "Data Structures"},
                new String[]{"Software Design", "Computer Networks"}
        );
        System.out.println(halfwayCourse(pairs1)); // Output: "Data Structures"

        List<String[]> pairs2 = Arrays.asList(
                new String[]{"Algorithms", "Foundations of Computer Science"},
                new String[]{"Data Structures", "Algorithms"},
                new String[]{"Foundations of Computer Science", "Logic"},
                new String[]{"Logic", "Compilers"},
                new String[]{"Compilers", "Distributed Systems"}
        );
        System.out.println(halfwayCourse(pairs2)); // Output: "Foundations of Computer Science"

//        List<String[]> pairs3 = Arrays.asList(
//                new String[]{"Data Structures", "Algorithms"}
//        );
//        System.out.println(halfwayCourse(pairs3)); // Output: "Data Structures"
    }
}
