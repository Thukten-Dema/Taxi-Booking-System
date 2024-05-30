package state;

public class PendingState implements BookingState {
    @Override
    public void handle(BookingContext booking) {
        System.out.println("Booking is pending. Waiting for driver to accept.");
    }

    @Override
    public void nextState(BookingContext booking) {
        booking.setState(new AcceptedState());
        System.out.println("Booking has been accepted by a driver.");
    }
}
