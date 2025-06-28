import java.util.*;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        List<Station> stations = generateStations();

        System.out.println("=== Smart Campus Bike-Sharing System ===");

        // Step 1: Display station overview
        System.out.println("\nStation Overview:");
        System.out.printf("%-8s %-10s %-12s %-12s\n", "Name", "Bikes", "EmptySlots", "Distance");
        for (Station s : stations) {
            System.out.printf("%-8s %-10d %-12d %-12.1f\n",
                    s.name, s.bikes, s.emptySlots, s.distanceFromUser);
        }

        // Step 1: User Recommendation
        System.out.println("\nStep 1: User Recommendation - Greedy vs Simple Sort");
        System.out.print("Enter request type (borrow/return): ");
        String type = scanner.nextLine().trim();

        long totalGreedyNs = 0, totalSortNs = 0;
        Station greedyResult = null, sortResult = null;
        int trials = 1000;

        for (int i = 0; i < trials; i++) {
            long startG = System.nanoTime();
            greedyResult = greedyRecommendation(type, stations);
            long endG = System.nanoTime();
            totalGreedyNs += (endG - startG);

            long startS = System.nanoTime();
            sortResult = simpleSortRecommendation(type, stations);
            long endS = System.nanoTime();
            totalSortNs += (endS - startS);
        }

        long avgGreedy = totalGreedyNs / trials;
        long avgSort = totalSortNs / trials;

        System.out.println("Greedy Result: " + (greedyResult != null ? greedyResult.name : "None"));
        System.out.println("Simple Sort Result: " + (sortResult != null ? sortResult.name : "None"));
        System.out.println("Greedy Avg Time: " + avgGreedy + " ns (O(n))");
        System.out.println("Simple Sort Avg Time: " + avgSort + " ns (O(n log n))");

        Map<String, Double> userMap = new LinkedHashMap<>();
        userMap.put("Greedy", (double) avgGreedy);
        userMap.put("Simple Sort", (double) avgSort);
        ChartDisplay.showBarChart("User Recommendation Algorithms", userMap, "Time (ns)");

        // Step 2: Admin Dispatch - Dijkstra vs A*
        System.out.println("\nStep 2: Admin Dispatch - Dijkstra vs A*");
        List<List<Dijkstra.Edge>> graph = buildRandomWeightedGraph(60);
        System.out.print("Enter start station ID (0–59): ");
        int from = scanner.nextInt();
        System.out.print("Enter destination station ID (0–59): ");
        int to = scanner.nextInt();

        System.out.println("\n[Admin Mode]");
        System.out.println("Rebalancing vehicle from S" + from + " to S" + to + "...");

        long startD = System.nanoTime();
        List<Integer> dPath = Dijkstra.shortestPath(graph, from, to);
        int dCost = Dijkstra.pathCost(graph, dPath);
        long endD = System.nanoTime();
        long dijkstraTime = endD - startD;

        long startA = System.nanoTime();
        List<Integer> aPath = Dijkstra.aStarPath(graph, from, to, stations);
        int aCost = Dijkstra.pathCost(graph, aPath);
        long endA = System.nanoTime();
        long aStarTime = endA - startA;

        System.out.println("Dijkstra path: " + pathToString(dPath) + " (Total cost: " + dCost + ")");
        System.out.println("A* path: " + pathToString(aPath) + " (Total cost: " + aCost + ")");
        System.out.printf("Dijkstra time: %.4f ms\n", dijkstraTime / 1_000_000.0);
        System.out.printf("A* time: %.4f ms\n", aStarTime / 1_000_000.0);

        Map<String, Double> adminMap = new LinkedHashMap<>();
        adminMap.put("Dijkstra", (double) dijkstraTime);
        adminMap.put("A*", (double) aStarTime);
        ChartDisplay.showBarChart("Admin Dispatch Algorithms", adminMap, "Time (ns)");
    }

    static String pathToString(List<Integer> path) {
        if (path == null || path.isEmpty()) return "None";
        StringBuilder sb = new StringBuilder("S" + path.get(0));
        for (int i = 1; i < path.size(); i++) {
            sb.append(" → S").append(path.get(i));
        }
        return sb.toString();
    }

    static List<Station> generateStations() {
        List<Station> stations = new ArrayList<>();
        Random rand = new Random();
        for (int i = 0; i < 60; i++) {
            String name = "S" + i;
            int bikes = rand.nextInt(20);
            int slots = rand.nextInt(20);
            double dist = 1000 + rand.nextInt(5000);
            int x = rand.nextInt(100);
            int y = rand.nextInt(100);
            stations.add(new Station(name, bikes, slots, dist, x, y));
        }
        return stations;
    }

    static Station greedyRecommendation(String type, List<Station> stations) {
        return stations.stream()
                .filter(s -> (type.equals("borrow") && s.bikes > 0) ||
                        (type.equals("return") && s.emptySlots > 0))
                .min(Comparator.comparingDouble(s -> s.distanceFromUser))
                .orElse(null);
    }

    static Station simpleSortRecommendation(String type, List<Station> stations) {
        List<Station> valid = new ArrayList<>();
        for (Station s : stations) {
            if ((type.equals("borrow") && s.bikes > 0) ||
                    (type.equals("return") && s.emptySlots > 0)) {
                valid.add(s);
            }
        }
        valid.sort(Comparator.comparingDouble(s -> s.distanceFromUser));
        return valid.isEmpty() ? null : valid.get(0);
    }

    static List<List<Dijkstra.Edge>> buildRandomWeightedGraph(int size) {
        Random rand = new Random();
        List<List<Dijkstra.Edge>> graph = new ArrayList<>();
        for (int i = 0; i < size; i++) graph.add(new ArrayList<>());

        for (int i = 0; i < size; i++) {
            int connections = 2 + rand.nextInt(3); // 2–4 connections per node
            for (int j = 0; j < connections; j++) {
                int neighbor = rand.nextInt(size);
                if (neighbor != i) {
                    int weight = 1 + rand.nextInt(15); // weight: 1–15
                    graph.get(i).add(new Dijkstra.Edge(neighbor, weight));
                    graph.get(neighbor).add(new Dijkstra.Edge(i, weight)); // bidirectional
                }
            }
        }
        return graph;
    }
}
