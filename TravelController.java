import java.util.*;
import java.util.stream.Collectors;

public class TravelController {
    private Map<String, Vehicle> vehicles;
    private Map<String, Location> locations;
    private Map<String, Route> routes;
    private List<Hazard> hazards; 

    // times -- phase 2
    // private double time = 0.0;
    private int time = 0;
    private PriorityQueue<Event> eventQueue = new PriorityQueue<>();

    public TravelController() {
        this.vehicles = new TreeMap<>();
        this.locations = new TreeMap<>();
        this.routes = new TreeMap<>();
        this.hazards = new ArrayList<>();
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
                            throw new IllegalArgumentException("Correct usage for create_vehicle is: create_vehicle,<capacity>,<type>,<id>,<route>,<currentLocation>,<speed>");
                        }
                        tokens[1] = tokens[1].trim();
                        tokens[6] = tokens[6].trim();
                        
                        createVehicle(Integer.parseInt(tokens[1]), tokens[2], tokens[3], tokens[4], tokens[5], Double.parseDouble(tokens[6]));
                        break;
                    case "display_vehicles":
                        displayVehicles();
                        break;
                    case "create_location":
                        if (tokens.length != 5) {
                            throw new IllegalArgumentException("Correct usage for create_location is: create_location,<name>,<id>,<x>,<y>");
                        }
                        tokens[3] = tokens[3].trim();
                        tokens[4] = tokens[4].trim();
                        createLocation(tokens[1], tokens[2], Double.parseDouble(tokens[3]), Double.parseDouble(tokens[4])); // 2 and 3 are x and y respectively
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
                        System.out.println(getTime());
                        break;
                    case "advance_time":
                        if (tokens.length != 2) {
                            throw new IllegalArgumentException("Correct usage for advance_time is: advance_time,<time_to_advance>");
                        }
                        tokens[1] = tokens[1].trim();
                        advanceTime(Integer.parseInt(tokens[1]));
                        break;
                    case "jump_to_time":
                        if (tokens.length != 2) {
                            throw new IllegalArgumentException("Correct usage for jump_to_time is: jump_to_time,<new_time>");
                        }
                        tokens[1] = tokens[1].trim();
                        jumpToTime(Integer.parseInt(tokens[1]));
                        break;
                    case "advance_time_to_next_event":
                        advanceToNextEvent();
                        break;
                    case "display_next_event":
                        displayNextEventTime();
                        break;
                    case "display_vehicle_status":
                        if (tokens.length != 2) {
                            throw new IllegalArgumentException("Correct usage for display_vehicle_status is: display_vehicle_status,<vehicleId>");
                        }
                        displayVehicleStatus(tokens[1]);
                        break;
                    case "create_hazard":
                        if (tokens.length < 6 || tokens.length > 7) {
                            throw new IllegalArgumentException("Correct usage for create_hazard is: create_hazard,<description>,<id>,<type(short_term or long_term)>,<impact>,<location1Id>,[<location2Id>]");
                        }
                        String description = tokens[1].trim();
                        String id = tokens[2].trim();
                        if (!validId(id)) {
                            throw new IllegalArgumentException("Hazard ID must be alphanumeric and up to 100 characters long");
                        }
                        // Check for uniqueness of hazard ID 
                        for (Hazard h : hazards) {
                            if (h.getId().equals(id)) {
                                throw new IllegalArgumentException("Hazard with ID " + id + " already exists.");
                            }
                        }
                        HazardType type = HazardType.valueOf(tokens[3].trim().toUpperCase());
                        double impact = Double.parseDouble(tokens[4].trim());
                        Location location1 = locations.get(tokens[5].trim());
                        if (location1 == null) {
                            throw new IllegalArgumentException("Location with ID " + tokens[5].trim() + " does not exist");
                        }
                        if (tokens.length == 7) {
                            Location location2 = locations.get(tokens[6].trim());
                            if (location2 == null) {
                                throw new IllegalArgumentException("Location with ID " + tokens[6].trim() + " does not exist");
                            }
                            createHazard(description, id, type, impact, location1, location2);
                        } else {
                            createHazard(description, id, type, impact, location1);
                        }
                        break;
                    case "display_hazards":
                        displayHazards();
                        break;
                    case "display_hazards_at_location":
                        if (tokens.length != 2) {
                            throw new IllegalArgumentException("Correct usage for display_hazards_at_location is: display_hazards_at_location,<locationId>");
                        }
                        displayHazardsAtLocation(tokens[1]);
                        break;
                    case "remove_hazard":
                        if (tokens.length != 2) {
                            throw new IllegalArgumentException("Correct usage for remove_hazard is: remove_hazard,<hazardId>");
                        }
                        removeHazard(tokens[1]);
                        break;
                    case "exit":
                        System.out.println("exit acknowledged");
                        commandLineInput.close();
                        return;
                    case "help":
                        System.out.println(help());
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
     * Displays the help message with available commands.
     * 
     * @return a string containing the help message.
     */
    private String help() {
        return "Available Commands:\n" +
                "==== Vehicle Commands ====\n" +
                "  create_vehicle,<capacity>,<type>,<id>,<route>,<currentLocation>,<speed>\n" +
                "  display_vehicles\n" +
                "==== Location Commands ====\n" +
                "  create_location,<name>,<id>,<x>,<y>\n" +
                "  display_locations\n" +
                "==== Route Commands ====\n" +
                "  create_route,<id>,<vehicleType>\n" +
                "  add_location_to_route,<routeId>,<locationId>,<position>\n" +
                "  remove_location_from_route,<routeId>,<position>\n" +
                "  display_locations_in_route,<routeId>\n" +
                "  display_routes\n" +
                "==== Vehicle Positioning Commands ====\n" +
                "  set_vehicle_position,<vehicleId>,<routeId>,<position>\n" +
                "  display_vehicles_at_location,<locationId>\n" +
                "  display_vehicles_on_route,<routeId>\n" +
                "==== Time Commands ====\n" +
                "  display_time\n" +
                "  advance_time,<time_to_advance>\n" +
                "  jump_to_time,<new_time>\n" +
                "  advance_time_to_next_event\n" +
                "  display_next_event\n" +
                "==== Vehicle Status Commands ====\n" +
                "  display_vehicle_status,<vehicleId>\n" +
                "==== Hazard Commands ====\n" +
                "  create_hazard,<description>,<id>,<type(short_term or long_term)>,<impact>,<location1Id>,[<location2Id>]\n" +
                "  display_hazards\n" +
                "  display_hazards_at_location,<locationId>\n" +
                "  remove_hazard,<hazardId>\n" +
                "==== Exit Command ====\n" +
                "  exit\n" +
                "==== Help Command ====\n" +
                "  help\n";
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

    /**
     * Checks if the ID is globally unique across vehicles, locations, and routes.
     * 
     * @param id
     * @return true if the ID is globally unique, false otherwise.
     */
    private boolean isIdGloballyUnique(String id) {
        return !vehicles.containsKey(id) && !locations.containsKey(id) && !routes.containsKey(id);
    }

    /**
     * Creates a vehicle with the specified parameters.
     * @throws IllegalArgumentException if the vehicle ID already exists, or if the route does not exist, or if the ID is invalid.
     * @param capacity
     * @param type
     * @param id
     * @param route
     * @param currentLocation
     * @param speed
     */
    private void createVehicle(int capacity, String type, String id, String route, String currentLocation, double speed) {
        VehicleType vehicleType;
        id = id.trim();
        route = route.trim();
        currentLocation = currentLocation.trim();
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

        if (vehicles.containsKey(id)) {
            throw new IllegalArgumentException("Vehicle with ID " + id + " already exists");
        }
        if (!isIdGloballyUnique(id)) {
            throw new IllegalArgumentException("ID " + id + " is not globally unique across vehicles, locations, and routes");
        }
        Route routeObject = routes.get(route);
        if (routes.get(route) == null) {
            throw new IllegalArgumentException("Route with ID " + route + " does not exist");
        }
        if (vehicleType.equals(routeObject.getVehicleType()) == false) {
            throw new IllegalArgumentException("Vehicle type " + vehicleType + " does not match route type " + routeObject.getVehicleType());
        }
        Location currentLocationObject = locations.get(currentLocation);
        if (currentLocationObject == null) {
            throw new IllegalArgumentException("Current location with ID '" + currentLocation + "' does not exist");
        }
        if (routeObject.getLocations().contains(currentLocationObject) == false) {
            throw new IllegalArgumentException("Current location " + currentLocation + " is not part of the route " + route);
        }
        if (!validId(id)) {
            throw new IllegalArgumentException("Vehicle ID must be alphanumeric and not exceed 100 characters");
        }

        if (speed <= 0) {
            throw new IllegalArgumentException("Speed must be greater than zero.");
        }

        Vehicle vehicle = new Vehicle(capacity, vehicleType, id, routeObject, currentLocationObject, speed);
        vehicles.put(id, vehicle);
        routeObject.add_vehicle(vehicle);
        displayMessage("info", "Vehicle created: " + id);

        // Scheduling first departure event
        DepartureEvent departureEvent = new DepartureEvent(time, vehicle, this);
        addEvent(departureEvent);
        displayMessage("info", "Departure event scheduled for vehicle " + id + " at time " + time);
    }

    /**
     * Displays all vehicles in service.
     */
    private void displayVehicles() {
        if (vehicles.isEmpty()) {
            displayMessage("info", "No vehicles in service");
            return;
        }
        vehicles.values().forEach(System.out::println);
    }

    /**
     * Creates a location with the specified name.
     * @throws IllegalArgumentException if the location already exists or if the ID is not globally unique.
     * @param name
     * @param id usually an abbreviation of the name, but can be anything
     * @param x
     * @param y
     */
    private void createLocation(String name, String id, double x, double y) {
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
        displayMessage("info", "Location created: " + name + " at ("+x+","+y+")");
    }

    /**
     * Displays all locations.
     */
    private void displayLocations() {
        if (locations.isEmpty()) {
            displayMessage("info", "No locations available");
            return;
        }
        locations.values().forEach(location -> System.out.println(location.getName()));
    }

    /**
     * Creates a route with the specified ID.
     * @throws IllegalArgumentException if the route ID already exists or is invalid, or if the ID is not globally unique.
     * @param id
     * @param type
     */
    private void createRoute(String id, String type) {
        VehicleType vehicleType;
        id = id.trim();
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
        if (!isIdGloballyUnique(id)) {
            throw new IllegalArgumentException("ID " + id + " is not globally unique across vehicles, locations, and routes");
        }
        if (!validId(id)) {
            throw new IllegalArgumentException("Route ID must be alphanumeric and not exceed 100 characters");
        }
        if (routes.containsKey(id)) {
            throw new IllegalArgumentException("Route with ID " + id + " already exists");
        }
        routes.put(id, new Route(id, vehicleType));
        displayMessage("info", "Route created: " + id);
    }

    /**
     * Adds a location to a route at a specified position.
     * @throws IllegalArgumentException if the route or location does not exist, or if the position is invalid.
     * @param routeId
     * @param locationId
     * @param position
     */
    private void addLocationToRoute(String routeId, String locationId, int position) {
        routeId = routeId.trim();
        locationId = locationId.trim();

        Route route = routes.get(routeId);
        Location location = locations.get(locationId);
        if (route == null || location == null) {
            throw new IllegalArgumentException("Invalid route or location ID");
        }
        if (route.getLocations().contains(location)) {
            displayMessage("error", "Location " + location.getName() + " already exists in route " + routeId);
            return;
        }
        
        double x = location.getX();
        double y = location.getY();

        // check for duplicate coordinates
        for (Location existing : route.getLocations()) {
            if (Double.compare(existing.getX(), x) == 0 && Double.compare(existing.getY(), y) == 0) {
                throw new IllegalArgumentException("Another location in the route already exists at coordinates (" + x + "," + y + ")");
            }
        }


        int oldSize = route.getLocations().size();
        boolean added = route.addLocation(location, position);
        if (added) {
            displayMessage("info", "Location " + location.getName() + " added to route " + routeId + " at position " + position);

            // If this route previously had only one stop, schedule departures for vehicles currently at that stop
            if (oldSize == 1 && route.getLocations().size() == 2) {
                for (Vehicle vehicle : route.getVehicles()) {
                    if (!vehicle.isInTransit() && vehicle.getCurrentLocation() != null) {
                        // ensure the vehicle is actually on this route at a location (not null)
                        DepartureEvent dep = new DepartureEvent(time, vehicle, this);
                        addEvent(dep);
                        displayMessage("info", "Departure event scheduled for vehicle " + vehicle.getId() + " at time " + time + " due to second location addition");
                    }
                }
            }

            // this will also update the vehicles on this route
            for (Vehicle vehicle : route.getVehicles()) {
                if (vehicle.getCurrentLocation() == null) {
                    vehicle.setCurrentPosition(position);
                    displayMessage("info", "Vehicle " + vehicle.getId() + " is now positioned at " + location.getName() + " on route " + routeId);
                    DepartureEvent departureEvent = new DepartureEvent(time, vehicle, this);
                    addEvent(departureEvent);
                    displayMessage("info", "Departure event scheduled for vehicle " + vehicle.getId() + " at time " + time);
                }
            }
        } else {
            throw new IllegalArgumentException("Invalid position for route " + routeId);
        }
        // route.addLocation(location, position);
        // displayMessage("info", "Location " + location.getName() + " added to route " + routeId);
    }

    /**
     * Removes a location from a route at a specified position.
     * @throws IllegalArgumentException if the route does not exist or if the position is invalid.
     * @param routeId
     * @param position
     */
    private void removeLocationFromRoute(String routeId, int position) {
        routeId = routeId.trim();
        Route route = routes.get(routeId);
        if (route == null) {
            throw new IllegalArgumentException("Invalid route ID");
        }
        if (position < 0 || position >= route.getLocations().size()) {
            throw new IllegalArgumentException("Invalid position for route " + routeId);
        }
        Location removed = route.removeLocation(position);
        displayMessage("info", "Location removed from route " + routeId);

        // Remove any arrival events targeting the removed location
        if (removed != null) {
            eventQueue.removeIf(ev -> (ev instanceof ArrivalEvent) && ((ArrivalEvent) ev).getDestination().equals(removed));
        }

        // find vehicles at this location and store in an arraylist
        List<Vehicle> vehiclesAtLocation = new ArrayList<>();
        for (Vehicle vehicle : vehicles.values()) {
            if (vehicle.getRoute() == route && vehicle.getCurrentLocation() == removed) {
                vehiclesAtLocation.add(vehicle);
            }
        }
        // if there are vehicles at this location, set their current position to the next location in the route
        for (Vehicle vehicle : vehiclesAtLocation) {
            if (route.getLocations().isEmpty()) {
                vehicle.arriveAt(null); // if no locations left, make vehicle arrive at null
                displayMessage("info", "Vehicle " + vehicle.getId() + " is in transit on the empty route " + routeId + ".");
                continue;
            }
            int nextPosition = position;
            if (nextPosition >= route.getLocations().size()) {
                nextPosition = 0; // wrap around to the first location if at the end
            }
            displayMessage("info", "Vehicle " + vehicle.getId() + " repositioned to " + route.getLocations().get(nextPosition).getName());
            vehicle.setCurrentPosition(nextPosition);
            Location nextLocation = vehicle.getNextLocation();
            displayMessage("info", "Vehicle " + vehicle.getId() + " will now travel to " + (nextLocation != null ? nextLocation.getName() : "no next location"));
        }
    }

    /**
     * Displays all locations in a route.
     * @param routeId
     */
    private void displayLocationsInRoute(String routeId) {
        routeId = routeId.trim();

        Route route = routes.get(routeId);
        if (route == null) {
            throw new IllegalArgumentException("Invalid route ID");
        }
        route.display_route();
    }

    /**
     * Displays all routes.
     */
    private void displayRoutes() {
        if (routes.isEmpty()) {
            displayMessage("info", "No routes available");
            return;
        }
        routes.keySet().forEach(System.out::println);
    }

    /**
     * Sets the position of a vehicle on a specified route.
     * @throws IllegalArgumentException if the vehicle or route does not exist.
     * @param vehicleId
     * @param routeId
     * @param position
     */
    private void setVehiclePosition(String vehicleId, String routeId, int position) {
        vehicleId = vehicleId.trim();
        routeId = routeId.trim();

        Vehicle vehicle = vehicles.get(vehicleId);
        Route route = routes.get(routeId);
        if (vehicle == null || route == null) {
            throw new IllegalArgumentException("Invalid vehicle or route ID");
        }
        // check to make sure the position is valid and exists
        if (position < 0 || position >= route.getLocations().size()) {
            throw new IllegalArgumentException("Invalid position for route " + routeId);
        }
        Route oldRoute = vehicle.getRoute();
        if (oldRoute != null && oldRoute != route) {
            oldRoute.getVehicles().remove(vehicle);
        }
        // Add to new route list if absent
        if (!route.getVehicles().contains(vehicle)) {
            route.getVehicles().add(vehicle);
        }

        vehicle.setCurrentRoute(route);
        vehicle.setCurrentPosition(position);

        // Schedule a new departure from this position at current controller time
        DepartureEvent departureEvent = new DepartureEvent(time, vehicle, this);
        addEvent(departureEvent);

        displayMessage("info", "Vehicle " + vehicleId + " positioned on route " + routeId);
    }

    /**
     * Displays all vehicles at a specified location.
     * @throws IllegalArgumentException if the location does not exist.
     * @param locationId
     */
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

    /**
     * Displays all vehicles assigned to a specified route.
     * @throws IllegalArgumentException if the route does not exist.
     * @param routeId
     */
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

    /**
     * Computes the travel time of the vehicle's distance in minutes
     * @param distance
     * @param speed
     * @return the travel time in minutes
     */
    public int computeTravelMinutes(double distance, double speed, Location from, Location to) {
        if (speed <= 0) {
            throw new IllegalArgumentException("Speed must be greater than zero.");
        }
        double hours = distance / speed;
        double minutes = hours * 60; 
        // to account for long-term hazards
        for (Hazard hazard : hazards) {
            if (hazard.isLongTerm() && hazard.affectsConnection(from, to)) {
                displayMessage("info", "Applying long-term hazard " + hazard.getDescription() + " to travel time.");
                minutes *= hazard.getImpact();
            }
        }
        return Math.max((int) minutes, 1); // to make sure theres at least 1 minute of travel time
    }

    /**
     * Adds an event to the event queue.
     * @param event
     */
    public void addEvent(Event event) {
        eventQueue.add(event);
    }

    /**
     * Advances the simulation to the next scheduled event.
     * If there are no events, it displays an error message.
     */
    public void advanceToNextEvent() {
        if (eventQueue.isEmpty()) {
            displayMessage("error", "No scheduled events");
            return;
        }

        Event e = eventQueue.poll();
        time = Math.max(time, e.getTime());
        e.execute();
        displayMessage("info", "Advanced to time: " + time);
    }

    /**
     * Jumps to a specified time, executing all events up to that time.
     * If the new time is less than or equal to the current time, it displays an error message.
     * @param newTime the time to jump to
     */
    public void jumpToTime(int newTime) {
        if (newTime <= time) {
            displayMessage("error", "Cannot go backwards in time.");
            return;
        }

        displayMessage("info", "Jumping time from " + time + " to " + newTime);
        while (!eventQueue.isEmpty() && eventQueue.peek().getTime() <= newTime) {
            advanceToNextEvent();
        }

        time = newTime;
        displayMessage("info", "Time is now " + time);
    }

    /**
     * Advances the simulation time by a specified number of minutes.
     * @throws IllegalArgumentException if the number of minutes is less than or equal to zero.
     * @param minutes the number of minutes to advance
     */
    public void advanceTime(int minutes) {
        if (minutes <= 0) {
            throw new IllegalArgumentException("Time to advance must be greater than zero.");
        }
        int newTime = time + minutes;
        jumpToTime(newTime);
    }

    /**
     * Gets the current simulation time.
     * @return the current time in minutes
     */
    public int getTime() {
        return time;
    }

    /**
     * Displays the time of the next scheduled event.
     * If there are no events, it displays a message indicating that there are no scheduled events.
     */
    public void displayNextEventTime() {
        if (eventQueue.isEmpty()) {
            displayMessage("info", "No scheduled events.");
            return;
        }
        int nextTime = eventQueue.peek().getTime();

        for (Event ev : eventQueue) {
            if (ev.getTime() == nextTime) {
                displayMessage("info", "Next event: " + ev.toString());
            }
        }
    }

    /**
     * Displays the status of a vehicle, indicating whether it is in transit or at a location.
     * @throws IllegalArgumentException if the vehicle does not exist.
     * @param vehicleId the ID of the vehicle to check
     */
    private void displayVehicleStatus(String vehicleId) {
        vehicleId = vehicleId.trim();
        Vehicle vehicle = vehicles.get(vehicleId);
        if (vehicle == null) {
            throw new IllegalArgumentException("Vehicle with ID " + vehicleId + " does not exist");
        }
        if (vehicle.isInTransit()) {
            displayMessage("info", "Vehicle " + vehicleId + " is in transit.");
        } else {
            displayMessage("info", "Vehicle " + vehicleId + " is at location " + vehicle.getCurrentLocation().getName() + ".");
        }
    }

    /**
     * Displays a message with a specified status and text output.
     * @param status the status of the message (e.g., "info", "error")
     * @param text_output the text to display
     */
    void displayMessage(String status, String text_output) {
        System.out.println(status.toUpperCase() + ": " + text_output);
    }

    // Hazard creation methods
    /**
     * Creates a hazard with the specified parameters (including a second location).
     * Displays an error message if the hazard ID already exists or if the locations are invalid.
     * @param description
     * @param id
     * @param type
     * @param impact
     * @param location1
     * @param location2
     */
    private void createHazard(String description, String id, HazardType type, double impact, Location location1, Location location2) {
        Hazard hazard = new Hazard(description, id, type, impact, location1, location2);
        hazards.add(hazard);
        displayMessage("info", "Hazard created: " + hazard.toString());
    }
    /**
     * Creates a hazard with the specified parameters.
     * Displays an error message if the hazard ID already exists or if the location is invalid.
     * @param description
     * @param id
     * @param type
     * @param impact
     * @param location1
     */
    private void createHazard(String description, String id, HazardType type, double impact, Location location1) {
        Hazard hazard = new Hazard(description, id, type, impact, location1);
        hazards.add(hazard);
        displayMessage("info", "Hazard created: " + hazard.toString());
    }

    public List<Hazard> getHazards() {
        return hazards;
    }

    // display hazards methods
    /**
     * Displays all hazards in the system.
     * If there are no hazards, it displays a message indicating that there are no hazards available.
     */
    private void displayHazards() {
        if (hazards.isEmpty()) {
            displayMessage("info", "No hazards available");
            return;
        }
        hazards.forEach(System.out::println);
    }
    /**
     * Displays all hazards at a specified location.
     * If there are no hazards at that location, it displays a message indicating that there are no hazards at that location.
     * @throws IllegalArgumentException if the location ID is invalid.
     * @param locationId
     */
    private void displayHazardsAtLocation(String locationId) {
        locationId = locationId.trim();
        Location location = locations.get(locationId);
        if (location == null) {
            throw new IllegalArgumentException("Invalid location ID");
        }
        List<Hazard> hazardsAtLocation = hazards.stream()
            .filter(h -> h.getLocation1().equals(location) || (h.getLocation2() != null && h.getLocation2().equals(location)))
            .collect(Collectors.toList());
        
        if (hazardsAtLocation.isEmpty()) {
            displayMessage("info", "No hazards at location " + locationId);
        } else {
            hazardsAtLocation.forEach(System.out::println);
        }
    }

    // remove hazards
    /**
     * Removes a hazard with the specified ID.
     * @throws IllegalArgumentException if the hazard ID is invalid or does not exist.
     * @param hazardId
     */
    private void removeHazard(String hazardId) {
        hazardId = hazardId.trim();
        Hazard hazardToRemove = null;
        for (Hazard hazard : hazards) {
            if (hazard.getId().equals(hazardId)) {
                hazardToRemove = hazard;
                break;
            }
        }
        if (hazardToRemove == null) {
            throw new IllegalArgumentException("Hazard with ID " + hazardId + " does not exist");
        }
        hazards.remove(hazardToRemove);
        displayMessage("info", "Hazard with ID " + hazardId + " removed successfully");
    }
}