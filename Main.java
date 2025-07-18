import javafx.application.Application;
import controllers.TravelController;
import commands.CommandInterpreter;

public class Main {
    public static void main(String[] args) {
        System.out.println("Welcome to the Mass Transit Simulation System! To start, type 'help' for a list of commands.");
        TravelController simulator = new TravelController();
        Application.launch(Browser.class, args);
        // Delegating command processing to a dedicated interpreter (GRASP Controller)
        CommandInterpreter interpreter = new CommandInterpreter(simulator);
        interpreter.commandLoop();
    }
}