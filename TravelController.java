import java.util.*;
import java.util.stream.Collectors;

public class TravelController {
    private Map<String, Vehicle> vehicles;
    private Map<String, Location> locations;
    private Map<String, Route> routes;
    private static final Set<String> usedIds = new HashSet<>();
    private double currentTime;
    private PriorityQueue<Event> eventQueue;

    public TravelController() {
        this.vehicles = new TreeMap<>();
        this.locations = new TreeMap<>();
        this.routes = new TreeMap<>();
        this.currentTime = 0.0;
        this.eventQueue = new PriorityQueue<>();
    }

    private void checkAndRegisterId(String id) {
        if (!usedIds.add(id)) {
            throw new IllegalArgumentException("ID '" + id + "' is already used");
        }
    }

    private void unregisterId(String id) {
        usedIds.remove(id);
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
                        if (tokens.length != 7) {
                            throw new IllegalArgumentException("Correct usage for create_vehicle is: create_vehicle,<capacity>,<type>,<id>,<direction>,<route>,<currentLocationID>");
                        }
                        createVehicle(Integer.parseInt(tokens[1]), tokens[2], tokens[3], tokens[4], tokens[5], tokens[6]);
                        break;
                    case "display_vehicles":
                        displayVehicles();
                        break;
                    case "create_location":
                        if (tokens.length != 3) {
                            throw new IllegalArgumentException("Correct usage for create_location is: create_location,<name>,<id>");
                        }
                        createLocation(tokens[1], tokens[2]);
                        break;
                    case "display_locations":
                        displayLocations();
                        break;
                    case "create_route":
                        if (tokens.length != 3) {
                            throw new IllegalArgumentException("Correct usage for create_route is: create_route,<id>,<vehicleType>");
                        }
                        createRoute(tokens[1], tokens[2]);
                        break;
                    case "add_location_to_route":
                        if (tokens.length != 4) {
                            throw new IllegalArgumentException("Correct usage for add_location_to_route is: add_location_to_route,<routeId>,<locationId>,<position>");
                        }
                        addLocationToRoute(tokens[1], tokens[2], Integer.parseInt(tokens[3]));
                        break;
                    case "remove_location_from_route":
                        if (tokens.length != 3) {
                            throw new IllegalArgumentException("Correct usage for remove_location_from_route is: remove_location_from_route,<routeId>,<position>");
                        }
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
                    case "advance_time":
                        if (tokens.length != 2) {
                            throw new IllegalArgumentException("Correct usage for advance_time is: advance_time,<timeStep>");
                        }
                        advanceTime(Double.parseDouble(tokens[1]));
                        break;
                    case "jump_to_time":
                        if (tokens.length != 2) {
                            throw new IllegalArgumentException("Correct usage for jump_to_time is: jump_to_time,<targetTime>");
                        }
                        jumpToTime(Double.parseDouble(tokens[1]));
                        break;
                    case "get_current_time":
                        System.out.println("Current time: " + getCurrentTime());
                        break;
                    case "get_next_event_time":
                        System.out.println("Next event time: " + getNextEventTime());
                        break;
                    case "set_vehicle_speed":
                        if (tokens.length != 3) {
                            throw new IllegalArgumentException("Correct usage for set_vehicle_speed is: set_vehicle_speed,<vehicleId>,<speed>");
                        }
                        setVehicleSpeed(tokens[1], Double.parseDouble(tokens[2]));
                        break;
                    case "set_location_distance":
                        if (tokens.length != 4) {
                            throw new IllegalArgumentException("Correct usage for set_location_distance is: set_location_distance,<locationId1>,<locationId2>,<distance>");
                        }
                        setLocationDistance(tokens[1], tokens[2], Double.parseDouble(tokens[3]));
                        break;
                    case "exit":
                        System.out.println("exit acknowledged");
                        commandLineInput.close();
                        return;
                    case "help":
                        System.out.println("Available commands:");
                        System.out.println("create_vehicle, display_vehicles, create_location, display_locations, create_route, add_location_to_route, remove_location_from_route, display_locations_in_route, display_routes, set_vehicle_position, display_vehicles_at_location, display_vehicles_on_route, advance_time, jump_to_time, get_current_time, get_next_event_time, set_vehicle_speed, set_location_distance, exit");
                        break;
                    default:
                        System.out.println("command " + tokens[0] + " NOT acknowledged");
                }
            } catch (Exception e) {
                displayMessage("error", "during command loop >> " + e.getMessage());
            }
        }
    }

    private void createVehicle(int capacity, String type, String id, String direction, String route, String currentLocation) {
        if (vehicles.containsKey(id)) {
            throw new IllegalArgumentException("Vehicle with ID '" + id + "' already exists");
        }

        if (!routes.containsKey(route)) {
            throw new IllegalArgumentException("Route with ID '" + route + "' does not exist");
        }

        if (!locations.containsKey(currentLocation)) {
            throw new IllegalArgumentException("Location with ID '" + currentLocation + "' does not exist");
        }

        Route routeObject = routes.get(route);
        Location currentLocationObject = locations.get(currentLocation);
        Vehicle vehicle = new Vehicle(capacity, type, id, direction, routeObject, currentLocationObject);
        routeObject.add_vehicle(vehicle);
        checkAndRegisterId(id);
        vehicles.put(id, vehicle);
        displayMessage("info", "Vehicle created: " + id);
    }

    private void displayVehicles() {
        if (vehicles.isEmpty()) {
            displayMessage("info", "No vehicles in service");
            return;
        }
        vehicles.keySet().forEach(System.out::println);
    }

    private void createLocation(String name, String id) {
        checkAndRegisterId(id);
        if (locations.containsKey(id)) {
            unregisterId(id);
            throw new IllegalArgumentException("Location with id " + id + " already exists");
        }
        locations.put(id, new Location(name, id));
        displayMessage("info", "Location created: " + name + "(ID: " + id + ")");
    }

    private void displayLocations() {
        if (locations.isEmpty()) {
            displayMessage("info", "No locations available");
            return;
        }
        locations.keySet().forEach(System.out::println);
    }

    private void createRoute(String id, String vehicleType) {
        checkAndRegisterId(id);
        if (routes.containsKey(id)) {
            throw new IllegalArgumentException("Route with ID " + id + " already exists");
        }
        routes.put(id, new Route(id, vehicleType));
        displayMessage("info", "Route for vehicle type " + vehicleType + " created (ID : " + id + ")");
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

    private void displayLocationsInRoute(String routeId) {
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
        List<Vehicle> vehiclesOnRoute = route.getVehicles();
        if (vehiclesOnRoute.isEmpty()) {
            displayMessage("info", "No vehicles assigned to route " + routeId);
        } else {
            for (Vehicle vehicle : vehiclesOnRoute) {
                System.out.println(vehicle);
            }
        }
    }

    public void advanceTime(double timeStep) {
        if (timeStep <= 0) {
            throw new IllegalArgumentException("Time step must be positive");
        }
        double targetTime = currentTime + timeStep;
        processEventsUntil(targetTime);
        currentTime = targetTime;
    }

    public void jumpToTime(double targetTime) {
        if (targetTime < currentTime) {
            throw new IllegalArgumentException("Cannot jump to a time in the past");
        }
        processEventsUntil(targetTime);
        currentTime = targetTime;
    }

    private void processEventsUntil(double targetTime) {
        while (!eventQueue.isEmpty() && eventQueue.peek().getTime() <= targetTime) {
            Event event = eventQueue.poll();
            currentTime = event.getTime();
            handleEvent(event);
        }
    }

    private void handleEvent(Event event) {
        switch (event.getType()) {
            case "VEHICLE_ARRIVAL":
                handleVehicleArrival(event);
                break;
            case "VEHICLE_DEPARTURE":
                handleVehicleDeparture(event);
                break;
            default:
                displayMessage("warning", "Unknown event type: " + event.getType());
        }
    }

    private void handleVehicleArrival(Event event) {
        String vehicleId = event.getDescription();
        Vehicle vehicle = vehicles.get(vehicleId);
        if (vehicle != null) {
            vehicle.updateState(currentTime);
            // Schedule departure event
            scheduleEvent(new Event(currentTime + 5.0, "VEHICLE_DEPARTURE", vehicleId));
        }
    }

    private void handleVehicleDeparture(Event event) {
        String vehicleId = event.getDescription();
        Vehicle vehicle = vehicles.get(vehicleId);
        if (vehicle != null) {
            vehicle.updateState(currentTime);
            // Schedule next arrival event if vehicle is moving
            if (!vehicle.isAtLocation()) {
                scheduleEvent(new Event(vehicle.getArrivalTime(), "VEHICLE_ARRIVAL", vehicleId));
            }
        }
    }

    public void scheduleEvent(Event event) {
        eventQueue.add(event);
    }

    public double getCurrentTime() {
        return currentTime;
    }

    public double getNextEventTime() {
        return eventQueue.isEmpty() ? Double.POSITIVE_INFINITY : eventQueue.peek().getTime();
    }

    public void setVehicleSpeed(String vehicleId, double speed) {
        Vehicle vehicle = vehicles.get(vehicleId);
        if (vehicle == null) {
            throw new IllegalArgumentException("Invalid vehicle ID");
        }
        vehicle.setSpeed(speed);
        // Schedule next arrival event if vehicle is moving
        if (!vehicle.isAtLocation()) {
            scheduleEvent(new Event(vehicle.getArrivalTime(), "VEHICLE_ARRIVAL", vehicleId));
        }
    }

    public void setLocationDistance(String locationId1, String locationId2, double distance) {
        Location loc1 = locations.get(locationId1);
        Location loc2 = locations.get(locationId2);
        if (loc1 == null || loc2 == null) {
            throw new IllegalArgumentException("Invalid location ID");
        }
        loc1.setDistanceTo(loc2, distance);
        // Update arrival times for vehicles traveling between these locations
        for (Vehicle vehicle : vehicles.values()) {
            if (!vehicle.isAtLocation() && 
                (vehicle.getCurrentLocation() == loc1 || vehicle.getCurrentLocation() == loc2) &&
                (vehicle.getNextLocation() == loc1 || vehicle.getNextLocation() == loc2)) {
                scheduleEvent(new Event(vehicle.getArrivalTime(), "VEHICLE_ARRIVAL", vehicle.getId()));
            }
        }
    }

    void displayMessage(String status, String text_output) {
        System.out.println(status.toUpperCase() + ": " + text_output);
    }
}