public class Hazard {
    private String description;
    private String id;
    private HazardType type;
    private int impact;
    private Location location1;
    private Location location2; // second location optional if it is a hazard affecting the connection b/w two locations

    public Hazard(String description, String id, HazardType type, int impact, Location location1, Location location2) {
        this.description = description;
        this.id = id;
        this.type = type;
        this.impact = impact;
        this.location1 = location1;
        this.location2 = location2;
    }
    public Hazard(String description, String id, HazardType type, int impact, Location location1) {
        this(description, id, type, impact, location1, null);
    }

    /* GETTERS */
    public String getDescription() {
        return description;
    }
    public String getId() {
        return id;
    }
    public HazardType getType() {
        return type;
    }
    public int getImpact() {
        return impact;
    }
    public Location getLocation1() {
        return location1;
    }
    public Location getLocation2() {
        return location2;
    }

    /* METHODS */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Hazard with ID: ").append(getId())
          .append(", Description: ").append(getDescription())
          .append(", Type: ").append(getType())
          .append(", Impact: ").append(getImpact())
          .append(", Location1: ").append(location1.getName());
        if (location2 != null) {
            sb.append(", Location2: ").append(location2.getName());
        }
        return sb.toString();
    }

    /**
     * Checks if the hazard affects a specific location.
     * @param location
     * @return true if the hazard affects the location, false otherwise
     */
    public boolean affectsLocation(Location location) {
        return location.equals(location1) || (location2 != null && location.equals(location2));
    }

    /**
     * Checks if the hazard affects the connection between two locations.
     * @param loc1
     * @param loc2
     * @return true if the hazard affects the connection, false otherwise
     */
    public boolean affectsConnection(Location loc1, Location loc2) {
        return (location1.equals(loc1) && location2.equals(loc2)) || (location1.equals(loc2) && location2.equals(loc1));
    }

    /**
     * Checks if the hazard is a short-term hazard.
     * @return true if the hazard is short-term, false otherwise
     */
    public boolean isShortTerm() {
        return type == HazardType.SHORT_TERM;
    }

    /**
     * Checks if the hazard is a long-term hazard.
     * @return true if the hazard is long-term, false otherwise
     */
    public boolean isLongTerm() {
        return type == HazardType.LONG_TERM;
    }
}
