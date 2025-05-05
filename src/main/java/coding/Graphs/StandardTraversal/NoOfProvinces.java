package coding.Graphs.StandardTraversal;

public class NoOfProvinces {
    public int findCircleNum(int[][] isConnected) {

        int noOfProvinces = 0;
        int vertices = isConnected.length;
        boolean[] isVisited = new boolean[vertices];

        for(int vertex = 0; vertex < vertices; vertex++) {
            if (!isVisited[vertex]) {
                dfs(vertex, isVisited, isConnected);
                noOfProvinces++;
            }
        }

        return noOfProvinces;
    }

    private void dfs(int vertex, boolean[] isVisited, int[][] isConnected) {
        isVisited[vertex] = true;
        int[] neighbours = isConnected[vertex];
        for (int neighbour = 0; neighbour < neighbours.length; neighbour++) {
            if (neighbour != vertex && !isVisited[neighbour] && isConnected[vertex][neighbour] == 1) {
                dfs(neighbour, isVisited, isConnected);
            }
        }
    }

    public static void main(String[] args) {
        var sol = new NoOfProvinces();
        System.out.println(sol.findCircleNum(
                new int[][]{
                        new int[] {1,1,0},
                        new int[] {1,1,0},
                        new int[] {0,0,1}
                }
        ));

        System.out.println(sol.findCircleNum(
                new int[][]{
                        new int[] {1,0,0},
                        new int[] {0,1,0},
                        new int[] {0,0,1}
                }
        ));
    }
}
