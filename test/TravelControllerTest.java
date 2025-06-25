import org.junit.Test;
import static org.junit.Assert.*;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class TravelControllerTest {
    @Test
    public void testCreateLocationAndVehicle() {
        TravelController tc = new TravelController();
        tc.createLocation("Main", "LOC1", 0, 0);
        tc.createRoute("R1", "Bus");
        tc.addLocationToRoute("R1", "LOC1", 0);
        tc.createVehicle(10, "Bus", "V1", "R1", "LOC1", 5.0);
        // No exceptions = pass
    }

    @Test
    public void testInvalidVehicleId() {
        TravelController tc = new TravelController();
        tc.createLocation("Main", "LOC1", 0, 0);
        tc.createRoute("R1", "Bus");
        tc.addLocationToRoute("R1", "LOC1", 0);
        tc.createVehicle(10, "Bus", "V@#", "R1", "LOC1", 5.0); // invalid id
        // Should not throw
    }

    @Test
    public void testAddTimeAndEvents() {
        TravelController tc = new TravelController();
        tc.createLocation("Main", "LOC1", 0, 0);
        tc.createRoute("R1", "Bus");
        tc.addLocationToRoute("R1", "LOC1", 0);
        tc.createVehicle(10, "Bus", "V1", "R1", "LOC1", 5.0);
        tc.addTime(10.0); // Should process events
    }

    @Test
    public void testCommandLoopExit() {
        TravelController tc = new TravelController();
        String input = "exit\n";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);
        tc.commandLoop();
        System.setIn(System.in); // restore
    }
} 