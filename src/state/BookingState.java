package state;

public interface BookingState {
    void handle(BookingContext booking);
    void nextState(BookingContext booking);
}
