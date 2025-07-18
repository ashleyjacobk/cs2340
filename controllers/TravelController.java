package controllers;
import java.util.*;
import managers.VehicleManager;
import java.util.stream.Collectors;

import entities.Hazard;
import entities.HazardType;
import entities.Location;
import entities.Route;
import entities.Vehicle;
import entities.VehicleType;
import events.ArrivalEvent;
import events.DepartureEvent;
import events.Event;

public class TravelController {
    private Map<String, Vehicle> vehicles;
    private Map<String, Location> locations;
    private Map<String, Route> routes;
    private List<Hazard> hazards; 
    private VehicleManager vehicleManager;

    // times -- phase 2
    // private double time = 0.0;
    private int time = 0;
    private PriorityQueue<Event> eventQueue = new PriorityQueue<>();

    public TravelController() {
        this.vehicles = new TreeMap<>();
        this.locations = new TreeMap<>();
        this.routes = new TreeMap<>();
        this.hazards = new ArrayList<>();
        this.vehicleManager = new VehicleManager(vehicles, locations, routes, this);
    }
    // Legacy text-based commandLoop removed; use commands.CommandInterpreter instead.

    public VehicleManager getVehicleManager() {
        return vehicleManager;
    }
    
    /**
     * Displays the help message with available commands.
     * 
     * @return a string containing the help message.
     */
    public String help() {
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
                "  display_vehicle_riders,<vehicleId>\n" +
                "==== Hazard Commands ====\n" +
                "  create_hazard,<description>,<id>,<type(short_term or long_term)>,<impact>,<location1Id>,[<location2Id>]\n" +
                "  display_hazards\n" +
                "  display_hazards_at_location,<locationId>\n" +
                "  remove_hazard,<hazardId>\n" +
                "==== Passenger Configuration Commands ====\n" +
                "  set_vehicle_riders,<vehicleId>,<numRiders>\n" +
                "  set_waiting_passengers,<locationId>,<numWaiting>\n" +
                "  set_passenger_ranges,<locationId>,<debarkLow>,<debarkHigh>,<transferLow>,<transferHigh>,<boardLow>,<boardHigh>\n" +
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
    public void createVehicle(int capacity, String type, String id, String route, String currentLocation, double speed) {
        VehicleType vehicleType;

        if (capacity < 0) {
            throw new IllegalArgumentException("Capacity cannot be negative");
        }

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

    // Vehicle display operations are now handled by VehicleManager.

    /**
     * Creates a location with the specified name.
     * @throws IllegalArgumentException if the location already exists or if the ID is not globally unique.
     * @param name
     * @param id usually an abbreviation of the name, but can be anything
     * @param x
     * @param y
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
    public void createRoute(String id, String type) {
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

        if (removed != null) {
            eventQueue.removeIf(ev -> (ev instanceof ArrivalEvent) && ((ArrivalEvent) ev).getDestination().equals(removed));
        }

        // Handle vehicles that were at the removed location
        for (Vehicle vehicle : vehicles.values()) {
            if (vehicle.getRoute() == route && vehicle.getCurrentLocation() == removed) {
                if (route.getLocations().isEmpty()) {
                    vehicle.arriveAt(null);
                    displayMessage("info", "Vehicle " + vehicle.getId() + " is now on an empty route " + routeId + " and will not move.");
                } else {
                    int nextPosition = position % route.getLocations().size(); // Wrap around if needed
                    vehicle.setCurrentPosition(nextPosition);
                    displayMessage("info", "Vehicle " + vehicle.getId() + " repositioned to " + route.getLocations().get(nextPosition).getName());

                    DepartureEvent newDeparture = new DepartureEvent(time, vehicle, this);
                    addEvent(newDeparture);
                    displayMessage("info", "New departure scheduled for vehicle " + vehicle.getId() + " at time " + time);
                }
            }
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

    /* Removed vehicle display methods (now in VehicleManager) */

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
     * Displays a message with a specified status and text output.
     * @param status the status of the message (e.g., "info", "error")
     * @param text_output the text to display
     */
    public void displayMessage(String status, String text_output) {
        System.out.println(status.toUpperCase() + ": " + text_output);
    }

    // Hazard creation methods
    /**
     * Creates a hazard with the specified parameters.
     * @throws IllegalArgumentException if the hazard ID already exists, or if the ID is invalid.
     * @param description
     * @param id
     * @param type
     * @param impact
     * @param location1
     * @param location2 optional second location for two-location hazards
     */
    private void createHazard(String description, String id, HazardType type, double impact, Location location1, Location location2) {
        Hazard hazard = new Hazard(description, id, type, impact, location1, location2);
        hazards.add(hazard);
        displayMessage("info", "Hazard created: " + hazard.toString());

        if (type == HazardType.SHORT_TERM) {
            List<Event> eventsToAdd = new ArrayList<>();
            Iterator<Event> iterator = eventQueue.iterator();

            while (iterator.hasNext()) {
                Event e = iterator.next();
                if (e instanceof DepartureEvent) {
                    DepartureEvent de = (DepartureEvent) e;
                    Vehicle v = de.getVehicle();

                    if (v != null && !v.isInTransit() && v.getCurrentLocation() != null && hazard.affectsLocation(v.getCurrentLocation())) {
                        iterator.remove();
                        int newDepartureTime = de.getTime() + (int) impact;
                        DepartureEvent newEvent = new DepartureEvent(newDepartureTime, v, this);
                        eventsToAdd.add(newEvent);
                        displayMessage("info", "Departure for vehicle " + v.getId() + " delayed due to new hazard. New departure at " + newDepartureTime);
                    }
                }
            }
            eventQueue.addAll(eventsToAdd);
        }
    }
    private void createHazard(String description, String id, HazardType type, double impact, Location location1) {
        createHazard(description, id, type, impact, location1, null);
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

    // === PASSENGER CONFIGURATION METHODS ===
    private void setVehicleRiders(String vehicleId, int riders) {
        vehicleId = vehicleId.trim();
        Vehicle v = vehicles.get(vehicleId);
        if (v == null) {
            throw new IllegalArgumentException("Vehicle with ID " + vehicleId + " does not exist");
        }
        v.setCurrentPassengers(riders);
        displayMessage("info", "Vehicle " + vehicleId + " rider count set to " + riders);
    }

    private void setWaitingPassengers(String locationId, int waiting) {
        locationId = locationId.trim();
        Location loc = locations.get(locationId);
        if (loc == null) {
            throw new IllegalArgumentException("Location with ID " + locationId + " does not exist");
        }

        if (waiting < 0) {
            throw new IllegalArgumentException("Waiting passenger count cannot be negative");
        }

        loc.setWaitingPassengers(waiting);
        displayMessage("info", "Location " + locationId + " waiting passengers set to " + waiting);
    }

    private void setPassengerRanges(String locationId, int debarkLow, int debarkHigh, int transferLow, int transferHigh, int boardLow, int boardHigh) {
        locationId = locationId.trim();
        Location loc = locations.get(locationId);
        if (loc == null) {
            throw new IllegalArgumentException("Location with ID " + locationId + " does not exist");
        }
        loc.setDebarkRange(debarkLow, debarkHigh);
        loc.setTransferRange(transferLow, transferHigh);
        loc.setBoardRange(boardLow, boardHigh);
        displayMessage("info", "Passenger ranges updated for location " + locationId);
    }

    /**
     * Read-only view of all locations, keyed by ID, for UI rendering purposes.
     */
    public Map<String, Location> getLocations() {
        return Collections.unmodifiableMap(locations);
    }

    /**
     * Read-only view of all routes, keyed by ID.
     */
    public Map<String, Route> getRoutes() {
        return Collections.unmodifiableMap(routes);
    }

    /**
     * Read-only view of all vehicles, keyed by ID.
     */
    public Map<String, Vehicle> getVehicles() {
        return Collections.unmodifiableMap(vehicles);
    }
}