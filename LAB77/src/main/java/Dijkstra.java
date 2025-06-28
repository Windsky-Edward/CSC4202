import java.util.*;

public class Dijkstra {
    public static class Edge {
        public int to;
        public int weight;

        public Edge(int to, int weight) {
            this.to = to;
            this.weight = weight;
        }
    }

    // Dijkstra
    public static List<Integer> shortestPath(List<List<Edge>> graph, int start, int end) {
        int n = graph.size();
        int[] dist = new int[n];
        int[] prev = new int[n];
        boolean[] visited = new boolean[n];
        Arrays.fill(dist, Integer.MAX_VALUE);
        Arrays.fill(prev, -1);

        dist[start] = 0;
        PriorityQueue<Integer> pq = new PriorityQueue<>(Comparator.comparingInt(i -> dist[i]));
        pq.add(start);

        while (!pq.isEmpty()) {
            int u = pq.poll();
            if (visited[u]) continue;
            visited[u] = true;

            for (Edge edge : graph.get(u)) {
                int v = edge.to;
                int weight = edge.weight;
                if (dist[u] + weight < dist[v]) {
                    dist[v] = dist[u] + weight;
                    prev[v] = u;
                    pq.add(v);
                }
            }
        }

        return reconstructPath(prev, start, end);
    }

    // A* 使用欧几里得坐标启发
    public static List<Integer> aStarPath(List<List<Edge>> graph, int start, int end, List<Station> stations) {
        int n = graph.size();
        int[] g = new int[n];
        int[] f = new int[n];
        int[] prev = new int[n];
        boolean[] visited = new boolean[n];
        Arrays.fill(g, Integer.MAX_VALUE);
        Arrays.fill(f, Integer.MAX_VALUE);
        Arrays.fill(prev, -1);

        g[start] = 0;
        f[start] = heuristic(stations.get(start), stations.get(end));

        PriorityQueue<Integer> pq = new PriorityQueue<>(Comparator.comparingInt(i -> f[i]));
        pq.add(start);

        while (!pq.isEmpty()) {
            int u = pq.poll();
            if (visited[u]) continue;
            visited[u] = true;

            if (u == end) break;

            for (Edge edge : graph.get(u)) {
                int v = edge.to;
                int tempG = g[u] + edge.weight;
                if (tempG < g[v]) {
                    g[v] = tempG;
                    f[v] = g[v] + heuristic(stations.get(v), stations.get(end));
                    prev[v] = u;
                    pq.add(v);
                }
            }
        }

        return reconstructPath(prev, start, end);
    }

    public static int pathCost(List<List<Edge>> graph, List<Integer> path) {
        if (path == null || path.size() < 2) return 0;
        int cost = 0;
        for (int i = 0; i < path.size() - 1; i++) {
            int u = path.get(i), v = path.get(i + 1);
            for (Edge edge : graph.get(u)) {
                if (edge.to == v) {
                    cost += edge.weight;
                    break;
                }
            }
        }
        return cost;
    }

    private static List<Integer> reconstructPath(int[] prev, int start, int end) {
        List<Integer> path = new ArrayList<>();
        for (int at = end; at != -1; at = prev[at]) {
            path.add(at);
        }
        Collections.reverse(path);
        return path.get(0) == start ? path : Collections.emptyList();
    }

    private static int heuristic(Station a, Station b) {
        double dx = a.x - b.x;
        double dy = a.y - b.y;
        return (int) Math.round(Math.sqrt(dx * dx + dy * dy));
    }
}
