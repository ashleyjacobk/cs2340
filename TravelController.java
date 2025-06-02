import java.util.*;
import java.util.stream.Collectors;

public class TravelController {
    private Map<String, Vehicle> vehicles;
    private Map<String, Location> locations;
    private Map<String, Route> routes;

    public TravelController() {
        this.vehicles = new TreeMap<>();
        this.locations = new TreeMap<>();
        this.routes = new TreeMap<>();
    }

    public void commandLoop() {
        Scanner commandLineInput = new Scanner(System.in);
        String wholeInputLine;
        String[] tokens;
        final String DELIMITER = ",";

        while (true) {
            try {
                System.out.print(" $> ");
                wholeInputLine = commandLineInput.nextLine();
                tokens = wholeInputLine.split(DELIMITER);

                if (tokens[0].indexOf("//") == 0 || wholeInputLine.equals("")) {
                    continue;
                }

                switch (tokens[0]) {
                    case "create_vehicle":
                        createVehicle(tokens[1], tokens[2], tokens[3], Integer.parseInt(tokens[4]));
                        break;
                    case "display_vehicles":
                        displayVehicles();
                        break;
                    case "create_location":
                        createLocation(tokens[1], tokens[2]);
                        break;
                    case "display_locations":
                        displayLocations();
                        break;
                    case "create_route":
                        createRoute(tokens[1]);
                        break;
                    case "add_location_to_route":
                        addLocationToRoute(tokens[1], tokens[2], Integer.parseInt(tokens[3]));
                        break;
                    case "remove_location_from_route":
                        removeLocationFromRoute(tokens[1], Integer.parseInt(tokens[2]));
                        break;
                    case "display_route":
                        displayRoute(tokens[1]);
                        break;
                    case "set_vehicle_position":
                        setVehiclePosition(tokens[1], tokens[2], Integer.parseInt(tokens[3]));
                        break;
                    case "display_vehicles_at_location":
                        displayVehiclesAtLocation(tokens[1]);
                        break;
                    case "display_vehicles_on_route":
                        displayVehiclesOnRoute(tokens[1]);
                        break;
                    case "exit":
                        System.out.println("exit acknowledged");
                        commandLineInput.close();
                        return;
                    default:
                        System.out.println("command " + tokens[0] + " NOT acknowledged");
                }
            } catch (Exception e) {
                displayMessage("error", "during command loop >> " + e.getMessage());
            }
        }
    }

    private void createVehicle(String id, String type, String licensePlate, int capacity) {
        vehicles.put(id, new Vehicle(id, type, licensePlate, capacity));
        displayMessage("info", "Vehicle created: " + id);
    }

    private void displayVehicles() {
        if (vehicles.isEmpty()) {
            displayMessage("info", "No vehicles in service");
            return;
        }
        vehicles.values().forEach(System.out::println);
    }

    private void createLocation(String id, String name) {
        locations.put(id, new Location(id, name));
        displayMessage("info", "Location created: " + id);
    }

    private void displayLocations() {
        if (locations.isEmpty()) {
            displayMessage("info", "No locations available");
            return;
        }
        locations.values().forEach(System.out::println);
    }

    private void createRoute(String id) {
        routes.put(id, new Route(id));
        displayMessage("info", "Route created: " + id);
    }

    private void addLocationToRoute(String routeId, String locationId, int position) {
        Route route = routes.get(routeId);
        Location location = locations.get(locationId);
        if (route == null || location == null) {
            throw new IllegalArgumentException("Invalid route or location ID");
        }
        route.addLocation(location, position);
        displayMessage("info", "Location " + locationId + " added to route " + routeId);
    }

    private void removeLocationFromRoute(String routeId, int position) {
        Route route = routes.get(routeId);
        if (route == null) {
            throw new IllegalArgumentException("Invalid route ID");
        }
        route.removeLocation(position);
        displayMessage("info", "Location removed from route " + routeId);
    }

    private void displayRoute(String routeId) {
        Route route = routes.get(routeId);
        if (route == null) {
            throw new IllegalArgumentException("Invalid route ID");
        }
        System.out.println(route);
    }

    private void setVehiclePosition(String vehicleId, String routeId, int position) {
        Vehicle vehicle = vehicles.get(vehicleId);
        Route route = routes.get(routeId);
        if (vehicle == null || route == null) {
            throw new IllegalArgumentException("Invalid vehicle or route ID");
        }
        vehicle.setCurrentRoute(route);
        vehicle.setCurrentPosition(position);
        displayMessage("info", "Vehicle " + vehicleId + " positioned on route " + routeId);
    }

    private void displayVehiclesAtLocation(String locationId) {
        Location location = locations.get(locationId);
        if (location == null) {
            throw new IllegalArgumentException("Invalid location ID");
        }
        List<Vehicle> vehiclesAtLocation = vehicles.values().stream()
            .filter(v -> v.getCurrentLocation() == location)
            .collect(Collectors.toList());
        
        if (vehiclesAtLocation.isEmpty()) {
            displayMessage("info", "No vehicles at location " + locationId);
        } else {
            vehiclesAtLocation.forEach(System.out::println);
        }
    }

    private void displayVehiclesOnRoute(String routeId) {
        Route route = routes.get(routeId);
        if (route == null) {
            throw new IllegalArgumentException("Invalid route ID");
        }
        List<Vehicle> vehiclesOnRoute = route.getAssignedVehicles();
        if (vehiclesOnRoute.isEmpty()) {
            displayMessage("info", "No vehicles assigned to route " + routeId);
        } else {
            vehiclesOnRoute.forEach(System.out::println);
        }
    }

    void displayMessage(String status, String text_output) {
        System.out.println(status.toUpperCase() + ": " + text_output);
    }
}
