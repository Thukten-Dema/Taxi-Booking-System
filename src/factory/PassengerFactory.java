package factory;

import BookingSystem.User;

public class PassengerFactory implements UserFactory {
    @Override
    public User createUser(String username, String phoneNumber, String email, String hashedPassword) {
        return new Passenger(username, phoneNumber, email, hashedPassword);
    }
}
        