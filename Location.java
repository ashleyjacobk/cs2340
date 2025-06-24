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
    private double x, y; // coordinates

    /* CONSTRUCTERS */
    public Location(String name, String id, int totalPassengers, boolean status, double x, double y) {
        this.name = name;
        this.id = id;
        this.totalPassengers = totalPassengers;
        this.status = status;
        this.vehicles = new ArrayList<>();
        this.x = x;
        this.y = y;
    }
    public Location(String name, String id, int totalPassengers) {
        this(name, id, totalPassengers, true, 0.0, 0.0);
    }
    public Location(String name, String id) {
        this(name, id, 0, true, 0.0, 0.0);
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
    public double getX() {
        return x;
    }
    public double getY() {
        return y;
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

    /* METHODS */
    
    public double distanceTo(Location location) {
        double dx = this.x - location.getX();
        double dy = this.y - location.getY();
        return Math.sqrt(dx * dx + dy * dy);
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