import java.util.ArrayList;

/**
 * Class to define a vehicle within a route
 */
public class Vehicle {

    public static ArrayList<Vehicle> totalVehicles = new ArrayList<>();

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

    public Vehicle(int capacity, String type, String id, String lastStop, Route route, Location currentLocation) {
        this.currentCapacity = 0;
        this.totalCapacity = capacity;
        this.type = type;
        this.id = id;
        this.direction = lastStop;
        this.status = true;
        this.movement = false;
        this.route = route;
        this.currentLocation = currentLocation;
        this.nextLocation = getNextLocation();

        totalVehicles.add(this);
    }

    private void Location getNextLocation() {

    }
}
