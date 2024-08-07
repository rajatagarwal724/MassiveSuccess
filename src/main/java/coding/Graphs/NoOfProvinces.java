package coding.Graphs;

public class NoOfProvinces {
    public int findCircleNum(int[][] isConnected) {
        int provinces = 0;
        int vertices = isConnected.length;
        boolean[] visited = new boolean[vertices];

        for (int vertex = 0; vertex < visited.length; vertex++) {
            if (!visited[vertex]) {
                dfs(visited, vertex, isConnected);
                provinces++;
            }
        }
        return provinces;
    }

    private void dfs(boolean[] visited, int vertex, int[][] isConnected) {
        visited[vertex] = true;
        int[] connectedGraph = isConnected[vertex];
        for (int neighbour = 0; neighbour < connectedGraph.length; neighbour++) {
            if (neighbour != vertex && !visited[neighbour] && isConnected[vertex][neighbour] == 1) {
                dfs(visited, neighbour, isConnected);
            }
        }
    }

    public static void main(String[] args) {
        var solution = new NoOfProvinces();

        int[][] test1 = {{1,1,0}, {1,1,0}, {0,0,1}};
        int[][] test2 = {{1,0,0,1}, {0,1,1,0}, {0,1,1,0}, {1,0,0,1}};
        int[][] test3 = {{1,0,0}, {0,1,0}, {0,0,1}};

        System.out.println(solution.findCircleNum(test1));  // Expected output: 2
        System.out.println(solution.findCircleNum(test2));  // Expected output: 2
        System.out.println(solution.findCircleNum(test3));  // Expected output: 3
    }
}
