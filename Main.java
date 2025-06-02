public class Main {
    public static void main(String[] args) {
        System.out.println("Welcome to the Mass Transit Simulation System!");
        TravelController simulator = new TravelController();
        simulator.commandLoop();
    }
}