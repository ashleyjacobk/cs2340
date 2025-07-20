package managers;

import controllers.TravelController;
import entities.Location;

import java.util.Map;

/** Handles creation and display operations for {@link Location} instances, allowing
 *  {@link controllers.TravelController} to focus on simulation state. */
public class LocationManager {

    private final Map<String, Location> locations;
    private final TravelController controller;

    public LocationManager(Map<String, Location> locations, TravelController controller) {
        this.locations = locations;
        this.controller = controller;
    }

    /**
     * Creates a location.
     */
    public void createLocation(String name, String id, double x, double y) {
        name = name.trim();
        id = id.trim();

        if (locations.containsKey(id)) {
            throw new IllegalArgumentException("Location with ID " + id + " already exists");
        }
        if (!validId(id)) {
            throw new IllegalArgumentException("Location ID must be alphanumeric and not exceed 100 characters");
        }
        if (!isIdGloballyUnique(id)) {
            throw new IllegalArgumentException("ID " + id + " is not globally unique across vehicles, locations, and routes");
        }
        locations.put(id, new Location(name, id, 0, true, x, y));
        controller.displayMessage("info", "Location created: " + name + " at ("+x+","+y+")");
    }

    /** Displays all locations. */
    public void displayLocations() {
        if (locations.isEmpty()) {
            controller.displayMessage("info", "No locations available");
            return;
        }
        locations.values().forEach(location -> System.out.println(location.getName()));
    }

    private boolean validId(String id) {
        return id.matches("[A-Za-z0-9]+") && id.length() <= 100;
    }

    private boolean isIdGloballyUnique(String id) {
        return !controller.getVehicles().containsKey(id) &&
               !locations.containsKey(id) &&
               !controller.getRoutes().containsKey(id);
    }
} 