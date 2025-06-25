import org.junit.Test;
import static org.junit.Assert.*;

public class EventTest {
    @Test
    public void testConstructorAndGetters() {
        Event event = new Event(10.5, "TYPE", "desc");
        assertEquals(10.5, event.getTime(), 0.0001);
        assertEquals("TYPE", event.getType());
        assertEquals("desc", event.getDescription());
    }

    @Test
    public void testCompareTo() {
        Event e1 = new Event(5.0, "A", "desc1");
        Event e2 = new Event(10.0, "B", "desc2");
        Event e3 = new Event(5.0, "C", "desc3");
        assertTrue(e1.compareTo(e2) < 0);
        assertTrue(e2.compareTo(e1) > 0);
        assertEquals(0, e1.compareTo(e3));
    }

    @Test
    public void testToString() {
        Event event = new Event(7.25, "ARRIVAL", "bus1");
        assertEquals("7.25: ARRIVAL - bus1", event.toString());
    }
} 