package state;
import BookingSystem.*;

public class CompletedState implements BookingState {
    @Override
    public void handle(BookingContext booking) {
        System.out.println("Booking is completed. Thank you for using our service.");
    }

    @Override
    public void nextState(BookingContext booking) {
        System.out.println("Booking is already completed. No further state transitions.");
    }
}
