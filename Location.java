public class Location {
    private String locationId;
    private String name;
    private int passengerCount;

    public Location(String locationId, String name) {
        this.locationId = locationId;
        this.name = name;
        this.passengerCount = 0;
    }

    public String getLocationId() {
        return locationId;
    }

    public String getName() {
        return name;
    }

    public int getPassengerCount() {
        return passengerCount;
    }

    public void setPassengerCount(int count) {
        this.passengerCount = count;
    }

    @Override
    public String toString() {
        return locationId + ": " + name;
    }
}
