package managers;

import controllers.TravelController;
import entities.Location;
import entities.Route;
import entities.Vehicle;
import entities.VehicleType;

import java.util.Map;
import java.util.stream.Collectors;
import events.DepartureEvent;

/**
 * Provides display-oriented operations for vehicles, reducing the size of {@link TravelController} and adhering to SRP.
 */
public class VehicleManager {

    private final Map<String, Vehicle> vehicles;
    private final Map<String, Location> locations;
    private final Map<String, Route> routes;
    private final TravelController controller;

    public VehicleManager(Map<String, Vehicle> vehicles,
                          Map<String, Location> locations,
                          Map<String, Route> routes,
                          TravelController controller) {
        this.vehicles = vehicles;
        this.locations = locations;
        this.routes = routes;
        this.controller = controller;
    }

    /** Displays every vehicle currently in service. */
    public void displayVehicles() {
        if (vehicles.isEmpty()) {
            controller.displayMessage("info", "No vehicles in service");
            return;
        }
        vehicles.values().forEach(System.out::println);
    }

    /** Shows all vehicles currently at a specific location. */
    public void displayVehiclesAtLocation(String locationId) {
        locationId = locationId.trim();
        Location location = locations.get(locationId);
        if (location == null) {
            throw new IllegalArgumentException("Location with ID " + locationId + " does not exist");
        }
        String vehiclesAtLoc = vehicles.values().stream()
                .filter(v -> location.equals(v.getCurrentLocation()))
                .map(Vehicle::getId)
                .collect(Collectors.joining(", "));
        if (vehiclesAtLoc.isEmpty()) {
            controller.displayMessage("info", "No vehicles at location " + locationId);
        } else {
            controller.displayMessage("info", "Vehicles at location " + locationId + ": " + vehiclesAtLoc);
        }
    }

    /** Displays every vehicle assigned to a given route. */
    public void displayVehiclesOnRoute(String routeId) {
        routeId = routeId.trim();
        Route route = routes.get(routeId);
        if (route == null) {
            throw new IllegalArgumentException("Route with ID " + routeId + " does not exist");
        }
        String vehiclesOnRoute = vehicles.values().stream()
                .filter(v -> route.equals(v.getRoute()))
                .map(Vehicle::getId)
                .collect(Collectors.joining(", "));
        if (vehiclesOnRoute.isEmpty()) {
            controller.displayMessage("info", "No vehicles on route " + routeId);
        } else {
            controller.displayMessage("info", "Vehicles on route " + routeId + ": " + vehiclesOnRoute);
        }
    }

    /** Human-readable status of a vehicle (in-transit vs. at station). */
    public void displayVehicleStatus(String vehicleId) {
        vehicleId = vehicleId.trim();
        Vehicle vehicle = vehicles.get(vehicleId);
        if (vehicle == null) {
            throw new IllegalArgumentException("Vehicle with ID " + vehicleId + " does not exist");
        }
        if (vehicle.isInTransit()) {
            controller.displayMessage("info", "Vehicle " + vehicleId + " departed from " + vehicle.getPreviousLocation().getName() + " and is in transit.");
        } else {
            controller.displayMessage("info", "Vehicle " + vehicleId + " is at location " + vehicle.getCurrentLocation().getName() + ".");
        }
    }

    /** Current passenger load for a vehicle. */
    public void displayVehicleRiders(String vehicleId) {
        vehicleId = vehicleId.trim();
        Vehicle vehicle = vehicles.get(vehicleId);
        if (vehicle == null) {
            throw new IllegalArgumentException("Vehicle with ID " + vehicleId + " does not exist");
        }
        controller.displayMessage("info", "Current passenger count for vehicle " + vehicleId + ": " + vehicle.getCurrentPassengers());
    }

    /** Creates a vehicle and schedules its first departure. */
    public void createVehicle(int capacity, String type, String id, String routeId, String currentLocationId, double speed) {
        id = id.trim();
        Route route = routes.get(routeId.trim());
        Location curLoc = locations.get(currentLocationId.trim());

        if (route == null) throw new IllegalArgumentException("Route with ID " + routeId + " does not exist");
        if (curLoc == null) throw new IllegalArgumentException("Current location does not exist");

        VehicleType vType;
        try {
            type = type.trim().toUpperCase();
            if (type.equals("U-BAHN")) type = "U_BAHN";
            if (type.equals("S-BAHN")) type = "S_BAHN";
            vType = VehicleType.valueOf(type);
        } catch (Exception ex) {
            throw new IllegalArgumentException("Vehicle type must be U-BAHN, S-BAHN, BUS, or TRAM");
        }

        if (vehicles.containsKey(id) || locations.containsKey(id) || routes.containsKey(id)) {
            throw new IllegalArgumentException("ID " + id + " is not globally unique");
        }

        if (!route.getVehicleType().equals(vType)) {
            throw new IllegalArgumentException("Vehicle type does not match route type");
        }
        if (!route.getLocations().contains(curLoc)) {
            throw new IllegalArgumentException("Current location not on route");
        }

        Vehicle vehicle = new Vehicle(capacity, vType, id, route, curLoc, speed);
        vehicles.put(id, vehicle);
        route.add_vehicle(vehicle);
        controller.displayMessage("info", "Vehicle created: " + id);

        // schedule first departure event at current time
        DepartureEvent de = new DepartureEvent(controller.getTime(), vehicle, controller);
        controller.addEvent(de);
    }

    /** Adjusts vehicle speed. */
    public void setVehicleSpeed(String vehicleId, double speed) {
        Vehicle v = vehicles.get(vehicleId.trim());
        if (v == null) throw new IllegalArgumentException("Vehicle not found");
        v.setSpeed(speed);
        controller.displayMessage("info", "Speed for " + vehicleId + " set to " + speed + " kph");
    }

    /** Sets explicit position of a vehicle on a route. */
    public void setVehiclePosition(String vehicleId, String routeId, int position) {
        Vehicle v = vehicles.get(vehicleId.trim());
        Route route = routes.get(routeId.trim());
        if (v == null) throw new IllegalArgumentException("Vehicle not found");
        if (route == null) throw new IllegalArgumentException("Route not found");
        if (position < 0 || position >= route.getLocations().size()) throw new IllegalArgumentException("Position out of range");
        v.arriveAt(route.getLocations().get(position));
        v.setCurrentRoute(route);
        controller.displayMessage("info", "Vehicle " + vehicleId + " positioned on route " + routeId);
    }

    /** Sets vehicle riders directly. */
    public void setVehicleRiders(String vehicleId, int riders) {
        Vehicle v = vehicles.get(vehicleId.trim());
        if (v == null) throw new IllegalArgumentException("Vehicle not found");
        v.setCurrentPassengers(riders);
        controller.displayMessage("info", "Vehicle " + vehicleId + " riders set to " + riders);
    }
} 