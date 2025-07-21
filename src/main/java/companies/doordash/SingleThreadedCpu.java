package companies.doordash;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;

public class SingleThreadedCpu {

//    class Task {
//        int index;
//        int enqTime;
//        int processTime;
//
//        public Task(int index, int enqTime, int processTime) {
//            this.index = index;
//            this.enqTime = enqTime;
//            this.processTime = processTime;
//        }
//    }
//
//    public int[] getOrder(int[][] tasks) {
//        List<Task> allTasks = new ArrayList<>();
//        for (int i = 0; i < tasks.length; i++) {
//            allTasks.add(new Task(i, tasks[i][0], tasks[i][1]));
//        }
//
//        Collections.sort(allTasks, (t1, t2) -> t1.enqTime - t2.enqTime);
//
//        PriorityQueue<Task> minHeap = new PriorityQueue<>((t1, t2) -> {
//            if (t1.processTime == t2.processTime) {
//                return t1.index - t2.index;
//            }
//            return t1.processTime - t2.processTime;
//        });
//
//        int currTime = 0;
//        int[] res = new int[tasks.length];
//        int resIdx = 0;
//        int taskIdx = 0;
//
//        while (resIdx < tasks.length) {
//            while (taskIdx < allTasks.size() && currTime >= allTasks.get(taskIdx).enqTime) {
//                minHeap.offer(allTasks.get(taskIdx++));
//            }
//
//            if (!minHeap.isEmpty()) {
//                var task = minHeap.poll();
//                var enqueueTime = task.enqTime;
//                var index = task.index;
//                var processingTime = task.processTime;
//
//                res[resIdx++] = index;
//                currTime += processingTime;
//            } else {
//                currTime = allTasks.get(taskIdx).enqTime;
//            }
//        }
//
//        if (resIdx == tasks.length) {
//            return res;
//        }
//        return new int[0];
//    }


    class Task {
        int index;
        int enqTime;
        int processTime;

        public Task(int index, int enqTime, int processTime) {
            this.index = index;
            this.enqTime = enqTime;
            this.processTime = processTime;
        }
    }

    public int[] getOrder(int[][] tasks) {

        List<Task> tasksList = new ArrayList<>();

        for(int i = 0; i < tasks.length; i++) {
            tasksList.add(new Task(i, tasks[i][0], tasks[i][1]));
        }

        Collections.sort(tasksList, (t1, t2) -> t1.enqTime - t2.enqTime);

        PriorityQueue<Task> minHeap = new PriorityQueue<>((t1, t2) -> {
            if(t1.processTime == t2.processTime) {
                return t1.index - t2.index;
            }
            return t1.processTime - t2.processTime;
        });

        int taskIdx = 0, resIdx = 0, currTime = 0;
        int[] res = new int[tasks.length];


        while(resIdx < tasks.length) {
            while (taskIdx < tasks.length && currTime >= tasksList.get(taskIdx).enqTime) {
                minHeap.offer(tasksList.get(taskIdx++));
            }

            if(!minHeap.isEmpty()) {
                var task = minHeap.poll();
                currTime = currTime + task.processTime;
                var index = task.index;
                res[resIdx++] = index;
            } else {
                currTime = tasksList.get(taskIdx).enqTime;
            }
        }

        if(resIdx == tasks.length) {
            return res;
        }
        return new int[0];
    }

    public static void main(String[] args) {
        var sol = new SingleThreadedCpu();

        System.out.println(Arrays.toString(
                sol.getOrder(
                        new int[][]{
                                {1, 2},
                                {2, 4},
                                {3, 2},
                                {4, 1},
                        }
                )
        ));

    }
}
