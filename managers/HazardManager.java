package managers;

import controllers.TravelController;
import entities.Hazard;
import entities.HazardType;
import entities.Location;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import events.DepartureEvent;
import events.Event;
import entities.Vehicle;

/** Handles hazard creation and queries. */
public class HazardManager {

    private final List<Hazard> hazards;
    private final Map<String, Location> locations;
    private final TravelController controller;

    public HazardManager(List<Hazard> hazards,
                         Map<String, Location> locations,
                         TravelController controller) {
        this.hazards = hazards;
        this.locations = locations;
        this.controller = controller;
    }

    public void createHazard(String description, String id, HazardType type, double impact, String loc1Id, String loc2Id) {
        Location loc1 = locations.get(loc1Id.trim());
        Location loc2 = (loc2Id == null || loc2Id.isEmpty()) ? null : locations.get(loc2Id.trim());
        if (loc1 == null) throw new IllegalArgumentException("Location1 not found");
        if (loc2Id != null && !loc2Id.isEmpty() && loc2 == null) throw new IllegalArgumentException("Location2 not found");
        if (!isIdGloballyUnique(id)) throw new IllegalArgumentException("Hazard id must be unique");
        Hazard hazard = new Hazard(description, id, type, impact, loc1, loc2);
        hazards.add(hazard);
        controller.displayMessage("info", "Hazard created: " + hazard.toString());

        // If short term, reschedule immediate departures
        if (type == HazardType.SHORT_TERM) {
            List<Event> toAdd = new ArrayList<>();
            Iterator<Event> it = controller.getEventQueue().iterator();
            while (it.hasNext()) {
                Event e = it.next();
                if (e instanceof DepartureEvent de) {
                    Vehicle v = de.getVehicle();
                    if (v != null && !v.isInTransit() && v.getCurrentLocation() != null && hazard.affectsLocation(v.getCurrentLocation())) {
                        it.remove();
                        int newTime = de.getTime() + (int) impact;
                        toAdd.add(new DepartureEvent(newTime, v, controller));
                    }
                }
            }
            controller.getEventQueue().addAll(toAdd);
        }
    }

    public void displayHazards() {
        if (hazards.isEmpty()) {
            controller.displayMessage("info", "No hazards available");
        } else hazards.forEach(System.out::println);
    }

    public void displayHazardsAtLocation(String locationId) {
        Location loc = locations.get(locationId.trim());
        if (loc == null) throw new IllegalArgumentException("Location not found");
        boolean any = false;
        for (Hazard h : hazards) {
            if (h.affectsLocation(loc)) {
                controller.displayMessage("info", h.toString());
                any = true;
            }
        }
        if (!any) controller.displayMessage("info", "No hazards at location " + locationId);
    }

    public void removeHazard(String hazardId) {
        hazards.removeIf(h -> h.getId().equals(hazardId.trim()));
        controller.displayMessage("info", "Hazard " + hazardId + " removed");
    }

    private boolean isIdGloballyUnique(String id) {
        for (Hazard h : hazards) {
            if (h.getId().equals(id)) return false;
        }
        return true;
    }
} 