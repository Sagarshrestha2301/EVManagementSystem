package com.taylorsuniversity.ev.usermanagement;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.logging.Level;

public class UserDAO {
    private static final String FILE_NAME = "users.dat";
    private static final String COUNTER_FILE_NAME = "user_counter.dat";
    private static final Logger LOGGER = Logger.getLogger(UserDAO.class.getName());
    private static final Object FILE_LOCK = new Object();

    private File getUsersFile() {
        // Use user's home directory for storing data files
        String userHome = System.getProperty("user.home");
        File appDir = new File(userHome, ".evmanagement");
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        return new File(appDir, FILE_NAME);
    }

    private File getCounterFile() {
        // Use user's home directory for storing data files
        String userHome = System.getProperty("user.home");
        File appDir = new File(userHome, ".evmanagement");
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        return new File(appDir, COUNTER_FILE_NAME);
    }

    private int loadCounter() {
        synchronized (FILE_LOCK) {
            File file = getCounterFile();
            if (!file.exists()) {
                return 1;
            }
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                return (int) ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
                LOGGER.log(Level.WARNING, "Error loading counter, starting with 1", e);
                return 1;
            }
        }
    }

    private void saveCounter(int counter) {
        synchronized (FILE_LOCK) {
            File file = getCounterFile();
            File parentDir = file.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
            }
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
                oos.writeObject(counter);
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "Error saving counter", e);
            }
        }
    }

    public List<User> loadUsers() {
        synchronized (FILE_LOCK) {
            List<User> users = new ArrayList<>();
            File file = getUsersFile();
            if (!file.exists()) {
                LOGGER.info("users.dat does not exist yet, returning empty list");
                return users;
            }

            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                Object obj = ois.readObject();
                if (obj instanceof List) {
                    users = (List<User>) obj;
                    users.removeIf(user -> user.getEmail() == null);
                }
                LOGGER.info("Loaded " + users.size() + " users from users.dat");
            } catch (EOFException e) {
                LOGGER.info("Empty or corrupted users.dat file");
            } catch (IOException | ClassNotFoundException e) {
                LOGGER.log(Level.SEVERE, "Error loading users", e);
            }
            return users;
        }
    }

    public void saveUser(User user) {
        synchronized (FILE_LOCK) {
            LOGGER.info("Attempting to save user: " + user.getEmail());
            List<User> users = loadUsers();
            users.removeIf(u -> u.getEmail() != null && u.getEmail().equals(user.getEmail()));
            users.add(user);

            File file = getUsersFile();
            File parentDir = file.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
            }

            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
                oos.writeObject(users);
                LOGGER.info("Successfully saved user: " + user.getEmail() + " to users.dat");
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "Error saving user: " + user.getEmail(), e);
            }
        }
    }

    public User findUserByEmail(String email) {
        return loadUsers().stream()
                .filter(u -> u.getEmail() != null && u.getEmail().equals(email))
                .findFirst()
                .orElse(null);
    }

    public String generateUserId() {
        int counter = loadCounter();
        String userId = "U" + String.format("%06d", counter); // e.g., "U000001"
        saveCounter(counter + 1);
        return userId;
    }

    public void debugUsers() {
        List<User> users = loadUsers();
        LOGGER.info("Current users in users.dat: " + users);
    }
}