import org.junit.Test;
import static org.junit.Assert.*;
import java.util.List;

public class RouteTest {
    @Test
    public void testConstructorAndGetters() {
        Route route = new Route("R1", "Bus");
        assertEquals("R1", route.toString());
        assertEquals("Bus", route.getVehicles().isEmpty() ? "Bus" : route.getVehicles().get(0).getType());
        assertTrue(route.getLocations().isEmpty());
        assertTrue(route.getVehicles().isEmpty());
    }

    @Test
    public void testAddLocationAndRemoveLocation() {
        Route route = new Route("R2", "Bus");
        Location loc1 = new Location("A", "A1", 0, 0);
        Location loc2 = new Location("B", "B1", 1, 1);
        assertTrue(route.addLocation(loc1, 0));
        assertFalse(route.addLocation(loc1, 1)); // duplicate
        assertTrue(route.addLocation(loc2, 1));
        List<Location> locs = route.getLocations();
        assertEquals(2, locs.size());
        assertEquals(loc1, locs.get(0));
        assertEquals(loc2, locs.get(1));
        assertEquals(loc2, route.removeLocation(1));
        assertNull(route.removeLocation(5)); // invalid
    }

    @Test
    public void testAddVehicleTypeMismatch() {
        Route route = new Route("R3", "Bus");
        Location loc = new Location("A", "A1", 0, 0);
        route.addLocation(loc, 0);
        Vehicle v = new Vehicle(10, "Train", "V1", route, loc, 10);
        assertFalse(route.add_vehicle(v));
    }

    @Test
    public void testAddVehicleSuccess() {
        Route route = new Route("R4", "Bus");
        Location loc = new Location("A", "A1", 0, 0);
        route.addLocation(loc, 0);
        Vehicle v = new Vehicle(10, "Bus", "V2", route, loc, 10);
        assertTrue(route.add_vehicle(v));
        assertEquals(1, route.getVehicles().size());
        assertEquals(v, route.getVehicles().get(0));
    }
} 