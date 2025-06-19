import java.util.*;
import java.util.stream.Collectors;

public class TravelController {
    private Map<String, Vehicle> vehicles;
    private Map<String, Location> locations;
    private Map<String, Route> routes;
    private double time = 0.0;

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
                System.out.print("$> ");
                wholeInputLine = commandLineInput.nextLine();
                tokens = wholeInputLine.split(DELIMITER);

                if (tokens[0].indexOf("//") == 0 || wholeInputLine.equals("")) {
                    continue;
                }

                switch (tokens[0]) {
                    case "create_vehicle":
                        if (tokens.length != 7) {
                            throw new IllegalArgumentException("Correct usage for create_vehicle is: create_vehicle,<capacity>,<type>,<id>,<direction>,<route>,<currentLocation>");
                        }
                        tokens[1] = tokens[1].trim();
                        createVehicle(Integer.parseInt(tokens[1]), tokens[2], tokens[3], tokens[4], tokens[5], tokens[6]);
                        break;
                    case "display_vehicles":
                        displayVehicles();
                        break;
                    case "create_location":
                        if (tokens.length != 2) {
                            throw new IllegalArgumentException("Correct usage for create_location is: create_location,<name>");
                        }
                        createLocation(tokens[1]);
                        break;
                    case "display_locations":
                        displayLocations();
                        break;
                    case "create_route":
                        if (tokens.length != 2) {
                            throw new IllegalArgumentException("Correct usage for create_route is: create_route,<id>");
                        }
                        createRoute(tokens[1]);
                        break;
                    case "add_location_to_route":
                        if (tokens.length != 4) {
                            throw new IllegalArgumentException("Correct usage for add_location_to_route is: add_location_to_route,<routeId>,<locationId>,<position>");
                        }
                        tokens[3] = tokens[3].trim();
                        addLocationToRoute(tokens[1], tokens[2], Integer.parseInt(tokens[3]));
                        break;
                    case "remove_location_from_route":
                        if (tokens.length != 3) {
                            throw new IllegalArgumentException("Correct usage for remove_location_from_route is: remove_location_from_route,<routeId>,<position>");
                        }
                        tokens[2] = tokens[2].trim();
                        removeLocationFromRoute(tokens[1], Integer.parseInt(tokens[2]));
                        break;
                    case "display_locations_in_route":
                        if (tokens.length != 2) {
                            throw new IllegalArgumentException("Correct usage for display_locations_in_route is: display_locations_in_route,<routeId>");
                        }
                        displayLocationsInRoute(tokens[1]);
                        break;
                    case "display_routes":
                        displayRoutes();
                        break;
                    case "set_vehicle_position":
                        if (tokens.length != 4) {
                            throw new IllegalArgumentException("Correct usage for set_vehicle_position is: set_vehicle_position,<vehicleId>,<routeId>,<position>");
                        }
                        tokens[3] = tokens[3].trim();
                        setVehiclePosition(tokens[1], tokens[2], Integer.parseInt(tokens[3]));
                        break;
                    case "display_vehicles_at_location":
                        if (tokens.length != 2) {
                            throw new IllegalArgumentException("Correct usage for display_vehicles_at_location is: display_vehicles_at_location,<locationId>");
                        }
                        displayVehiclesAtLocation(tokens[1]);
                        break;
                    case "display_vehicles_on_route":
                        if (tokens.length != 2) {
                            throw new IllegalArgumentException("Correct usage for display_vehicles_on_route is: display_vehicles_on_route,<routeId>");
                        }
                        displayVehiclesOnRoute(tokens[1]);
                        break;
                    case "display_time":
                        System.out.printf("%.2f%n", getTime());
                        break;
                    // case "display_position_of_vehicle":
                    //     if (tokens.length != 2) {
                    //         throw new IllegalArgumentException("Correct usage for display_vehicles_at_location is: display_vehicles_at_location,<locationId>");
                    //     }
                    //     Vehicle vehicle = vehicles.get(tokens[1]);
                    //     if (vehicle == null) {
                    //         throw new IllegalArgumentException("Vehicle with ID " + tokens[1] + " does not exist");
                    //     }
                    //     System.out.println(vehicle.getType() + " " + vehicle.getId() + " is at location " + vehicle.getCurrentLocation().getName() + " on route" + vehicle.getRoute());
                    case "advance_time":
                        if (tokens.length != 2) {
                            throw new IllegalArgumentException("Correct usage for advance_time is: advance_time,<timePassed (HH.MM)>");
                        }
                        advanceTime(Double.parseDouble(tokens[1]));
                        break;
                    case "exit":
                        System.out.println("exit acknowledged");
                        commandLineInput.close();
                        return;
                    case "help":
                        System.out.println("Available commands:");
                        System.out.println("create_vehicle, display_vehicles, create_location, display_locations, create_route, add_location_to_route, remove_location_from_route, display_locations_in_route, display_routes, set_vehicle_position, display_vehicles_at_location, display_vehicles_on_route, exit");
                        break;
                    default:
                        System.out.println("command " + tokens[0] + " NOT acknowledged");
                }
            } catch (Exception e) {
                displayMessage("error", "during command loop >> " + e.getMessage());
            }
        }
    }

    /**
     * Helper method to validate ID format.
     * 
     * @param id
     * @return true if the ID is valid, false otherwise.
     */
    private boolean validId(String id) { 
        return id.matches("[A-Za-z0-9]+") && id.length() <= 100; 
    }
    
    private void createVehicle(int capacity, String type, String id, String direction, String route, String currentLocation) {
        id = id.trim();
        type = type.trim();
        direction = direction.trim();
        route = route.trim();
        currentLocation = currentLocation.trim();
        Location directionObject;

        if (vehicles.containsKey(id)) {
            throw new IllegalArgumentException("Vehicle with ID " + id + " already exists");
        }
        if (routes.get(route) == null) {
            throw new IllegalArgumentException("Route with ID " + route + " does not exist");
        }
        if (!validId(id)) {
            throw new IllegalArgumentException("Vehicle ID must be alphanumeric and not exceed 100 characters");
        }

        try {
            directionObject = locations.get(direction);
        } catch (NullPointerException e) {
            throw new IllegalArgumentException("Direction location with ID '" + direction + "' does not exist");
        }

        Route routeObject = routes.get(route);
        Location currentLocationObject = locations.get(currentLocation);
        vehicles.put(id, new Vehicle(capacity, type, id, directionObject, routeObject, currentLocationObject));
        displayMessage("info", "Vehicle created: " + id);
    }

    private void displayVehicles() {
        if (vehicles.isEmpty()) {
            displayMessage("info", "No vehicles in service");
            return;
        }
        vehicles.keySet().forEach(System.out::println);
    }

    private void createLocation(String name) {
        name = name.trim();

        if (locations.containsKey(name)) {
            throw new IllegalArgumentException("Location with name " + name + " already exists");
        }
        locations.put(name, new Location(name, null, 0, true));
        displayMessage("info", "Location created: " + name);
    }

    private void displayLocations() {
        if (locations.isEmpty()) {
            displayMessage("info", "No locations available");
            return;
        }
        locations.keySet().forEach(System.out::println);
    }

    private void createRoute(String id) {
        id = id.trim();

        if (routes.containsKey(id)) {
            throw new IllegalArgumentException("Route with ID " + id + " already exists");
        }
        if (!validId(id)) {
            throw new IllegalArgumentException("Route ID must be alphanumeric and not exceed 100 characters");
        }
        if (routes.containsKey(id)) {
            throw new IllegalArgumentException("Route with ID " + id + " already exists");
        }
        routes.put(id, new Route(id, null));
        displayMessage("info", "Route created: " + id);
    }

    private void addLocationToRoute(String routeId, String locationId, int position) {
        routeId = routeId.trim();
        locationId = locationId.trim();

        Route route = routes.get(routeId);
        Location location = locations.get(locationId);
        if (route == null || location == null) {
            throw new IllegalArgumentException("Invalid route or location ID");
        }
        route.addLocation(location, position);
        displayMessage("info", "Location " + locationId + " added to route " + routeId);
    }

    private void removeLocationFromRoute(String routeId, int position) {
        routeId = routeId.trim();

        Route route = routes.get(routeId);
        if (route == null) {
            throw new IllegalArgumentException("Invalid route ID");
        }
        route.removeLocation(position);
        displayMessage("info", "Location removed from route " + routeId);
    }

    private void displayLocationsInRoute(String routeId) {
        routeId = routeId.trim();

        Route route = routes.get(routeId);
        if (route == null) {
            throw new IllegalArgumentException("Invalid route ID");
        }
        route.display_route();
    }

    private void displayRoutes() {
        if (routes.isEmpty()) {
            displayMessage("info", "No routes available");
            return;
        }
        routes.keySet().forEach(System.out::println);
    }

    private void setVehiclePosition(String vehicleId, String routeId, int position) {
        vehicleId = vehicleId.trim();
        routeId = routeId.trim();

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
        locationId = locationId.trim();

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
        routeId = routeId.trim();

        Route route = routes.get(routeId);
        if (route == null) {
            throw new IllegalArgumentException("Invalid route ID");
        }
        List<Vehicle> vehiclesOnRoute = route.getVehicles();
        if (vehiclesOnRoute.isEmpty()) {
            displayMessage("info", "No vehicles assigned to route " + routeId);
        } else {
            for (Vehicle vehicle : vehiclesOnRoute) {
                System.out.println(vehicle);
            }
        }
    }

    void displayMessage(String status, String text_output) {
        System.out.println(status.toUpperCase() + ": " + text_output);
    }

    private void advanceTime(double time) {
        if (time > 0.0) {
            this.time = (this.time + time) % 24;
        }
    }

    private double getTime() {
        return time;
    }
}