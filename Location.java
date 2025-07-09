import java.util.ArrayList;
import java.util.List;

public class Location {
    private String name;
    private int totalPassengers;
    private boolean status;
    private List<Vehicle> vehicles;
    private String id;
    private double x, y; // coordinates
    private int waitingPassengers;

    // ranges for passenger exchange operations
    private int debarkLow = 0, debarkHigh = 0;
    private int transferLow = 0, transferHigh = 0;
    private int boardLow = 0, boardHigh = 0;

    /* CONSTRUCTERS */
    public Location(String name, String id, int waitingPassengers, boolean status, double x, double y) {
        this.name = name;
        this.id = id;
        this.waitingPassengers = waitingPassengers;
        this.status = status;
        this.vehicles = new ArrayList<>();
        this.x = x;
        this.y = y;
    }
    public Location(String name, String id, int waitingPassengers) {
        this(name, id, waitingPassengers, true, 0.0, 0.0);
    }
    public Location(String name, String id) {
        this(name, id, 0, true, 0.0, 0.0);
    }

    /* GETTERS */
    public String getName() {
        return name;
    }
    public String getId() { 
        return id; 
    }
    public boolean getStatus() {
        return status;
    }
    public List<Vehicle> getVehicles() {
        return vehicles;
    }
    public double getX() {
        return x;
    }
    public double getY() {
        return y;
    }
    public int getWaitingPassengers() {
        return waitingPassengers;
    }
    public int getDebarkLow() { 
        return debarkLow; 
    }
    public int getDebarkHigh() { 
        return debarkHigh; 
    }
    public int getTransferLow() { 
        return transferLow; 
    }
    public int getTransferHigh() { 
        return transferHigh; 
    }
    public int getBoardLow() { 
        return boardLow; 
    }
    public int getBoardHigh() { 
        return boardHigh; 
    }

    /* SETTERS */
    public void setName(String name) {
        this.name = name;
    }
    public void setWaitingPassengers(int waitingPassengers) {
        if (waitingPassengers < 0) {
            throw new IllegalArgumentException("Waiting passenger count cannot be negative");
        }
        this.waitingPassengers = waitingPassengers;
    }
    public void setTotalPassengers(int totalPassengers) {
        if (totalPassengers < 0) {
            throw new IllegalArgumentException("Passenger count cannot be negative");
        }
        this.waitingPassengers = totalPassengers;
    }
    public void setStatus(boolean status) {
        this.status = status;
    }
    public void setDebarkRange(int low, int high) {
        validateRange(low, high);
        this.debarkLow = low;
        this.debarkHigh = high;
    }
    public void setTransferRange(int low, int high) {
        validateRange(low, high);
        this.transferLow = low;
        this.transferHigh = high;
    }
    public void setBoardRange(int low, int high) {
        validateRange(low, high);
        this.boardLow = low;
        this.boardHigh = high;
    }

    /* METHODS */
    
    public double distanceTo(Location location) {
        double dx = this.x - location.getX();
        double dy = this.y - location.getY();
        return Math.sqrt(dx * dx + dy * dy);
    }
    
    @Override
    public String toString() {
        return this.name;
    }

    private void validateRange(int low, int high) {
        if (low < 0 || high < 0 || low > high) {
            throw new IllegalArgumentException("Invalid range: low=" + low + " high=" + high);
        }
    }
}