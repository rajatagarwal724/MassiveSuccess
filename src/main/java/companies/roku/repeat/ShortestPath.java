package companies.roku.repeat;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

public class ShortestPath {
   int[][] DIRECTIONS = new int[][] {
           {-1, 0},
           {1, 0},
           {0, 1},
           {0, -1}
   };

   record State(int row, int col, Set<Character> keys) {}

   public int shortestPathAllKeys(String[] grid) {
       int startRow = -1, startCol = -1, totalKeys = 0;
       int R = grid.length, C = grid[0].length();
       for(int i = 0; i < grid.length; i++) {
           for(int j = 0; j < grid[0].length(); j++) {
               char elem = grid[i].charAt(j);

               if(elem == '@') {
                   startRow = i;
                   startCol = j;
               } else if (Character.isLetter(elem) && Character.isLowerCase(elem)) {
                   totalKeys++;
               }
           }
       }

       Set<Character> initalKeys = new HashSet<>();
       State initalState = new State(startRow, startCol, initalKeys);
       Queue<State> queue = new LinkedList<>();
       Set<State> visited = new HashSet<>();

       queue.offer(initalState);
       visited.add(initalState);
       int steps = 0;

       while(!queue.isEmpty()) {
           int size = queue.size();
           steps++;
           for (int i = 0; i < size; i++) {
               var node = queue.poll();
               int row = node.row;
               int col = node.col;
               var keys = node.keys;

               if (keys.size() == totalKeys) {
                   return steps;
               }

               for (int[] dir: DIRECTIONS) {
                   int newRow = row + dir[0];
                   int newCol = col + dir[1];

                   if (0 <= newRow && newRow < R && 0 <= newCol && newCol < C) {
                       char cell = grid[newRow].charAt(newCol);
                       Character collectedKey = null;
                       if (cell == '#') {
                           continue;
                       } else if (Character.isLowerCase(cell)) {
                           collectedKey = cell;
                       } else if (Character.isUpperCase(cell) && !keys.contains(Character.toLowerCase(cell))) {
                           continue;
                       }
                       var newKeys = new HashSet<>(keys);
                       if (null != collectedKey) {
                           newKeys.add(collectedKey);
                       }

                       if (!visited.contains(new State(newRow, newCol, newKeys))) {
                           visited.add(new State(newRow, newCol, newKeys));
                           queue.offer(new State(newRow, newCol, newKeys));
                       }
                   }
               }
           }
       }
       return -1;
   }

    public static void main(String[] args) {
        var sol = new ShortestPath();
//        System.out.println(
//                sol.shortestPathAllKeys(
//                        new String[]{
//                                {"@.a.."},
//                                {""}
//                        }
//                )
//        );
    }
}
