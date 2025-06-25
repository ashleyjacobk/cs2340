import org.junit.Test;
import static org.junit.Assert.*;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class EdgeCaseTest {

    @Test
    public void testAddTimeNegative() {
        TravelController tc = new TravelController();
        tc.addTime(10.0);
        double timeAfterAdd = tc.getCurrentTime();
        assertEquals(10.0, timeAfterAdd, 0.0001);
        tc.addTime(-5.0); // should not change time
        assertEquals(10.0, tc.getCurrentTime(), 0.0001);
    }

    @Test
    public void testZeroSpeedVehicle() {
        TravelController tc = new TravelController();
        tc.createLocation("Main", "LOC1", 0, 0);
        tc.createRoute("R1", "Bus");
        tc.addLocationToRoute("R1", "LOC1", 0);
        tc.createVehicle(10, "Bus", "V1", "R1", "LOC1", 0.0); // speed 0
        tc.getVehicleArrival("V1"); // should indicate currently at location / no arrival
        assertEquals(Double.POSITIVE_INFINITY, tc.getNextEventTime(), 0.0);
    }

    @Test
    public void testDistanceCalculation() {
        Location a = new Location("A", "A1", 0, 0);
        Location b = new Location("B", "B1", 3, 4); // distance 5
        assertEquals(5.0, a.getDistanceTo(b), 0.0001);
    }

    @Test
    public void testJumpToNextEventUpdatesTime() {
        TravelController tc = new TravelController();
        // Schedule a custom event 2 minutes in the future
        tc.scheduleEvent(new Event(2.0, "VEHICLE_ARRIVAL", "NON_EXISTENT"));
        tc.jumpToNextEvent();
        assertEquals(2.0, tc.getCurrentTime(), 0.0001);
    }

    @Test
    public void testDuplicateLocationId() {
        TravelController tc = new TravelController();
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outContent));

        tc.createLocation("Main", "DUP1", 0, 0);
        tc.createLocation("Secondary", "DUP1", 1, 1); // duplicate ID

        System.setOut(originalOut);
        String output = outContent.toString();
        assertTrue(output.contains("already used"));
    }
} 