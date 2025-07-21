package coding.linkedin;

import java.util.*;

/**
 * LeetCode 210: Course Schedule II
 * 
 * There are a total of numCourses courses you have to take, labeled from 0 to numCourses - 1.
 * You are given an array prerequisites where prerequisites[i] = [ai, bi] indicates that 
 * you must take course bi first if you want to take course ai.
 * 
 * Return the ordering of courses you should take to finish all courses. 
 * If there are many valid answers, return any of them. 
 * If it is impossible to finish all courses, return an empty array.
 * 
 * Example 1:
 * Input: numCourses = 2, prerequisites = [[1,0]]
 * Output: [0,1]
 * Explanation: There are a total of 2 courses. To take course 1 you should have finished course 0.
 * So the correct course order is [0,1].
 * 
 * Example 2:
 * Input: numCourses = 4, prerequisites = [[1,0],[2,0],[3,1],[3,2]]
 * Output: [0,2,1,3]
 * Explanation: There are a total of 4 courses. To take course 1 you should have finished course 0.
 * To take course 3 you should have finished both courses 1 and 2.
 * There are two correct answers: [0,1,2,3] and [0,2,1,3].
 * 
 * Example 3:
 * Input: numCourses = 1, prerequisites = []
 * Output: [0]
 * 
 * TOPOLOGICAL SORT EXPLANATION:
 * 
 * A topological sort is a linear ordering of vertices in a directed acyclic graph (DAG) 
 * such that for every directed edge (u, v), vertex u comes before v in the ordering.
 * 
 * Key insight: If we can find a topological ordering of all courses, then we can complete 
 * all courses. If no such ordering exists (due to cycles), then it's impossible.
 * 
 * Time Complexity: O(V + E) where V = numCourses, E = prerequisites
 * Space Complexity: O(V + E) for adjacency list and auxiliary data structures
 */
public class CourseScheduleIITopologicalSort {
    
    /**
     * Approach 1: Kahn's Algorithm (BFS-based Topological Sort)
     * 
     * Algorithm:
     * 1. Build adjacency list representation of the graph
     * 2. Calculate in-degree for each vertex
     * 3. Start with vertices having in-degree 0 (no prerequisites)
     * 4. Remove vertices one by one and update in-degrees of neighbors
     * 5. If we can remove all vertices, return the order; otherwise return empty array
     * 
     * This is the most intuitive approach for course scheduling problems.
     */
    public int[] findOrder(int numCourses, int[][] prerequisites) {
        // Step 1: Build adjacency list and in-degree array
        List<List<Integer>> adjacencyList = new ArrayList<>();
        int[] inDegree = new int[numCourses];
        
        // Initialize adjacency list
        for (int i = 0; i < numCourses; i++) {
            adjacencyList.add(new ArrayList<>());
        }
        
        // Build graph: prerequisite[0] depends on prerequisite[1]
        // So prerequisite[1] -> prerequisite[0] (edge from prerequisite to course)
        for (int[] prerequisite : prerequisites) {
            int course = prerequisite[0];
            int prereq = prerequisite[1];
            
            adjacencyList.get(prereq).add(course);  // prereq -> course
            inDegree[course]++;  // course has one more prerequisite
        }
        
        // Step 2: Find all courses with no prerequisites (in-degree = 0)
        Queue<Integer> queue = new LinkedList<>();
        for (int i = 0; i < numCourses; i++) {
            if (inDegree[i] == 0) {
                queue.offer(i);
            }
        }
        
        // Step 3: Process courses in topological order
        int[] result = new int[numCourses];
        int index = 0;
        
        while (!queue.isEmpty()) {
            int currentCourse = queue.poll();
            result[index++] = currentCourse;
            
            // Remove current course and update in-degrees of dependent courses
            for (int dependentCourse : adjacencyList.get(currentCourse)) {
                inDegree[dependentCourse]--;
                
                // If dependent course has no more prerequisites, add to queue
                if (inDegree[dependentCourse] == 0) {
                    queue.offer(dependentCourse);
                }
            }
        }
        
        // If we processed all courses, return the order; otherwise, cycle detected
        return index == numCourses ? result : new int[0];
    }
    
