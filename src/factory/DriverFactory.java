package factory;
import BookingSystem.*;

// DriverFactory class implementing UserFactory interface
public class DriverFactory implements UserFactory {
    @Override
    public User createUser(String username, String phoneNumber, String email, String hashedPassword) {
        return new Driver(username, phoneNumber, email, hashedPassword);
    }
}
