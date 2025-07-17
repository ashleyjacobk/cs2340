package events;
import java.util.Random;

import controllers.TravelController;
import entities.Hazard;
import entities.Location;
import entities.Vehicle;

public class ArrivalEvent implements Event {
    private final int time;
    private final Vehicle vehicle;
    private final Location destination;
    private final TravelController controller;
    private final Location origin;

    public ArrivalEvent(int time, Vehicle vehicle, Location origin, Location destination, TravelController controller) {
        this.time = time;
        this.vehicle = vehicle;
        this.origin = origin;
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

        // Passenger exchange processing
        Location loc = destination;
        Random rand = new Random();

        int ridePassCur = vehicle.getCurrentPassengers();
        int waitPassCur = loc.getWaitingPassengers();

        // Determine debarking passengers
        int debarkLow = loc.getDebarkLow();
        int debarkHigh = loc.getDebarkHigh();
        int debark = randInRange(rand, debarkLow, debarkHigh);
        debark = Math.min(debark, ridePassCur);
        int debarked = vehicle.debarkPassengers(debark);

        // Determine transfers (subset of debarked)
        int transferLow = loc.getTransferLow();
        int transferHigh = loc.getTransferHigh();
        int transfer = randInRange(rand, transferLow, transferHigh);
        transfer = Math.min(transfer, debarked);

        // Determine boarding passengers
        int boardLow = loc.getBoardLow();
        int boardHigh = loc.getBoardHigh();
        int boardAttempt = randInRange(rand, boardLow, boardHigh);
        boardAttempt = Math.min(boardAttempt, waitPassCur);

        int boarded = vehicle.boardPassengers(boardAttempt);
        int delayed = boardAttempt - boarded;

        // Update waiting passengers at the location
        // Ensure the new waiting passenger count can never be negative.
        int waitPassNext = Math.max(0, waitPassCur - boarded + transfer);
        loc.setWaitingPassengers(waitPassNext);

        // Report exchange summary
        controller.displayMessage("info", String.format("Passenger exchange at %s: debarked=%d, transfer=%d, boarded=%d, delayed=%d, riders now=%d, waiting=%d", loc.getName(), debarked, transfer, boarded, delayed, vehicle.getCurrentPassengers(), waitPassNext));

        // Determine exchange time (default 1 minute + short-term hazard impact)
        int exchangeTime = 1;
        for (Hazard hazard : controller.getHazards()) {
            if (hazard.isShortTerm() && hazard.affectsLocation(destination)) {
                controller.displayMessage("info", "Applying short-term hazard " + hazard.getDescription() + " to vehicle " + vehicle.getId() + ".");
                exchangeTime += (int) hazard.getImpact();
            }
        }

        int departureTime = this.time + exchangeTime;
        DepartureEvent departureEvent = new DepartureEvent(departureTime, vehicle, controller);
        controller.addEvent(departureEvent);

        controller.displayMessage("info", "Vehicle " + vehicle.getId() + " has arrived at " + destination.getName() + " from " + (origin != null ? origin.getName() : "unknown") + ". Departure scheduled at time " + departureTime + ".");
    }

    private int randInRange(Random rand, int low, int high) {
        if (low > high) {
            return 0;
        }
        if (low == high) {
            return low;
        }
        return rand.nextInt(high - low + 1) + low;
    }

    @Override
    public String toString() {
        return String.format("Time %d: ARRIVAL of vehicle %s from %s to %s", time, vehicle.getId(), (origin != null ? origin.getName() : "unknown"), destination.getName());
    }

    public Location getDestination() {
        return destination;
    }

    public Location getOrigin() {
        return origin;
    }
}
