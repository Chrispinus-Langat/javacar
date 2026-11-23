import java.io.Serializable;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class RentalTransaction implements Serializable {
    private static final long serialVersionUID = 1L;

    /** Rentals of this many days or more get a discount. */
    public static final int DISCOUNT_MIN_DAYS = 7;
    public static final double LONG_RENTAL_DISCOUNT = 0.10;
    /** Each late day is charged at this multiple of the daily rate. */
    public static final double LATE_FEE_MULTIPLIER = 1.5;

    private final int id;
    private final Car car;
    private final Customer customer;
    private final LocalDate startDate;
    private final int rentalDays;
    private LocalDate returnDate; // null while the rental is active

    public RentalTransaction(int id, Car car, Customer customer, LocalDate startDate, int rentalDays) {
        this.id = id;
        this.car = car;
        this.customer = customer;
        this.startDate = startDate;
        this.rentalDays = rentalDays;
    }

    public int getId() { return id; }
    public Car getCar() { return car; }
    public Customer getCustomer() { return customer; }
    public LocalDate getStartDate() { return startDate; }
    public int getRentalDays() { return rentalDays; }
    public LocalDate getReturnDate() { return returnDate; }
    public LocalDate getDueDate() { return startDate.plusDays(rentalDays); }
    public boolean isActive() { return returnDate == null; }

    public boolean isOverdue(LocalDate today) {
        return isActive() && today.isAfter(getDueDate());
    }

    /** Base cost for the agreed days, with a discount for long rentals. */
    public double calculateRentalCost() {
        double cost = rentalDays * car.getDailyRate();
        if (rentalDays >= DISCOUNT_MIN_DAYS) {
            cost *= (1 - LONG_RENTAL_DISCOUNT);
        }
        return cost;
    }

    /** Late fee, based on the return date (0 if not yet returned or returned on time). */
    public double calculateLateFee() {
        if (returnDate == null || !returnDate.isAfter(getDueDate())) return 0.0;
        long daysLate = ChronoUnit.DAYS.between(getDueDate(), returnDate);
        return daysLate * car.getDailyRate() * LATE_FEE_MULTIPLIER;
    }

    public double getTotalCharge() {
        return calculateRentalCost() + calculateLateFee();
    }

    public void markReturned(LocalDate date) {
        if (date.isBefore(startDate)) {
            throw new IllegalArgumentException("Return date cannot be before the start date.");
        }
        this.returnDate = date;
    }

    @Override
    public String toString() {
        String status = isActive() ? "ACTIVE (due " + getDueDate() + ")"
                : "RETURNED " + returnDate + (calculateLateFee() > 0
                    ? String.format(" (late fee $%.2f)", calculateLateFee()) : "");
        return String.format("#%d | %s %s [%s] -> %s | %s, %d day(s) | $%.2f | %s",
                id, car.getMake(), car.getModel(), car.getRegistrationNumber(),
                customer.getName(), startDate, rentalDays, getTotalCharge(), status);
    }
}