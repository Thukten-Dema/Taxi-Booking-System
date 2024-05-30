package state;
import BookingSystem.*;

public class AcceptedState implements BookingState {
    @Override
    public void handle(BookingContext booking) {
        System.out.println("Booking is accepted. Driver is on the way.");
    }

    @Override
    public void nextState(BookingContext booking) {
        booking.setState(new CompletedState());
        System.out.println("Booking is completed. Driver has dropped off the passenger.");
    }
}
