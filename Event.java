public class Event implements Comparable<Event> {
    private double time;
    private String type;
    private String description;

    public Event(double time, String type, String description) {
        this.time = time;
        this.type = type;
        this.description = description;
    }

    public double getTime() {
        return time;
    }

    public String getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public int compareTo(Event other) {
        return Double.compare(this.time, other.time);
    }

    @Override
    public String toString() {
        return String.format("%.2f: %s - %s", time, type, description);
    }
} 