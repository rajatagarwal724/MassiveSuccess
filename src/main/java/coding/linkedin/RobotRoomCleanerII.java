//package coding.linkedin;
//
//import java.util.HashSet;
//import java.util.Set;
//
//public class RobotRoomCleanerII {
//
//    record Cell(int row, int col) {}
//    Robot robot;
//
//    Set<String> visited = new HashSet<>();
//
//    int[][] DIRECTIONS = new int[][] {
//            {-1, 0},
//            {0, 1},
//            {1, 0},
//            {0, -1}
//    };
//
//    public void cleanRoom(Robot robot) {
//        this.robot = robot;
//        backtrack(0, 0, 0);
//    }
//
//    private void backtrack(int row, int col, int currDir) {
//        visited.add(row + ":" + col);
//        robot.clean();
//        for (int i = 0; i < DIRECTIONS.length; i++) {
//            int newD = (currDir + i) % 4;
//            int newRow = row + DIRECTIONS[newD][0];
//            int newCol = col + DIRECTIONS[newD][1];
//
//            if (!visited.contains(row + ":" + col) && robot.move()) {
//                backtrack(newRow, newCol, newD);
//                goBack();
//            }
//            robot.turnRight();
//        }
//    }
//
//    private void goBack() {
//
//        robot.turnRight();
//        robot.turnRight();
//
//        robot.move();
//
//        robot.turnRight();
//        robot.turnRight();
//    }
//}
//
//
//// This is the robot's control interface.
//// You should not implement it, or speculate about its implementation
//interface Robot {
//    // Returns true if the cell in front is open and robot moves into the cell.
//    // Returns false if the cell in front is blocked and robot stays in the current cell.
//    public boolean move();
//
//    // Robot will stay in the same cell after calling turnLeft/turnRight.
//    // Each turn will be 90 degrees.
//    public void turnLeft();
//    public void turnRight();
//
//    // Clean the current cell.
//    public void clean();
//}
