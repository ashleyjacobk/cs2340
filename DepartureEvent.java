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

        if (currentLocation == null) {
            controller.displayMessage("error", "Vehicle " + vehicle.getId() + " has no current location set; cannot depart.");
            return;
        }

        if (nextLocation == null) {
            controller.displayMessage("info", "Vehicle " + vehicle.getId() + " has no next location to depart to.");
            return;
        }

        // for the edge case of single location routes
        if (currentLocation.equals(nextLocation)) {
            controller.displayMessage("info", "Vehicle " + vehicle.getId() + " is already at " + currentLocation.getName() + " and will not depart.");
            return;
        }

        vehicle.setInTransit();
        controller.displayMessage("info", "Vehicle " + vehicle.getId() + " is departing from " + currentLocation.getName() + " towards " + nextLocation.getName() + ".");

        double distance = currentLocation.distanceTo(nextLocation);
        int travelTime = controller.computeTravelMinutes(distance, vehicle.getSpeed(), currentLocation, nextLocation);
        int arrivalTime = time + travelTime;

        ArrivalEvent arrivalEvent = new ArrivalEvent(arrivalTime, vehicle, nextLocation, controller);
        controller.addEvent(arrivalEvent);
    }

    @Override
    public String toString() {
        String nextLocName = (vehicle.getNextLocation() != null) ? vehicle.getNextLocation().getName() : "None";
        return String.format("Time %d: DEPARTURE of vehicle %s to %s", time, vehicle.getId(), nextLocName);
    }
}
