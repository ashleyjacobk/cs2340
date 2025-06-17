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
    private String id;
    private String direction;
    private boolean status;
    private boolean movement;
    private Route route;
    private Location currentLocation;
    private Location nextLocation;
    private double speed;  // Speed in units per time
    private double arrivalTime;  // Time when vehicle will arrive at next location
    private boolean isAtLocation;  // Whether vehicle is at a location or between locations

    public Vehicle(int capacity, String type, String id, String direction, Route route, Location currentLocation) {
        if (!id.matches("[a-zA-Z0-9]+")) {
            throw new IllegalArgumentException("Vehicle ID must be alphanumeric.");
        }
        if (id.length() > 100) {
            throw new IllegalArgumentException("Vehicle ID can not exceed 100 characters.");
        }
        this.currentCapacity = 0;
        this.totalCapacity = capacity;
        this.type = type;
        this.id = id;
        this.direction = direction;
        this.status = true;
        this.movement = false;
        this.route = route;
        this.currentLocation = currentLocation;
        this.nextLocation = calculateNextLocation();
        this.speed = 0.0;
        this.arrivalTime = Double.POSITIVE_INFINITY;
        this.isAtLocation = true;

        totalVehicles.add(this);
    }

    private Location calculateNextLocation() {
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

    public String getId() {
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

    public Location getNextLocation() {
        return nextLocation;
    }

    public void setSpeed(double speed) {
        if (speed < 0) {
            throw new IllegalArgumentException("Speed cannot be negative");
        }
        this.speed = speed;
        updateArrivalTime();
    }

    public double getSpeed() {
        return speed;
    }

    public double getArrivalTime() {
        return arrivalTime;
    }

    public boolean isAtLocation() {
        return isAtLocation;
    }

    private void updateArrivalTime() {
        if (nextLocation == null || speed == 0) {
            arrivalTime = Double.POSITIVE_INFINITY;
            return;
        }
        double distance = currentLocation.getDistanceTo(nextLocation);
        arrivalTime = distance / speed;
    }

    public void updateState(double currentTime) {
        if (!isAtLocation && currentTime >= arrivalTime) {
            // Vehicle has arrived at next location
            currentLocation = nextLocation;
            nextLocation = calculateNextLocation();
            isAtLocation = true;
            updateArrivalTime();
        }
    }

    @Override
    public String toString() {
        if (isAtLocation) {
            return this.type + " " + this.id + " is at " + this.currentLocation + " on route " + getRoute().toString();
        } else {
            return this.type + " " + this.id + " is traveling from " + this.currentLocation + " to " + this.nextLocation + 
                   " on route " + getRoute().toString() + " (arriving at " + String.format("%.2f", arrivalTime) + ")";
        }
    }
}
