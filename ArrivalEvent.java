public class ArrivalEvent implements Event {
    private final int time;
    private final Vehicle vehicle;
    private final Location destination;
    private final TravelController controller;

    public ArrivalEvent(int time, Vehicle vehicle, Location destination, TravelController controller) {
        this.time = time;
        this.vehicle = vehicle;
        this.destination = destination;
        this.controller = controller;
    }
    
    @Override
    public int getTime() {
        return time;
    }

    @Override
    public void execute() {
        vehicle.arriveAt(destination);
        System.out.println("TIME: " + time + " - ARRIVE: Vehicle " + vehicle.getId() + " has arrived at " + destination.getName() + ".");

        int departureTime = this.time + 1;
        DepartureEvent departureEvent = new DepartureEvent(departureTime, vehicle, controller);
        controller.addEvent(departureEvent);
    }

    @Override
    public String toString() {
        return "ArrivalEvent{" +
                "time=" + time +
                ", vehicle=" + vehicle.getId() +
                ", destination=" + destination.getName() +
                '}';
    }
}
