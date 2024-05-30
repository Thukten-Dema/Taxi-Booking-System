package command;

import ui.DriverHomePage;
import BookingSystem.Command;

public class AcceptBookingCommand implements Command {
    private DriverHomePage driverHomePage;
    private int row;

    public AcceptBookingCommand(DriverHomePage driverHomePage, int row) {
        this.driverHomePage = driverHomePage;
        this.row = row;
    }

    @Override
    public void execute() {
        driverHomePage.updateBookingStatus(row, "Accepted");
    }
}
