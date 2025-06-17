import java.util.ArrayList;
import java.util.List;

public class Location {
    private String name;
    private int totalPassengers;
    private boolean status;
    private List<Vehicle> vehicles;
    private String id;
    // private static List<Location> locations = new ArrayList<>();

    /* CONSTRUCTERS */
    public Location(String name, String id, int totalPassengers, boolean status) {
        // if (!id.matches("[a-zA-Z0-9]+")) {
        //     throw new IllegalArgumentException("Location ID must be alphanumeric.");
        // }
        // if (id.length() > 100) {
        //     throw new IllegalArgumentException("Location ID can not exceed 100 characters.");
        // }
        this.name = name;
        this.id = id;
        this.totalPassengers = totalPassengers;
        this.status = status;
        this.vehicles = new ArrayList<>();
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

    @Override
    public String toString() {
        return this.name;
    }

    /* METHODS */
    // public static List<Location> getAllLocations() {
    //     return locations;
    // }
}