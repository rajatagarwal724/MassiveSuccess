package coding.linkedin;

import java.util.Arrays;
import java.util.PriorityQueue;

public class ProcessTasksUsingServers {
    
    static class IdleServer {
        int weight;
        int index;

        public IdleServer(int weight, int index) {
            this.weight = weight;
            this.index = index;
        }
    }

    static class BusyServer {
        int freeAt;
        int index;
        int weight;

        public BusyServer(int freeAt, int index, int weight) {
            this.freeAt = freeAt;
            this.index = index;
            this.weight = weight;
        }
    }

    public int[] assignTasks(int[] servers, int[] tasks) {
        PriorityQueue<IdleServer> idleServers = new PriorityQueue<>(
                (s1, s2) -> {
                    if (s1.weight == s2.weight) {
                        return s1.index - s2.index;
                    }
                    return s1.weight - s2.weight;
                }
        );
        
        PriorityQueue<BusyServer> busyServers = new PriorityQueue<>(
                (s1, s2) -> {
                    if (s1.freeAt == s2.freeAt) {
                        if (s1.weight == s2.weight) {
                            return s1.index - s2.index;
                        }
                        return s1.weight - s2.weight;
                    }
                    return s1.freeAt - s2.freeAt;
                }
        );

        // Initialize all servers as idle
        for (int i = 0; i < servers.length; i++) {
            idleServers.offer(new IdleServer(servers[i], i));
        }

        int[] res = new int[tasks.length];
        int resIdx = 0;

        for (int currTime = 0; currTime < tasks.length; currTime++) {
            int taskDuration = tasks[currTime];

            while (!busyServers.isEmpty() && busyServers.peek().freeAt <= currTime) {
                var busyServer = busyServers.poll();
                idleServers.offer(new IdleServer(busyServer.weight, busyServer.index));
            }

            if (!idleServers.isEmpty()) {
                var idleServer = idleServers.poll();
                res[resIdx++] = idleServer.index;
                busyServers.offer(new BusyServer(currTime + taskDuration, idleServer.index, idleServer.weight));
            } else {
                var nextServer = busyServers.poll();
                res[resIdx++] = nextServer.index;
                busyServers.offer(new BusyServer(nextServer.freeAt + taskDuration, nextServer.index, nextServer.weight));
            }
        }

        return res;
    }

    public static void main(String[] args) {
        var sol = new ProcessTasksUsingServers();
        System.out.println(
                Arrays.toString(sol.assignTasks(new int[] {3,3,2}, new int[] {1,2,3,2,1,2}))
        );
    }
}