    /**
     * Approach 2: DFS-based Topological Sort
     * 
     * Algorithm:
     * 1. Perform DFS traversal
     * 2. Use three states: WHITE (unvisited), GRAY (visiting), BLACK (visited)
     * 3. If we encounter a GRAY node during DFS, there's a cycle
     * 4. Add nodes to result in post-order (when finishing DFS)
     * 5. Reverse the result to get correct topological order
     */
    public int[] findOrderDFS(int numCourses, int[][] prerequisites) {
        List<List<Integer>> adjacencyList = new ArrayList<>();
        
        // Initialize adjacency list
        for (int i = 0; i < numCourses; i++) {
            adjacencyList.add(new ArrayList<>());
        }
        
        // Build graph
        for (int[] prerequisite : prerequisites) {
            int course = prerequisite[0];
            int prereq = prerequisite[1];
            adjacencyList.get(prereq).add(course);
        }
        
        // 0 = WHITE (unvisited), 1 = GRAY (visiting), 2 = BLACK (visited)
        int[] state = new int[numCourses];
        List<Integer> result = new ArrayList<>();
        
        // Perform DFS from each unvisited node
        for (int i = 0; i < numCourses; i++) {
            if (state[i] == 0) {
                if (!dfs(i, adjacencyList, state, result)) {
                    return new int[0]; // Cycle detected
                }
            }
        }
        
        // Convert to array and reverse (DFS gives reverse topological order)
        Collections.reverse(result);
        return result.stream().mapToInt(i -> i).toArray();
    }
    
    private boolean dfs(int course, List<List<Integer>> adjacencyList, int[] state, List<Integer> result) {
        if (state[course] == 1) {
            return false; // Cycle detected (back edge)
        }
        if (state[course] == 2) {
            return true; // Already processed
        }
        
        state[course] = 1; // Mark as visiting (GRAY)
        
        // Visit all dependent courses
        for (int dependentCourse : adjacencyList.get(course)) {
            if (!dfs(dependentCourse, adjacencyList, state, result)) {
                return false;
            }
        }
        
        state[course] = 2; // Mark as visited (BLACK)
        result.add(course); // Add to result in post-order
        return true;
    }
    
    /**
     * Approach 3: Using HashMap (Alternative Kahn's Algorithm)
     * 
     * This approach uses HashMap for cleaner code, especially useful for 
     * sparse graphs or when course numbers are not consecutive.
     */
    public int[] findOrderHashMap(int numCourses, int[][] prerequisites) {
        Map<Integer, List<Integer>> adjacencyList = new HashMap<>();
        Map<Integer, Integer> inDegreeMap = new HashMap<>();
        
        // Initialize all courses
        for (int i = 0; i < numCourses; i++) {
            adjacencyList.put(i, new ArrayList<>());
            inDegreeMap.put(i, 0);
        }
        
        // Build graph
        for (int[] prerequisite : prerequisites) {
            int course = prerequisite[0];
            int prereq = prerequisite[1];
            
            adjacencyList.get(prereq).add(course);
            inDegreeMap.put(course, inDegreeMap.get(course) + 1);
        }
        
        // Find courses with no prerequisites
        Queue<Integer> queue = new LinkedList<>();
        for (Map.Entry<Integer, Integer> entry : inDegreeMap.entrySet()) {
            if (entry.getValue() == 0) {
                queue.offer(entry.getKey());
            }
        }
        
        List<Integer> result = new ArrayList<>();
        
        while (!queue.isEmpty()) {
            int currentCourse = queue.poll();
            result.add(currentCourse);
            
            // Process dependent courses
            for (int dependentCourse : adjacencyList.get(currentCourse)) {
                inDegreeMap.put(dependentCourse, inDegreeMap.get(dependentCourse) - 1);
                
                if (inDegreeMap.get(dependentCourse) == 0) {
                    queue.offer(dependentCourse);
                }
            }
        }
        
        return result.size() == numCourses ? 
               result.stream().mapToInt(i -> i).toArray() : 
               new int[0];
    }
    
