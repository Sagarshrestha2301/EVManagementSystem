package com.taylorsuniversity.ev.routeplanning;

import com.taylorsuniversity.ev.util.Location;

import java.util.*;
import java.util.logging.Logger;
import java.util.logging.Level;

public class RouteGraph {
    private static final Logger LOGGER = Logger.getLogger(RouteGraph.class.getName());
    private Map<Location, Map<Location, Edge>> graph = new HashMap<>();

    public List<Location> findShortestPath(Location start, Location end) {
        if (start == null || end == null || !graph.containsKey(start) || !graph.containsKey(end)) {
            LOGGER.log(Level.WARNING, "Invalid path input: start={0}, end={1}", new Object[]{start, end});
            return Collections.emptyList();
        }

        Map<Location, Double> distances = new HashMap<>();
        Map<Location, Location> previous = new HashMap<>();
        PriorityQueue<Location> pq = new PriorityQueue<>(Comparator.comparingDouble(distances::get));
        Set<Location> visited = new HashSet<>();

        for (Location node : graph.keySet()) distances.put(node, Double.MAX_VALUE);
        distances.put(start, 0.0);
        pq.add(start);

        while (!pq.isEmpty()) {
            Location current = pq.poll();
            if (visited.contains(current)) continue;
            visited.add(current);

            if (current.equals(end)) break;

            Map<Location, Edge> neighbors = graph.getOrDefault(current, Collections.emptyMap());
            for (Map.Entry<Location, Edge> entry : neighbors.entrySet()) {
                Location neighbor = entry.getKey();
                Edge edge = entry.getValue();
                double newDist = distances.get(current) + edge.distance;
                if (newDist < distances.getOrDefault(neighbor, Double.MAX_VALUE)) {
                    distances.put(neighbor, newDist);
                    previous.put(neighbor, current);
                    pq.add(neighbor);
                }
            }
        }

        List<Location> path = new ArrayList<>();
        for (Location at = end; at != null; at = previous.get(at)) path.add(at);
        Collections.reverse(path);
        return path.size() > 1 && path.get(0).equals(start) ? path : Collections.emptyList();
    }

    public double calculatePathDistance(List<Location> path) {
        if (path == null || path.size() < 2) return 0.0;
        double totalDistance = 0.0;
        for (int i = 0; i < path.size() - 1; i++) {
            Location src = path.get(i);
            Location dest = path.get(i + 1);
            Edge edge = graph.getOrDefault(src, Collections.emptyMap()).get(dest);
            if (edge == null) return Double.MAX_VALUE; // Path broken
            totalDistance += edge.distance;
        }
        return totalDistance;
    }

    public static class Edge {
        Location destination;
        double distance; // km
        double elevationChange; // meters

        Edge(Location destination, double distance, double elevationChange) {
            if (distance < 0) throw new IllegalArgumentException("Distance cannot be negative");
            this.destination = destination;
            this.distance = distance;
            this.elevationChange = elevationChange;
        }
    }

    public RouteGraph() {
        // Initial edges moved to TripPlannerController.initializeGraph()
    }

    public void addEdge(Location src, Location dest, double distance, double elevationChange) {
        if (src == null || dest == null || src.equals(dest)) {
            LOGGER.log(Level.WARNING, "Invalid edge: src={0}, dest={1}", new Object[]{src, dest});
            return;
        }
        graph.computeIfAbsent(src, k -> new HashMap<>()).put(dest, new Edge(dest, distance, elevationChange));
        graph.computeIfAbsent(dest, k -> new HashMap<>()).put(src, new Edge(src, distance, -elevationChange));
        LOGGER.log(Level.INFO, "Added edge: {0} -> {1}, distance={2}km, elevation={3}m",
                new Object[]{src.getName(), dest.getName(), distance, elevationChange});
    }

    public List<Location> dijkstra(Location start, Location end) {
        return findShortestPath(start, end);
    }

    public double getDistance(Location src, Location dest) {
        Edge edge = graph.getOrDefault(src, Collections.emptyMap()).get(dest);
        return edge != null ? edge.distance : Double.MAX_VALUE;
    }

    public double getElevationChange(Location src, Location dest) {
        Edge edge = graph.getOrDefault(src, Collections.emptyMap()).get(dest);
        return edge != null ? edge.elevationChange : 0;
    }
}