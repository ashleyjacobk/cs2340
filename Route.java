import java.util.ArrayList;
import java.util.List;

public class Route {
    private String routeId;
    private VehicleType vehicleType;
    private List<Location> locations;
    private List<Vehicle> vehicles;

    public Route(String routeID, VehicleType vehicleType) {
        this.routeId = routeID;
        this.vehicleType = vehicleType;
        locations = new ArrayList<Location>();
        vehicles = new ArrayList<Vehicle>();
    }

    public boolean addLocation(Location newLoc, int position) {
        if (position <= locations.size() && position >= 0) {
            locations.add(position, newLoc);
            return true;
        } else {
            // System.out.println("This position is invalid in the route");
            return false;
        }
    }

    public boolean add_vehicle(Vehicle newVehicle) {
        if (!vehicleType.equals(newVehicle.getType())) {
            throw new IllegalArgumentException("You can only add vehicles of type " + vehicleType + " to this route.");
        }
        // Prevent duplicates (by object reference or by ID)
        for (Vehicle v : vehicles) {
            if (v.getId().equals(newVehicle.getId())) {
                return false; // already present
            }
        }
        vehicles.add(newVehicle);
        return true;
    }

    public Location removeLocation(int position) {
        if (position < 0 || position >= locations.size()) {
            System.out.println("This position is not in the route");
            return null;
        }
        return locations.remove(position);
    }

    public void display_route() {
        if (locations.isEmpty()) {
            System.out.println("[]");
            return;
        }
        System.out.print("[");
        for (int i = 0; i < locations.size(); i++) {
            System.out.print(locations.get(i).getName());
            if (i < locations.size() - 1) {
                System.out.print(", ");
            }
        }
        System.out.println("]");
    }

    public void display_vehicles() {
        for (int i = 0; i < vehicles.size(); i++) {
            Vehicle vehicle = vehicles.get(i);
            System.out.print(vehicle.getType() + ": " + vehicle.getId() + " ");
        }
        System.out.println();
    }

    public List<Location> getLocations() {
        return locations;
    }

    public List<Vehicle> getVehicles() {
        return vehicles;
    }

    public String getRouteId() {
        return routeId;
    }
    public VehicleType getVehicleType() {
        return vehicleType;
    }

    @Override
    public String toString() {
        return routeId;
    }
}