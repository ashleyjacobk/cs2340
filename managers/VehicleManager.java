package managers;

import controllers.TravelController;
import entities.Location;
import entities.Route;
import entities.Vehicle;

import java.util.Map;
import java.util.stream.Collectors;

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
} 