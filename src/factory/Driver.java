package factory;
import BookingSystem.User;

//Concrete Driver class extending User
public class Driver extends User {
    public Driver(String username, String phoneNumber, String email, String hashedPassword) {
        super(username, phoneNumber, email, hashedPassword);
    }

    // Specific methods for Driver can be added here
}
