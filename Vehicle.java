import java.util.ArrayList;
import java.util.List;

/**
 * Creates a class for a vehicle which runs through locations on a specific route
 * @author Group 10
 * @version 1.0
 */
public class Vehicle {

    /**
     * Creates
     */
    private static ArrayList<Vehicle> totalVehicles = new ArrayList<>();
    // private Random random = new Random();

    private int currentCapacity;
    private int totalCapacity;
    private String type;
    private int id;
    private String direction;
    private boolean status;
    private boolean movement;
    private Route route;
    private Location currentLocation;
    private Location nextLocation;

    public Vehicle(int capacity, String type, int id, String direction, Route route, Location currentLocation) {
        this.currentCapacity = 0;
        this.totalCapacity = capacity;
        this.type = type;
        this.id = id;
        this.direction = direction;
        this.status = true;
        this.movement = false;
        this.route = route;
        this.currentLocation = currentLocation;
        this.nextLocation = getNextLocation();

        totalVehicles.add(this);
    }

    private Location getNextLocation() {
        if (route == null || currentLocation == null) {
            return null;
        }
        
        List<Location> routeLocations = route.getLocations();
        int currentIndex = routeLocations.indexOf(currentLocation);
        
        if (currentIndex == -1) {
            return null;
        }
        
        // Find the destination location
        Location destination = null;
        for (Location loc : routeLocations) {
            if (loc.getName().equals(direction)) {
                destination = loc;
                break;
            }
        }
        
        if (destination == null) {
            return null;
        }
        
        int destIndex = routeLocations.indexOf(destination);
        if (currentIndex < destIndex) {
            return routeLocations.get(currentIndex + 1);
        } else if (currentIndex > destIndex) {
            return routeLocations.get(currentIndex - 1);
        }
        
        return null;
    }

    public String getType() {
        return type;
    }

    public int getId() {
        return id;
    }

    public String getDirection() {
        return direction;
    }

    public static ArrayList<Vehicle> getTotalVehicles() {
        return totalVehicles;
    }

    public Route getRoute() {
        return route;
    }
    
    public void setCurrentRoute(Route route) {
        this.route = route;
    }

    public void setCurrentPosition(int position) {
        if (route != null && position >= 0 && position < route.getLocations().size()) {
            this.currentLocation = route.getLocations().get(position);
        }
    }

    public Location getCurrentLocation() {
        return currentLocation;
    }

    @Override
    public String toString() {
        return this.type + " " + this.id + " is at " + this.currentLocation + " on route " + this.route;
    }
}
