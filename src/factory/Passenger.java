package factory;
import BookingSystem.*;

//Concrete Passenger class extending User
public class Passenger extends User {
    public Passenger(String username, String phoneNumber, String email, String hashedPassword) {
        super(username, phoneNumber, email, hashedPassword);
    }

}
