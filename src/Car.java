import java.io.Serializable;

public class Car implements Serializable {
    private static final long serialVersionUID = 1L;
    public static final double DEFAULT_DAILY_RATE = 50.0;

    private final String make;
    private final String model;
    private final int year;
    private final String registrationNumber;
    private final double dailyRate;
    private boolean available;

    public Car(String make, String model, int year, String registrationNumber, double dailyRate) {
        if (isBlank(make) || isBlank(model) || isBlank(registrationNumber)) {
            throw new IllegalArgumentException("Make, model and registration number are required.");
        }
        if (dailyRate <= 0) {
            throw new IllegalArgumentException("Daily rate must be greater than zero.");
        }
        this.make = make.trim();
        this.model = model.trim();
        this.year = year;
        this.registrationNumber = registrationNumber.trim();
        this.dailyRate = dailyRate;
        this.available = true;
    }

    public Car(String make, String model, String registrationNumber) {
        this(make, model, 0, registrationNumber, DEFAULT_DAILY_RATE);
    }

    private static boolean isBlank(String s) { return s == null || s.trim().isEmpty(); }

    public String getMake() { return make; }
    public String getModel() { return model; }
    public int getYear() { return year; }
    public String getRegistrationNumber() { return registrationNumber; }
    public double getDailyRate() { return dailyRate; }
    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }

    @Override
    public String toString() {
        return String.format("%-18s %-10s $%6.2f/day  %s",
                make + " " + model + (year > 0 ? " (" + year + ")" : ""),
                "[" + registrationNumber + "]", dailyRate,
                available ? "AVAILABLE" : "RENTED");
    }
}