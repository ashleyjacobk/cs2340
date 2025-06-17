import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class Location {
    private String name;
    private int totalPassengers;
    private boolean status;
    private List<Vehicle> vehicles;
    private String id;
    private Map<Location, Double> distances;  // Map of connected locations and their distances
    // private static List<Location> locations = new ArrayList<>();

    /* CONSTRUCTERS */
    public Location(String name, String id, int totalPassengers, boolean status) {
        if (!id.matches("[a-zA-Z0-9]+")) {
            throw new IllegalArgumentException("Location ID must be alphanumeric.");
        }
        if (id.length() > 100) {
            throw new IllegalArgumentException("Location ID can not exceed 100 characters.");
        }
        this.name = name;
        this.id = id;
        this.totalPassengers = totalPassengers;
        this.status = status;
        this.vehicles = new ArrayList<>();
        this.distances = new HashMap<>();
        // locations.add(this);
    }
    public Location(String name, String id, int totalPassengers) {
        this(name, id, totalPassengers, true);
    }
    public Location(String name, String id) {
        this(name, id, 0, true);
    }

    /* GETTERS */
    public String getName() {
        return name;
    }
    public String getId() { return id; }
    public int getTotalPassengers() {
        return totalPassengers;
    }
    public boolean getStatus() {
        return status;
    }
    public List<Vehicle> getVehicles() {
        return vehicles;
    }
    
    /* SETTERS */
    public void setName(String name) {
        this.name = name;
    }
    public void setTotalPassengers(int totalPassengers) {
        this.totalPassengers = totalPassengers;
    }
    public void setStatus(boolean status) {
        this.status = status;
    }

    public void setDistanceTo(Location other, double distance) {
        if (distance < 0) {
            throw new IllegalArgumentException("Distance cannot be negative");
        }
        distances.put(other, distance);
        other.distances.put(this, distance);  // Make it bidirectional
    }

    public double getDistanceTo(Location other) {
        Double distance = distances.get(other);
        if (distance == null) {
            throw new IllegalArgumentException("No distance defined between " + this.name + " and " + other.name);
        }
        return distance;
    }

    @Override
    public String toString() {
        return this.name;
    }

    /* METHODS */
    // public static List<Location> getAllLocations() {
    //     return locations;
    // }
}