package coding.linkedin;

import java.util.HashSet;
import java.util.Set;

public class RobotRoomCleaner {

    Robot robot;
    private int[][] dir = new int[][] {
            {-1, 0}, // UP
            {0, 1}, // RIGHT
            {1, 0}, // DOWN
            {0, -1} // LEFT
    };
    Set<String> visited = new HashSet<>();



    public void cleanRoom(Robot robot) {
        this.robot = robot;
        backtrack(0, 0, 0);
    }

    private void backtrack(int startRow, int startCol, int currDir) {
        visited.add(startRow + ":" + startCol);
        robot.clean();;

        for (int i = 0; i < dir.length; i++) {
            int newD = (currDir + i) % dir.length;
            int newRow = startRow + dir[newD][0];
            int newCol = startCol + dir[newD][1];

            if (!visited.contains(newRow + ":" + newCol) && robot.move()) {
                backtrack(newRow, newCol, newD);

                goback();
            }

            robot.turnRight();
        }
    }

    private void goback() {
        robot.turnRight();
        robot.turnRight();

        robot.move();

        robot.turnRight();
        robot.turnRight();
    }
}


  // This is the robot's control interface.
  // You should not implement it, or speculate about its implementation
  interface Robot {
      // Returns true if the cell in front is open and robot moves into the cell.
      // Returns false if the cell in front is blocked and robot stays in the current cell.
      public boolean move();

      // Robot will stay in the same cell after calling turnLeft/turnRight.
      // Each turn will be 90 degrees.
      public void turnLeft();
      public void turnRight();

      // Clean the current cell.
      public void clean();
  }

