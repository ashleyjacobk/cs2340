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
            controller.createVehicle(capacity, tokens[2], tokens[3], tokens[4], tokens[5], speed);
        });

        commandMap.put("display_vehicles", tokens -> controller.displayVehicles());

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