    /**
     * Utility method to check if the course ordering is valid
     */
    public boolean isValidOrdering(int[] order, int[][] prerequisites) {
        if (order.length == 0) return false;
        
        Map<Integer, Integer> position = new HashMap<>();
        for (int i = 0; i < order.length; i++) {
            position.put(order[i], i);
        }
        
        // Check if all prerequisites come before their dependent courses
        for (int[] prerequisite : prerequisites) {
            int course = prerequisite[0];
            int prereq = prerequisite[1];
            
            if (position.get(prereq) >= position.get(course)) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * Utility method to visualize the course dependency graph
     */
    public void printGraph(int numCourses, int[][] prerequisites) {
        System.out.println("Course Dependency Graph:");
        Map<Integer, List<Integer>> adjacencyList = new HashMap<>();
        
        for (int i = 0; i < numCourses; i++) {
            adjacencyList.put(i, new ArrayList<>());
        }
        
        for (int[] prerequisite : prerequisites) {
            int course = prerequisite[0];
            int prereq = prerequisite[1];
            adjacencyList.get(prereq).add(course);
        }
        
        for (int i = 0; i < numCourses; i++) {
            System.out.print("Course " + i + " -> ");
            System.out.println(adjacencyList.get(i));
        }
        System.out.println();
    }
    
    public static void main(String[] args) {
        CourseScheduleIITopologicalSort solution = new CourseScheduleIITopologicalSort();
        
        System.out.println("=== Course Schedule II - Topological Sort Solutions ===\n");
        
        // Test Case 1: Basic example
        System.out.println("Test 1: numCourses = 2, prerequisites = [[1,0]]");
        int[][] prerequisites1 = {{1, 0}};
        solution.printGraph(2, prerequisites1);
        
        int[] result1 = solution.findOrder(2, prerequisites1);
        System.out.println("Kahn's Algorithm: " + Arrays.toString(result1));
        
        int[] result1DFS = solution.findOrderDFS(2, prerequisites1);
        System.out.println("DFS Algorithm: " + Arrays.toString(result1DFS));
        
        int[] result1HashMap = solution.findOrderHashMap(2, prerequisites1);
        System.out.println("HashMap Algorithm: " + Arrays.toString(result1HashMap));
        
        System.out.println("Valid ordering: " + solution.isValidOrdering(result1, prerequisites1));
        System.out.println();
        
        // Test Case 2: More complex example
        System.out.println("Test 2: numCourses = 4, prerequisites = [[1,0],[2,0],[3,1],[3,2]]");
        int[][] prerequisites2 = {{1, 0}, {2, 0}, {3, 1}, {3, 2}};
        solution.printGraph(4, prerequisites2);
        
        int[] result2 = solution.findOrder(4, prerequisites2);
        System.out.println("Kahn's Algorithm: " + Arrays.toString(result2));
        
        int[] result2DFS = solution.findOrderDFS(4, prerequisites2);
        System.out.println("DFS Algorithm: " + Arrays.toString(result2DFS));
        
        int[] result2HashMap = solution.findOrderHashMap(4, prerequisites2);
        System.out.println("HashMap Algorithm: " + Arrays.toString(result2HashMap));
        
        System.out.println("Valid ordering: " + solution.isValidOrdering(result2, prerequisites2));
        System.out.println();
        
        // Test Case 3: Impossible case (cycle)
        System.out.println("Test 3: numCourses = 2, prerequisites = [[1,0],[0,1]] (Cycle)");
        int[][] prerequisites3 = {{1, 0}, {0, 1}};
        solution.printGraph(2, prerequisites3);
        
        int[] result3 = solution.findOrder(2, prerequisites3);
        System.out.println("Kahn's Algorithm: " + Arrays.toString(result3));
        
        int[] result3DFS = solution.findOrderDFS(2, prerequisites3);
        System.out.println("DFS Algorithm: " + Arrays.toString(result3DFS));
        
        int[] result3HashMap = solution.findOrderHashMap(2, prerequisites3);
        System.out.println("HashMap Algorithm: " + Arrays.toString(result3HashMap));
        System.out.println();
        
        // Test Case 4: No prerequisites
        System.out.println("Test 4: numCourses = 3, prerequisites = [] (No dependencies)");
        int[][] prerequisites4 = {};
        solution.printGraph(3, prerequisites4);
        
        int[] result4 = solution.findOrder(3, prerequisites4);
        System.out.println("Kahn's Algorithm: " + Arrays.toString(result4));
        
        int[] result4DFS = solution.findOrderDFS(3, prerequisites4);
        System.out.println("DFS Algorithm: " + Arrays.toString(result4DFS));
        
        int[] result4HashMap = solution.findOrderHashMap(3, prerequisites4);
        System.out.println("HashMap Algorithm: " + Arrays.toString(result4HashMap));
        System.out.println();
        
        // Test Case 5: Complex dependency chain
        System.out.println("Test 5: Complex dependency chain");
        int[][] prerequisites5 = {{1, 0}, {2, 1}, {3, 2}, {4, 3}};
        solution.printGraph(5, prerequisites5);
        
        int[] result5 = solution.findOrder(5, prerequisites5);
        System.out.println("Kahn's Algorithm: " + Arrays.toString(result5));
        
        int[] result5DFS = solution.findOrderDFS(5, prerequisites5);
        System.out.println("DFS Algorithm: " + Arrays.toString(result5DFS));
        
        System.out.println("Valid ordering: " + solution.isValidOrdering(result5, prerequisites5));
        
        // Algorithm comparison
        System.out.println("\n=== Algorithm Comparison ===");
        System.out.println("1. Kahn's Algorithm (BFS):");
        System.out.println("   - Intuitive for course scheduling");
        System.out.println("   - Processes nodes in breadth-first manner");
        System.out.println("   - Easy to implement and understand");
        System.out.println("   - Natural for problems requiring 'level-by-level' processing");
        
        System.out.println("2. DFS-based Topological Sort:");
        System.out.println("   - Uses depth-first traversal");
        System.out.println("   - Can detect cycles during traversal");
        System.out.println("   - Requires post-order processing and reversal");
        System.out.println("   - More memory efficient for sparse graphs");
        
        System.out.println("3. HashMap-based (Kahn's variant):");
        System.out.println("   - Better for sparse graphs or non-consecutive node labels");
        System.out.println("   - More flexible data structure handling");
        System.out.println("   - Slightly more overhead but cleaner code");
    }
} 