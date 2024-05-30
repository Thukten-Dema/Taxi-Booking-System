package observer;
import BookingSystem.*;

public class DriverObserver implements Observer {
    private String driverEmail;

    public DriverObserver(String driverEmail) {
        this.driverEmail = driverEmail;
    }

    @Override
    public void update(String message) {
        // Implement logic to notify the driver
        System.out.println("Notification to driver (" + driverEmail + "): " + message);
    }
}
