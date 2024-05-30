package strategy;
import BookingSystem.*;

public class BankOfBhutanPayment implements PaymentStrategy {
    private String accountNumber;
    private String bankCode;

    public BankOfBhutanPayment(String accountNumber, String bankCode) {
        this.accountNumber = accountNumber;
        this.bankCode = bankCode;
    }

    @Override
    public boolean pay(double amount) {
        // Implement Bank of Bhutan payment logic here
        System.out.println("Paid " + amount + " using Bank of Bhutan account " + accountNumber);
        // Simulate payment processing
        return true;
    }
}
