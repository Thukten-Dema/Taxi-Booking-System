// BookingContext.java
package state;

import BookingSystem.*;

public class BookingContext {
    private BookingState state;

    public BookingContext() {
        state = new PendingState();  // Initial state
    }

    public void setState(BookingState state) {
        this.state = state;
    }

    public void handle() {
        state.handle(this);
    }

    public void nextState() {
        state.nextState(this);
    }
}
