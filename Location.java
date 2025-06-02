import java.util.ArrayList;
import java.util.List;

public class Location {
    private String identifier;
    private String name;
    private int totalPassengers;
    private boolean status;
    private List<Vehicle> vehicles;
    private static List<Location> locations = new ArrayList<>();

    /* CONSTRUCTERS */
    public Location(String identifier, String name, int totalPassengers, boolean status) {
        this.identifier = identifier;
        this.name = name;
        this.totalPassengers = totalPassengers;
        this.status = status;
        this.vehicles = vehicle.getVehiclesAtLocation();
        locations.add(this);
    }
    public Location(String identifier, String name, int totalPassengers) {
        this(identifier, name, totalPassengers, true);
    }
    public Location(String identifier, String name) {
        this(identifier, name, 0, true);
    }

    /* GETTERS */
    public String getIdentifier() {
        return identifier;
    }
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
    public String setIdentifier() {
        return identifier;
    }
    public String setName() {
        return name;
    }
    public int setTotalPassengers() {
        return totalPassengers;
    }
    public boolean setStatus() {
        return status;
    }

    /* METHODS */
    public List<Location> getAllLocations() {
        return locations;
    }

    
}