package observer;
import BookingSystem.*;

public class PassengerObserver implements Observer {
    private String passengerEmail;

    public PassengerObserver(String passengerEmail) {
        this.passengerEmail = passengerEmail;
    }

    @Override
    public void update(String message) {
        // Implement logic to notify the passenger
        System.out.println("Notification to passenger (" + passengerEmail + "): " + message);
    }
}
