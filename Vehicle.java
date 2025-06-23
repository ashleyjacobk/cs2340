import java.util.ArrayList;
import java.util.List;

/**
 * Creates a class for a vehicle which runs through locations on a specific route
 * @author Group 10
 * @version 1.0
 */
public class Vehicle {
    private static ArrayList<Vehicle> totalVehicles = new ArrayList<>();

    private int currentCapacity;
    private int totalCapacity;
    private VehicleType type;
    private final String ID; // should be final since id shouldnt be changed
    private boolean status;
    private boolean movement;
    private Route route;
    private Location currentLocation;
    private Location nextLocation;
    private double speed; // in kph
    private boolean inTransit = false;

    public Vehicle(int capacity, VehicleType type, String id, Route route, Location currentLocation, double speed) {
        this.currentCapacity = 0;
        this.totalCapacity = capacity;
        this.type = type;
        this.ID = id;
        this.status = true;
        this.movement = false;
        this.route = route;
        this.currentLocation = currentLocation;
        this.nextLocation = getNextLocationFromRoute();
        this.speed = speed;
        this.inTransit = false;

        totalVehicles.add(this);
    }

    private Location getNextLocationFromRoute() {
        if (route == null) {
            return null;
        }
        
        List<Location> routeLocations = route.getLocations();
        if (routeLocations.isEmpty()) {
            return null;
        }
        int currentIndex = routeLocations.indexOf(currentLocation);
        
        if (currentIndex == routeLocations.size() - 1) {
            return routeLocations.get(0); // if at last location, return to first location
        } else if (currentIndex >= 0) {
            return routeLocations.get(currentIndex + 1); // return next location in the route
        }
        
        return null; // if current location is not found in the route
    }

    public boolean isInTransit() {
        return inTransit;
    }
    
    public Location getNextLocation() {
        return nextLocation;
    }

    public VehicleType getType() {
        return type;
    }

    public String getId() {
        return ID;
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
            arriveAt(route.getLocations().get(position));
        }
    }

    public Location getCurrentLocation() {
        return currentLocation;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        if (speed > 0) {
            this.speed = speed;
        }
    }

    public void setInTransit() {
        this.inTransit = true;
        this.currentLocation = null; // Clear current location when in transit
    }

    public void arriveAt(Location location) {
        this.inTransit = false;
        this.currentLocation = location;
    }

    @Override
    public String toString() {
        String locationStr = (currentLocation != null) ? currentLocation.getName() : "in transit";
        return this.type + " " + this.ID + " is at " + locationStr + " on route " + getRoute().toString();
    }
}
