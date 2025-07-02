public class Main {
    public static void main(String[] args) {
        System.out.println("Welcome to the Mass Transit Simulation System! To start, type 'help' for a list of commands.");
        TravelController simulator = new TravelController();
        simulator.commandLoop();
    }
}