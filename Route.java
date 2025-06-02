import java.util.ArrayList;
import java.util.List;

public class Route {
    private String routeId;
    private List<Location> locations;
    private List<Vehicle> vehicles;

    public Route(String routeID) {
        this.routeId = routeID;
        locations = new ArrayList<Location>();
        vehicles = new ArrayList<Vehicle>();
    }

    public void addLocation(Location newLoc, int position) {
        if (position < locations.size() && position >= 0) {
            locations.add(position, newLoc);
        } else {
            System.out.println("This position is invalid in the route");
        }
    }

    public void add_vehicle(Vehicle newVehicle) {
        vehicles.add(newVehicle);
    }

    public void removeLocation(int position) {
        if (position < locations.size() && position >= 0) {
            locations.remove(position);
        } else {
            System.out.println("This position is not in the route");
        }
    }

    public void display_route() {
        for (int i = 0; i < locations.size(); i++) {
            System.out.print(locations.get(i).getName() + " ");
        }
        System.out.println();
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
}
