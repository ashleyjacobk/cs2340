import org.junit.Test;
import static org.junit.Assert.*;

public class LocationTest {
    @Test
    public void testConstructorAndGetters() {
        Location loc = new Location("Main", "LOC1", 0.0, 0.0);
        assertEquals("Main", loc.getName());
        assertEquals("LOC1", loc.getId());
        assertEquals(0.0, loc.getX(), 0.0001);
        assertEquals(0.0, loc.getY(), 0.0001);
    }

    @Test
    public void testDistanceToSelf() {
        Location loc = new Location("A", "A1", 1.0, 1.0);
        assertEquals(0.0, loc.getDistanceTo(loc), 0.0001);
    }

    @Test
    public void testDistanceToOther() {
        Location loc1 = new Location("A", "A1", 0.0, 0.0);
        Location loc2 = new Location("B", "B1", 3.0, 4.0);
        assertEquals(5.0, loc1.getDistanceTo(loc2), 0.0001);
    }

    @Test
    public void testToString() {
        Location loc = new Location("Main", "LOC1", 2.5, 3.5);
        assertEquals("Main", loc.toString());
    }
} 