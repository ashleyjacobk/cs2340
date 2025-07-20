package managers;

import controllers.TravelController;
import entities.Location;

import java.util.Map;

/**
 * Handles passenger‐related configuration at locations (waiting passengers and exchange ranges).
 */
public class PassengerManager {

    private final Map<String, Location> locations;
    private final TravelController controller;

    public PassengerManager(Map<String, Location> locations, TravelController controller) {
        this.locations = locations;
        this.controller = controller;
    }

    /** Sets the number of waiting passengers at a stop. */
    public void setWaitingPassengers(String locationId, int waiting) {
        Location loc = locations.get(locationId.trim());
        if (loc == null) throw new IllegalArgumentException("Location not found");
        if (waiting < 0) throw new IllegalArgumentException("Waiting passengers must be >= 0");
        loc.setWaitingPassengers(waiting);
        controller.displayMessage("info", "Waiting passengers at " + locationId + " set to " + waiting);
    }

    /** Configures boarding/debark/transfer ranges for a stop. */
    public void setPassengerRanges(String locationId,
                                   int debarkLow, int debarkHigh,
                                   int transferLow, int transferHigh,
                                   int boardLow, int boardHigh) {
        Location loc = locations.get(locationId.trim());
        if (loc == null) throw new IllegalArgumentException("Location not found");
        loc.setDebarkRange(debarkLow, debarkHigh);
        loc.setTransferRange(transferLow, transferHigh);
        loc.setBoardRange(boardLow, boardHigh);
        controller.displayMessage("info", "Passenger ranges updated for " + locationId);
    }
} 