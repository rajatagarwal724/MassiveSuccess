package coding.topk;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

public class ClosestPointsToOrigin {

    public List<Point> findClosestPoints(Point[] points, int k) {
        Queue<Point> maxHeap = new PriorityQueue<>((p1, p2) -> p2.distFromOrigin() - p1.distFromOrigin());

        for(int i = 0; i < k; i++) {
            maxHeap.offer(points[i]);
        }

        for(int i = k; i < points.length; i++) {
            if (points[i].distFromOrigin() < maxHeap.peek().distFromOrigin()) {
                maxHeap.poll();
                maxHeap.offer(points[i]);
            }
        }

        return new ArrayList<>(maxHeap);
    }

    public static void main(String[] args) {
        var sol = new ClosestPointsToOrigin();

        sol.findClosestPoints(new Point[] { new Point(1, 2), new Point(1, 3) }, 1)
                .forEach(System.out::println);
        System.out.println("################");

        sol.findClosestPoints(new Point[] { new Point(1, 3), new Point(3, 4), new Point(2, -1) }, 2)
                .forEach(System.out::println);
        System.out.println("################");
    }
}

class Point {
    int x;
    int y;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int distFromOrigin() {
        // ignoring sqrt
        return (x * x) + (y * y);
    }

    @Override
    public String toString() {
        return "Point{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}
