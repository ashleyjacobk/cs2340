public class Vehicle {
    private String vehicleId;
    private String vehicleType;
    private String licensePlate;
    private int capacity;
    private Route currentRoute;
    private Location currentLocation;
    private int currentPosition;

    public Vehicle(String vehicleId, String vehicleType, String licensePlate, int capacity) {
        this.vehicleId = vehicleId;
        this.vehicleType = vehicleType;
        this.licensePlate = licensePlate;
        this.capacity = capacity;
        this.currentPosition = -1;
    }

    public String getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(String vehicleId) {
        this.vehicleId = vehicleId;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public Route getCurrentRoute() {
        return currentRoute;
    }

    public void setCurrentRoute(Route route) {
        this.currentRoute = route;
        if (route != null) {
            route.assignVehicle(this);
        }
    }

    public Location getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(Location location) {
        this.currentLocation = location;
    }

    public int getCurrentPosition() {
        return currentPosition;
    }

    public void setCurrentPosition(int position) {
        if (currentRoute != null && position >= 0 && position < currentRoute.getLocations().size()) {
            this.currentPosition = position;
            this.currentLocation = currentRoute.getLocations().get(position);
        } else {
            throw new IllegalArgumentException("Invalid position for current route");
        }
    }

    @Override
    public String toString() {
        return vehicleId + " (" + vehicleType + ") - " + licensePlate;
    }
}