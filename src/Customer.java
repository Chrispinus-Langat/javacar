import java.io.Serializable;

public class Customer implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String name;
    private final String driversLicense;
    private final String phone;

    public Customer(String name, String driversLicense, String phone) {
        if (name == null || name.trim().isEmpty() || driversLicense == null || driversLicense.trim().isEmpty()) {
            throw new IllegalArgumentException("Name and driver's license are required.");
        }
        this.name = name.trim();
        this.driversLicense = driversLicense.trim();
        this.phone = phone == null ? "" : phone.trim();
    }

    public Customer(String name, String driversLicense) {
        this(name, driversLicense, "");
    }

    public String getName() { return name; }
    public String getDriversLicense() { return driversLicense; }
    public String getPhone() { return phone; }

    @Override
    public String toString() {
        return name + " (License: " + driversLicense + (phone.isEmpty() ? "" : ", Phone: " + phone) + ")";
    }
}