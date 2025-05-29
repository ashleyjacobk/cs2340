import java.util.ArrayList;
import java.util.List;

public class Route {
    private String routeId;
    private List<Location> locations;
    private List<Vehicle> assignedVehicles;

    public Route(String routeId) {
        this.routeId = routeId;
        this.locations = new ArrayList<>();
        this.assignedVehicles = new ArrayList<>();
    }

    public String getRouteId() {
        return routeId;
    }

    public void addLocation(Location location, int position) {
        if (position < 0 || position > locations.size()) {
            throw new IllegalArgumentException("Invalid position");
        }
        locations.add(position, location);
    }

    public void removeLocation(int position) {
        if (position < 0 || position >= locations.size()) {
            throw new IllegalArgumentException("Invalid position");
        }
        locations.remove(position);
    }

    public List<Location> getLocations() {
        return new ArrayList<>(locations);
    }

    public void assignVehicle(Vehicle vehicle) {
        if (!assignedVehicles.contains(vehicle)) {
            assignedVehicles.add(vehicle);
        }
    }

    public void removeVehicle(Vehicle vehicle) {
        assignedVehicles.remove(vehicle);
    }

    public List<Vehicle> getAssignedVehicles() {
        return new ArrayList<>(assignedVehicles);
    }

    @Override
    public String toString() {
        return "Route " + routeId + ": " + locations.toString();
    }
}
