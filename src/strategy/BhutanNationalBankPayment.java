package strategy;
import BookingSystem.*;

public class BhutanNationalBankPayment implements PaymentStrategy {
    private String accountNumber;
    private String bankCode;

    public BhutanNationalBankPayment(String accountNumber, String bankCode) {
        this.accountNumber = accountNumber;
        this.bankCode = bankCode;
    }

    @Override
    public boolean pay(double amount) {
        // Implement Bhutan National Bank payment logic here
        System.out.println("Paid " + amount + " using Bhutan National Bank account " + accountNumber);
        // Simulate payment processing
        return true;
    }
}
