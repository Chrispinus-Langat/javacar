import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class RentalAgency implements Serializable {
    private static final long serialVersionUID = 1L;

    private final List<Car> cars = new ArrayList<>();
    private final List<Customer> customers = new ArrayList<>();
    private final List<RentalTransaction> transactions = new ArrayList<>();
    private int nextTransactionId = 1;

    // ---------- Management ----------
    public void addCar(Car car) {
        if (findCar(car.getRegistrationNumber()) != null) {
            throw new IllegalArgumentException("Car already exists: " + car.getRegistrationNumber());
        }
        cars.add(car);
    }

    public void removeCar(String registrationNumber) {
        Car car = findCar(registrationNumber);
        if (car == null) throw new IllegalArgumentException("Car not found: " + registrationNumber);
        if (!car.isAvailable()) throw new IllegalStateException("Cannot remove a car that is currently rented.");
        cars.remove(car);
    }

    public void addCustomer(Customer customer) {
        if (findCustomer(customer.getDriversLicense()) != null) {
            throw new IllegalArgumentException("Customer already exists: " + customer.getDriversLicense());
        }
        customers.add(customer);
    }

    // ---------- Renting & returning ----------
    public RentalTransaction rentCar(String registrationNumber, String driversLicense, int rentalDays) {
        return rentCar(registrationNumber, driversLicense, rentalDays, LocalDate.now());
    }

    public RentalTransaction rentCar(String registrationNumber, String driversLicense,
                                     int rentalDays, LocalDate startDate) {
        if (rentalDays <= 0) throw new IllegalArgumentException("Rental days must be at least 1.");
        Car car = findCar(registrationNumber);
        if (car == null) throw new IllegalArgumentException("Car not found: " + registrationNumber);
        Customer customer = findCustomer(driversLicense);
        if (customer == null) throw new IllegalArgumentException("Customer not found: " + driversLicense);
        if (!car.isAvailable()) throw new IllegalStateException("Car is already rented: " + registrationNumber);

        car.setAvailable(false);
        RentalTransaction t = new RentalTransaction(nextTransactionId++, car, customer, startDate, rentalDays);
        transactions.add(t);
        return t;
    }

    public RentalTransaction returnCar(String registrationNumber) {
        return returnCar(registrationNumber, LocalDate.now());
    }

    public RentalTransaction returnCar(String registrationNumber, LocalDate returnDate) {
        Car car = findCar(registrationNumber);
        if (car == null) throw new IllegalArgumentException("Car not found: " + registrationNumber);
        for (RentalTransaction t : transactions) {
            if (t.isActive() && t.getCar().getRegistrationNumber().equalsIgnoreCase(registrationNumber)) {
                t.markReturned(returnDate);
                car.setAvailable(true);
                return t;
            }
        }
        throw new IllegalStateException("That car is not currently rented.");
    }

    // ---------- Lookups ----------
    public Car findCar(String registrationNumber) {
        for (Car c : cars) {
            if (c.getRegistrationNumber().equalsIgnoreCase(registrationNumber.trim())) return c;
        }
        return null;
    }

    public Customer findCustomer(String driversLicense) {
        for (Customer c : customers) {
            if (c.getDriversLicense().equalsIgnoreCase(driversLicense.trim())) return c;
        }
        return null;
    }

    public List<Car> getCars() { return Collections.unmodifiableList(cars); }
    public List<Customer> getCustomers() { return Collections.unmodifiableList(customers); }
    public List<RentalTransaction> getTransactions() { return Collections.unmodifiableList(transactions); }

    public List<Car> getAvailableCars() {
        return cars.stream().filter(Car::isAvailable).collect(Collectors.toList());
    }

    public List<RentalTransaction> getActiveRentals() {
        return transactions.stream().filter(RentalTransaction::isActive).collect(Collectors.toList());
    }

    public List<RentalTransaction> getOverdueRentals(LocalDate today) {
        return transactions.stream().filter(t -> t.isOverdue(today)).collect(Collectors.toList());
    }

    public List<RentalTransaction> getRentalsForCustomer(String driversLicense) {
        return transactions.stream()
                .filter(t -> t.getCustomer().getDriversLicense().equalsIgnoreCase(driversLicense.trim()))
                .collect(Collectors.toList());
    }

    /** Revenue from completed (returned) rentals, including late fees. */
    public double getTotalRevenue() {
        return transactions.stream().filter(t -> !t.isActive())
                .mapToDouble(RentalTransaction::getTotalCharge).sum();
    }

    // ---------- Persistence ----------
    public void save(String filePath) throws IOException {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filePath))) {
            out.writeObject(this);
        }
    }

    public static RentalAgency load(String filePath) throws IOException, ClassNotFoundException {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(filePath))) {
            return (RentalAgency) in.readObject();
        }
    }
}