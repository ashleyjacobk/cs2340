import java.util.ArrayList;
import java.util.List;

public class Location {
    private String name;
    private int totalPassengers;
    private boolean status;
    private List<Vehicle> vehicles;
    // private static List<Location> locations = new ArrayList<>();

    /* CONSTRUCTERS */
    public Location(String name, int totalPassengers, boolean status) {
        this.name = name;
        this.totalPassengers = totalPassengers;
        this.status = status;
        this.vehicles = new ArrayList<>();
        // locations.add(this);
    }
    public Location(String name, int totalPassengers) {
        this(name, totalPassengers, true);
    }
    public Location(String name) {
        this(name, 0, true);
    }

    /* GETTERS */
    public String getName() {
        return name;
    }
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
    public String setName() {
        return name;
    }
    public int setTotalPassengers() {
        return totalPassengers;
    }
    public boolean setStatus() {
        return status;
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