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
    private managers.LocationManager locationManager;
    private managers.RouteManager routeManager;
    private managers.PassengerManager passengerManager;
    private managers.HazardManager hazardManager;

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
        this.locationManager = new managers.LocationManager(locations, this);
        this.routeManager = new managers.RouteManager(routes, locations, this);
        this.passengerManager = new managers.PassengerManager(locations, this);
        this.hazardManager = new managers.HazardManager(hazards, locations, this);
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

    // Location creation and display responsibilities moved to LocationManager to adhere to SRP.

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

    // Hazard responsibilities moved to HazardManager.

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

    // Passenger-specific operations moved to PassengerManager.

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

    public managers.LocationManager getLocationManager() {
        return locationManager;
    }

    public managers.RouteManager getRouteManager() {
        return routeManager;
    }

    public managers.PassengerManager getPassengerManager() {
        return passengerManager;
    }

    public managers.HazardManager getHazardManager() {
        return hazardManager;
    }

    public java.util.PriorityQueue<events.Event> getEventQueue() {
        return eventQueue;
    }

    public java.util.List<entities.Hazard> getHazards() {
        return hazards;
    }
}