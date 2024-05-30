package factory;
import BookingSystem.User;
public interface UserFactory {
    User createUser(String username, String phoneNumber, String email, String hashedPassword);
}


