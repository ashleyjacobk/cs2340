public interface Event extends Comparable<Event> {
    int getTime();
    void execute();

    @Override
    default int compareTo(Event other) {
        return Integer.compare(this.getTime(), other.getTime());
    }
}
