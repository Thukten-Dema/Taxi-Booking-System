package command;
import ui.DriverHomePage;
import BookingSystem.Command;


public class CancelBookingCommand implements Command {
    private DriverHomePage driverHomePage;
    private int row;

    public CancelBookingCommand(DriverHomePage driverHomePage, int row) {
        this.driverHomePage = driverHomePage;
        this.row = row;
    }

    @Override
    public void execute() {
        driverHomePage.updateBookingStatus(row, "Canceled");
    }
}
