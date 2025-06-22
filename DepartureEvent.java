public class DepartureEvent implements Event {
    private final int time;
    private final Vehicle vehicle;
    private final TravelController controller;

    public DepartureEvent(int time, Vehicle vehicle, TravelController controller) {
        this.time = time;
        this.vehicle = vehicle;
        this.controller = controller;
    }

    @Override
    public int getTime() {
        return time;
    }

    @Override
    public void execute() {
        Location currentLocation = vehicle.getCurrentLocation();
        Location nextLocation = vehicle.getNextLocation();

        if (nextLocation == null) {
            System.out.println("INFO: Vehicle " + vehicle.getId() + " has no next location to depart to.");
            return;
        }

        vehicle.setInTransit();
        System.out.println("TIME: " + time + " - DEPART: Vehicle " + vehicle.getId() + " is departing from " + currentLocation.getName() + " towards " + nextLocation.getName() + ".");

        double distance = currentLocation.distanceTo(nextLocation);
        int travelTime = controller.computeTravelMinutes(distance, vehicle.getSpeed());
        int arrivalTime = time + travelTime;

        ArrivalEvent arrivalEvent = new ArrivalEvent(arrivalTime, vehicle, nextLocation, controller);
        controller.addEvent(arrivalEvent);
    }

    @Override
    public String toString() {
        return "DepartureEvent{" +
                "time=" + time +
                ", vehicle=" + vehicle.getId() +
                ", nextLocation=" + (vehicle.getNextLocation() != null ? vehicle.getNextLocation().getName() : "None") +
                '}';
    }
}
