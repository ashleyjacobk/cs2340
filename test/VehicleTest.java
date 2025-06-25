import org.junit.Test;
import static org.junit.Assert.*;

public class VehicleTest {
    @Test
    public void testConstructorAndGetters() {
        Route route = new Route("R1", "Bus");
        Location loc = new Location("Main", "LOC1", 0, 0);
        route.addLocation(loc, 0);
        Vehicle v = new Vehicle(20, "Bus", "V1", route, loc, 10.0);
        assertEquals("Bus", v.getType());
        assertEquals("V1", v.getId());
        assertEquals(route, v.getRoute());
        assertEquals(loc, v.getCurrentLocation());
        assertEquals(10.0, v.getSpeed(), 0.0001);
        assertTrue(v.isAtLocation());
    }

    @Test
    public void testSetCurrentPosition() {
        Route route = new Route("R2", "Bus");
        Location loc1 = new Location("A", "A1", 0, 0);
        Location loc2 = new Location("B", "B1", 1, 1);
        route.addLocation(loc1, 0);
        route.addLocation(loc2, 1);
        Vehicle v = new Vehicle(10, "Bus", "V2", route, loc1, 5.0);
        v.setCurrentPosition(1);
        assertEquals(loc2, v.getCurrentLocation());
    }

    @Test
    public void testUpdateArrivalTimeAndState() {
        Route route = new Route("R3", "Bus");
        Location loc1 = new Location("A", "A1", 0, 0);
        Location loc2 = new Location("B", "B1", 3, 4);
        route.addLocation(loc1, 0);
        route.addLocation(loc2, 1);
        Vehicle v = new Vehicle(10, "Bus", "V3", route, loc1, 5.0);
        v.setAtLocation(false);
        v.setAbsoluteArrivalTime(2.0);
        v.updateState(2.0);
        assertTrue(v.isAtLocation());
        assertEquals(loc2, v.getCurrentLocation());
    }

    @Test
    public void testToStringAtLocation() {
        Route route = new Route("R4", "Bus");
        Location loc = new Location("Main", "LOC1", 0, 0);
        route.addLocation(loc, 0);
        Vehicle v = new Vehicle(10, "Bus", "V4", route, loc, 5.0);
        assertTrue(v.toString().contains("is at"));
    }

    @Test
    public void testToStringTraveling() {
        Route route = new Route("R5", "Bus");
        Location loc1 = new Location("A", "A1", 0, 0);
        Location loc2 = new Location("B", "B1", 1, 1);
        route.addLocation(loc1, 0);
        route.addLocation(loc2, 1);
        Vehicle v = new Vehicle(10, "Bus", "V5", route, loc1, 5.0);
        v.setAtLocation(false);
        v.setAbsoluteArrivalTime(3.0);
        assertTrue(v.toString().contains("traveling from"));
    }
} 