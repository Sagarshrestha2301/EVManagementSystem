package com.taylorsuniversity.ev.routeplanning;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class TripDAO {
    private static final String FILE_NAME = "trips.dat";
    private static final Logger LOGGER = Logger.getLogger(TripDAO.class.getName());

    public List<Trip> loadTrips() {
        List<Trip> trips = new ArrayList<>();
        File file = new File(FILE_NAME);
        if (!file.exists()) return trips;

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            trips = (List<Trip>) ois.readObject();
        } catch (EOFException e) {
            LOGGER.info("Empty or corrupted trips.dat file");
        } catch (IOException | ClassNotFoundException e) {
            LOGGER.log(Level.SEVERE, "Error loading trips", e);
        }
        return trips;
    }

    public void saveTrip(Trip trip) throws IOException {
        if (trip == null) throw new IllegalArgumentException("Trip cannot be null");
        List<Trip> trips = loadTrips();
        trips.removeIf(t -> t.getTripId().equals(trip.getTripId()));
        trips.add(trip);
        saveTrips(trips);
    }

    public Trip findTripById(String tripId) {
        if (tripId == null) return null;
        return loadTrips().stream()
                .filter(t -> t.getTripId().equals(tripId))
                .findFirst()
                .orElse(null);
    }

    public List<Trip> getTripsByUser(String userEmail) {
        if (userEmail == null) return new ArrayList<>();
        return loadTrips().stream()
                .filter(t -> t.getUserEmail() != null && t.getUserEmail().equals(userEmail))
                .collect(Collectors.toList());
    }

    public List<Trip> readTrips() {
        return new ArrayList<>(loadTrips());
    }

    public void saveTrips(List<Trip> trips) throws IOException {
        if (trips == null) throw new IllegalArgumentException("Trips list cannot be null");
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            oos.writeObject(new ArrayList<>(trips));
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error saving trips", e);
            throw e;
        }
    }
}