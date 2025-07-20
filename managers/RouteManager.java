package managers;

import controllers.TravelController;
import entities.Location;
import entities.Route;
import entities.VehicleType;

import java.util.Map;
import java.util.stream.Collectors;

/** Handles creation and modification of {@link Route} instances. */
public class RouteManager {

    private final Map<String, Route> routes;
    private final Map<String, Location> locations;
    private final TravelController controller;

    public RouteManager(Map<String, Route> routes,
                        Map<String, Location> locations,
                        TravelController controller) {
        this.routes = routes;
        this.locations = locations;
        this.controller = controller;
    }

    /** Creates a route. */
    public void createRoute(String id, String type) {
        id = id.trim();
        VehicleType vehicleType;
        try {
            type = type.trim().toUpperCase();
            if (type.equals("U-BAHN")) {
                type = "U_BAHN";
            } else if (type.equals("S-BAHN")) {
                type = "S_BAHN";
            }
            vehicleType = VehicleType.valueOf(type);
        } catch (Exception e) {
            throw new IllegalArgumentException("Vehicle type must be U-BAHN, S-BAHN, BUS, or TRAM");
        }

        if (routes.containsKey(id)) {
            throw new IllegalArgumentException("Route with ID " + id + " already exists");
        }
        if (!validId(id)) {
            throw new IllegalArgumentException("Route ID must be alphanumeric and not exceed 100 characters");
        }
        if (!isIdGloballyUnique(id)) {
            throw new IllegalArgumentException("ID " + id + " is not globally unique across entities");
        }
        routes.put(id, new Route(id, vehicleType));
        controller.displayMessage("info", "Route created: " + id);
    }

    /** Adds a location at a given position within a route. */
    public void addLocationToRoute(String routeId, String locationId, int position) {
        Route route = routes.get(routeId.trim());
        Location loc = locations.get(locationId.trim());
        if (route == null) throw new IllegalArgumentException("Route not found");
        if (loc == null) throw new IllegalArgumentException("Location not found");
        route.addLocation(loc, position);
        controller.displayMessage("info", "Location " + locationId + " added to route " + routeId + " at position " + position);
    }

    /** Removes a location from a route by position. */
    public void removeLocationFromRoute(String routeId, int position) {
        Route route = routes.get(routeId.trim());
        if (route == null) throw new IllegalArgumentException("Route not found");
        route.removeLocation(position);
        controller.displayMessage("info", "Removed location at position " + position + " from route " + routeId);
    }

    /** Displays all route IDs. */
    public void displayRoutes() {
        if (routes.isEmpty()) {
            controller.displayMessage("info", "No routes available");
        } else {
            routes.keySet().forEach(System.out::println);
        }
    }

    /** Displays list of locations in a route. */
    public void displayLocationsInRoute(String routeId) {
        Route route = routes.get(routeId.trim());
        if (route == null) throw new IllegalArgumentException("Route not found");
        String list = route.getLocations().stream().map(Location::getName).collect(Collectors.joining(", "));
        controller.displayMessage("info", "[" + list + "]");
    }

    // ---- helpers ----
    private boolean validId(String id) {
        return id.matches("[A-Za-z0-9]+") && id.length() <= 100;
    }
    private boolean isIdGloballyUnique(String id) {
        return !routes.containsKey(id) &&
               !locations.containsKey(id) &&
               !controller.getVehicles().containsKey(id);
    }
} 