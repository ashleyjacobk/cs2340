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
        controller.displayMessage("info", "Vehicle " + vehicle.getId() + " has arrived at " + destination.getName() + ".");
        int exchangeTime = 1; // default exchange time
        // account for short-term hazards
        for (Hazard hazard : controller.getHazards()) {
            if (hazard.isShortTerm() && hazard.affectsLocation(destination)) {
                controller.displayMessage("info", "Applying short-term hazard " + hazard.getDescription() + " to vehicle " + vehicle.getId() + ".");
                exchangeTime += (int) hazard.getImpact();
            }
        }
        int departureTime = this.time + exchangeTime;
        DepartureEvent departureEvent = new DepartureEvent(departureTime, vehicle, controller);
        controller.addEvent(departureEvent);
    }

    @Override
    public String toString() {
        return String.format("Time %d: ARRIVAL of vehicle %s at %s", time, vehicle.getId(), destination.getName());
    }

    public Location getDestination() {
        return destination;
    }
}
