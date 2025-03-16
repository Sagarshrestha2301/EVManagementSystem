package com.taylorsuniversity.ev.routeplanning;

import com.taylorsuniversity.ev.charginginfrastructure.ChargingStation;
import com.taylorsuniversity.ev.charginginfrastructure.ChargingStationController;
import com.taylorsuniversity.ev.charginginfrastructure.ChargingStationDTO;
import com.taylorsuniversity.ev.usermanagement.User;
import com.taylorsuniversity.ev.util.Location;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TripPlannerController {
    private final RouteGraph routeGraph;
    private final TripDAO tripDAO;
    private final ChargingStationController chargingStationController;
    private static final double DEFAULT_SPEED_KMH = 60.0;
    private static final double ENERGY_PER_KM_KWH = 0.2;

    public TripPlannerController() {
        this.routeGraph = new RouteGraph();
        this.tripDAO = new TripDAO();
        this.chargingStationController = new ChargingStationController();
        initializeGraph();
    }

    private void initializeGraph() {
        List<ChargingStationDTO> stations = chargingStationController.getAllChargingStations();
        for (int i = 0; i < stations.size() - 1; i++) {
            ChargingStationDTO src = stations.get(i);
            ChargingStationDTO dest = stations.get(i + 1);
            Location srcLoc = new Location(src.getName(), src.getLatitude(), src.getLongitude());
            Location destLoc = new Location(dest.getName(), dest.getLatitude(), dest.getLongitude());
            double distance = chargingStationController.calculateDistance(srcLoc, destLoc);
            routeGraph.addEdge(srcLoc, destLoc, distance, 0.0);
        }
    }

    public Trip planTrip(User user, Location start, Location end) {
        if (user == null || start == null || end == null) {
            throw new IllegalArgumentException("User, start, and end locations cannot be null");
        }
        List<Location> path = routeGraph.findShortestPath(start, end);
        if (path.isEmpty() || !path.get(0).equals(start) || !path.get(path.size() - 1).equals(end)) {
            throw new IllegalArgumentException("No valid path found between " + start.getName() + " and " + end.getName());
        }

        String tripId = "TRIP_" + System.currentTimeMillis();
        String userEmail = user.getEmail();
        double batteryRange = user.getBatteryRange() > 0 ? user.getBatteryRange() : 300.0;
        double currentChargeLevel = user.getCurrentChargeLevel() > 0 ? user.getCurrentChargeLevel() : 100.0;
        String vehicleModel = user.getVehicleModel() != null ? user.getVehicleModel() : "Tata Nexon EV";

        Trip trip = new Trip(tripId, userEmail, start, end, new ArrayList<>());
        double totalDistanceKm = routeGraph.calculatePathDistance(path);
        double energyConsumptionKWh = totalDistanceKm * ENERGY_PER_KM_KWH;

        double availableRange = (currentChargeLevel / 100.0) * batteryRange;
        List<ChargingStation> chargingStops = new ArrayList<>();
        if (totalDistanceKm > availableRange) {
            List<ChargingStationDTO> allStations = chargingStationController.getAllChargingStations();
            double remainingDistance = totalDistanceKm;
            double coveredDistance = 0.0;
            Location current = start;

            while (remainingDistance > availableRange) {
                ChargingStationDTO nearestAvailable = null;
                double minDistance = Double.MAX_VALUE;

                for (ChargingStationDTO station : allStations) {
                    if (!station.getName().equals(current.getName()) && !station.getName().equals(end.getName()) &&
                            station.getStatus().equals("AVAILABLE")) {
                        Location stationLoc = new Location(station.getName(), station.getLatitude(), station.getLongitude());
                        double distToStation = chargingStationController.calculateDistance(current, stationLoc);
                        if (distToStation < minDistance && distToStation <= availableRange) {
                            minDistance = distToStation;
                            nearestAvailable = station;
                        }
                    }
                }

                if (nearestAvailable != null) {
                    ChargingStation stop = new ChargingStation(
                            nearestAvailable.getStationId(), nearestAvailable.getName(), nearestAvailable.getStatus(),
                            nearestAvailable.getChargerType(), nearestAvailable.getPowerOutput(),
                            nearestAvailable.getAvailablePorts(), nearestAvailable.getLatitude(),
                            nearestAvailable.getLongitude(), nearestAvailable.getCostPerKWh()
                    );
                    chargingStops.add(stop);
                    coveredDistance += minDistance;
                    remainingDistance -= minDistance;
                    current = new Location(stop.getName(), stop.getLatitude(), stop.getLongitude());
                } else {
                    throw new IllegalStateException("No available charging station found within range.");
                }
            }
            for (ChargingStation stop : chargingStops) {
                trip.addChargingStop(stop);
            }
        }

        trip.setRouteDetails(totalDistanceKm, energyConsumptionKWh);
        trip.setCurrentChargeLevel(currentChargeLevel);
        trip.setBatteryRange(batteryRange);
        trip.setVehicleModel(vehicleModel);

        try {
            tripDAO.saveTrip(trip);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save trip: " + e.getMessage(), e);
        }

        return trip;
    }

    public double estimateTripTime(double distanceKm) {
        if (distanceKm < 0) return 0.0;
        return distanceKm / DEFAULT_SPEED_KMH;
    }

    public List<TripDTO> getUserTrips(String userEmail) {
        List<Trip> trips = tripDAO.getTripsByUser(userEmail);
        List<TripDTO> tripDTOs = new ArrayList<>();
        for (Trip trip : trips) {
            tripDTOs.add(new TripDTO(trip));
        }
        return tripDTOs; //groks tripDTOs;
    }
}