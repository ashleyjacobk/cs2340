package commands;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import controllers.TravelController;

/**
 * Interprets user input and delegates execution to {@link Command} instances.
 * New commands can be registered at runtime without modifying this class, satisfying the
 * Open/Closed Principle and reducing the burden on {@code TravelController} (which now
 * focuses purely on simulation state).  
 */
public class CommandInterpreter {

    private final Map<String, Command> commandMap = new HashMap<>();
    private final TravelController controller;

    public CommandInterpreter(TravelController controller) {
        this.controller = controller;
        registerBuiltInCommands();
    }

    /**
     * Registers the core commands supported by the system.  Additional commands can be added
     * later via {@link #registerCommand(String, Command)}.
     */
    private void registerBuiltInCommands() {
        // NOTE: Only a subset of commands are demonstrated here.  Others can be added following
        // the same pattern, without changing existing code.
        commandMap.put("create_vehicle", tokens -> {
            if (tokens.length != 7) {
                throw new IllegalArgumentException("Correct usage for create_vehicle is: create_vehicle,<capacity>,<type>,<id>,<route>,<currentLocation>,<speed>");
            }
            int capacity = Integer.parseInt(tokens[1].trim());
            double speed = Double.parseDouble(tokens[6].trim());
            controller.getVehicleManager().createVehicle(capacity, tokens[2], tokens[3], tokens[4], tokens[5], speed);
        });

        commandMap.put("display_vehicles", tokens -> controller.getVehicleManager().displayVehicles());

        commandMap.put("set_vehicle_speed", tokens -> {
            if (tokens.length != 3) throw new IllegalArgumentException("Usage: set_vehicle_speed,<vehicleId>,<speed_kph>");
            controller.getVehicleManager().setVehicleSpeed(tokens[1], Double.parseDouble(tokens[2]));
        });

        commandMap.put("set_vehicle_position", tokens -> {
            if (tokens.length != 4) throw new IllegalArgumentException("Usage: set_vehicle_position,<vehicleId>,<routeId>,<position>");
            controller.getVehicleManager().setVehiclePosition(tokens[1], tokens[2], Integer.parseInt(tokens[3]));
        });

        commandMap.put("display_vehicle_status", tokens -> {
            if (tokens.length != 2) throw new IllegalArgumentException("Usage: display_vehicle_status,<vehicleId>");
            controller.getVehicleManager().displayVehicleStatus(tokens[1]);
        });

        commandMap.put("display_vehicle_riders", tokens -> {
            if (tokens.length != 2) throw new IllegalArgumentException("Usage: display_vehicle_riders,<vehicleId>");
            controller.getVehicleManager().displayVehicleRiders(tokens[1]);
        });

        commandMap.put("set_vehicle_riders", tokens -> {
            if (tokens.length != 3) throw new IllegalArgumentException("Usage: set_vehicle_riders,<vehicleId>,<numRiders>");
            controller.getVehicleManager().setVehicleRiders(tokens[1], Integer.parseInt(tokens[2]));
        });

        commandMap.put("display_vehicles_at_location", tokens -> {
            if (tokens.length != 2) throw new IllegalArgumentException("Usage: display_vehicles_at_location,<locationId>");
            controller.getVehicleManager().displayVehiclesAtLocation(tokens[1]);
        });

        commandMap.put("display_vehicles_on_route", tokens -> {
            if (tokens.length != 2) throw new IllegalArgumentException("Usage: display_vehicles_on_route,<routeId>");
            controller.getVehicleManager().displayVehiclesOnRoute(tokens[1]);
        });

        commandMap.put("advance_time", tokens -> {
            if (tokens.length != 2) {
                throw new IllegalArgumentException("Correct usage for advance_time is: advance_time,<minutes>");
            }
            controller.advanceTime(Integer.parseInt(tokens[1].trim()));
        });

        commandMap.put("jump_to_time", tokens -> {
            if (tokens.length != 2) {
                throw new IllegalArgumentException("Correct usage for jump_to_time is: jump_to_time,<time>");
            }
            controller.jumpToTime(Integer.parseInt(tokens[1].trim()));
        });

        commandMap.put("help", tokens -> System.out.println(controller.help()));

        commandMap.put("exit", tokens -> {
            System.out.println("Exiting simulation. Goodbye!");
            System.exit(0);
        });

        commandMap.put("create_location", tokens -> {
            if (tokens.length != 5) {
                throw new IllegalArgumentException("Correct usage for create_location is: create_location,<name>,<id>,<x>,<y>");
            }
            double x = Double.parseDouble(tokens[3].trim());
            double y = Double.parseDouble(tokens[4].trim());
            controller.getLocationManager().createLocation(tokens[1], tokens[2], x, y);
        });

        commandMap.put("display_locations", tokens -> controller.getLocationManager().displayLocations());

        commandMap.put("create_route", tokens -> {
            if (tokens.length != 3) throw new IllegalArgumentException("Usage: create_route,<id>,<vehicleType>");
            controller.getRouteManager().createRoute(tokens[1], tokens[2]);
        });

        commandMap.put("add_location_to_route", tokens -> {
            if (tokens.length != 4) throw new IllegalArgumentException("Usage: add_location_to_route,<routeId>,<locationId>,<position>");
            controller.getRouteManager().addLocationToRoute(tokens[1], tokens[2], Integer.parseInt(tokens[3]));
        });

        commandMap.put("remove_location_from_route", tokens -> {
            if (tokens.length != 3) throw new IllegalArgumentException("Usage: remove_location_from_route,<routeId>,<position>");
            controller.getRouteManager().removeLocationFromRoute(tokens[1], Integer.parseInt(tokens[2]));
        });

        commandMap.put("display_routes", tokens -> controller.getRouteManager().displayRoutes());

        commandMap.put("display_locations_in_route", tokens -> {
            if (tokens.length != 2) throw new IllegalArgumentException("Usage: display_locations_in_route,<routeId>");
            controller.getRouteManager().displayLocationsInRoute(tokens[1]);
        });

        // Passenger commands
        commandMap.put("set_waiting_passengers", tokens -> {
            if (tokens.length != 3) throw new IllegalArgumentException("Usage: set_waiting_passengers,<locationId>,<numWaiting>");
            controller.getPassengerManager().setWaitingPassengers(tokens[1], Integer.parseInt(tokens[2]));
        });

        commandMap.put("set_passenger_ranges", tokens -> {
            if (tokens.length != 8) throw new IllegalArgumentException("Usage: set_passenger_ranges,<locationId>,<debarkLow>,<debarkHigh>,<transferLow>,<transferHigh>,<boardLow>,<boardHigh>");
            controller.getPassengerManager().setPassengerRanges(tokens[1],
                    Integer.parseInt(tokens[2]), Integer.parseInt(tokens[3]),
                    Integer.parseInt(tokens[4]), Integer.parseInt(tokens[5]),
                    Integer.parseInt(tokens[6]), Integer.parseInt(tokens[7]));
        });

        // Hazard commands
        commandMap.put("create_hazard", tokens -> {
            if (tokens.length < 6 || tokens.length > 7) throw new IllegalArgumentException("Usage: create_hazard,<description>,<id>,<type(short_term|long_term)>,<impact>,<location1Id>,[<location2Id>]");
            entities.HazardType type = tokens[3].equals("short_term") ? entities.HazardType.SHORT_TERM : entities.HazardType.LONG_TERM;
            controller.getHazardManager().createHazard(tokens[1], tokens[2], type, Double.parseDouble(tokens[4]), tokens[5], tokens.length == 7 ? tokens[6] : null);
        });

        commandMap.put("display_hazards", tokens -> controller.getHazardManager().displayHazards());

        commandMap.put("display_hazards_at_location", tokens -> {
            if (tokens.length != 2) throw new IllegalArgumentException("Usage: display_hazards_at_location,<locationId>");
            controller.getHazardManager().displayHazardsAtLocation(tokens[1]);
        });

        commandMap.put("remove_hazard", tokens -> {
            if (tokens.length != 2) throw new IllegalArgumentException("Usage: remove_hazard,<hazardId>");
            controller.getHazardManager().removeHazard(tokens[1]);
        });
    }

    /**
     * Allows external clients to add new commands without changing the interpreter.
     */
    public void registerCommand(String keyword, Command command) {
        commandMap.put(keyword, command);
    }

    /**
     * Starts an interactive loop, accepting user commands from {@code System.in}.  This replaces the
     * command loop previously located in {@code TravelController}, illustrating the Controller GRASP pattern.
     */
    public void commandLoop() {
        Scanner scanner = new Scanner(System.in);
        final String DELIMITER = ",";
        while (true) {
            try {
                System.out.print("$> ");
                String wholeInputLine = scanner.nextLine();
                if (wholeInputLine == null || wholeInputLine.trim().isEmpty() || wholeInputLine.trim().startsWith("//")) {
                    continue;
                }
                String[] tokens = wholeInputLine.split(DELIMITER);
                Command command = commandMap.get(tokens[0]);
                if (command == null) {
                    System.out.println("ERROR: Unknown command '" + tokens[0] + "'. Type 'help' for a list of commands.");
                    continue;
                }
                command.execute(tokens);
            } catch (Exception ex) {
                System.out.println("ERROR: " + ex.getMessage());
            }
        }
    }
} 