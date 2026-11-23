import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class RentalAgencyTest {
    private RentalAgency agency;
    private final LocalDate start = LocalDate.of(2026, 1, 1);

    @BeforeEach
    void setUp() {
        agency = new RentalAgency();
        agency.addCar(new Car("Toyota", "Corolla", 2020, "KDA 123A", 50));
        agency.addCustomer(new Customer("Jane Doe", "DL-1", "0711"));
    }

    @Test
    void rentCarMarksUnavailableAndCalculatesCost() {
        RentalTransaction t = agency.rentCar("KDA 123A", "DL-1", 3, start);
        assertFalse(agency.findCar("KDA 123A").isAvailable());
        assertEquals(150.0, t.calculateRentalCost(), 0.001);
    }

    @Test
    void longRentalGetsDiscount() {
        RentalTransaction t = agency.rentCar("KDA 123A", "DL-1", 10, start);
        assertEquals(450.0, t.calculateRentalCost(), 0.001); // 500 - 10%
    }

    @Test
    void cannotRentSameCarTwice() {
        agency.rentCar("KDA 123A", "DL-1", 1, start);
        assertThrows(IllegalStateException.class, () -> agency.rentCar("KDA 123A", "DL-1", 1, start));
    }

    @Test
    void invalidInputsFail() {
        assertThrows(IllegalArgumentException.class, () -> agency.rentCar("NOPE", "DL-1", 1));
        assertThrows(IllegalArgumentException.class, () -> agency.rentCar("KDA 123A", "NOPE", 1));
        assertThrows(IllegalArgumentException.class, () -> agency.rentCar("KDA 123A", "DL-1", 0));
    }

    @Test
    void returnOnTimeHasNoLateFeeAndFreesCar() {
        agency.rentCar("KDA 123A", "DL-1", 3, start);
        RentalTransaction t = agency.returnCar("KDA 123A", start.plusDays(3));
        assertEquals(0.0, t.calculateLateFee(), 0.001);
        assertTrue(agency.findCar("KDA 123A").isAvailable());
        assertEquals(150.0, agency.getTotalRevenue(), 0.001);
    }

    @Test
    void lateReturnChargesLateFee() {
        agency.rentCar("KDA 123A", "DL-1", 3, start);
        RentalTransaction t = agency.returnCar("KDA 123A", start.plusDays(5)); // 2 days late
        assertEquals(2 * 50 * 1.5, t.calculateLateFee(), 0.001);
        assertEquals(300.0, t.getTotalCharge(), 0.001);
    }

    @Test
    void overdueRentalsAreDetected() {
        agency.rentCar("KDA 123A", "DL-1", 2, start);
        assertEquals(1, agency.getOverdueRentals(start.plusDays(5)).size());
        assertEquals(0, agency.getOverdueRentals(start.plusDays(1)).size());
    }

    @Test
    void returningACarThatIsNotRentedFails() {
        assertThrows(IllegalStateException.class, () -> agency.returnCar("KDA 123A", start));
    }

    @Test
    void cannotRemoveRentedCar() {
        agency.rentCar("KDA 123A", "DL-1", 1, start);
        assertThrows(IllegalStateException.class, () -> agency.removeCar("KDA 123A"));
    }

    @Test
    void duplicatesAreRejected() {
        assertThrows(IllegalArgumentException.class, () -> agency.addCar(new Car("A", "B", "KDA 123A")));
        assertThrows(IllegalArgumentException.class, () -> agency.addCustomer(new Customer("X", "DL-1")));
    }

    @Test
    void saveAndLoadRoundTrip(@TempDir Path dir) throws Exception {
        agency.rentCar("KDA 123A", "DL-1", 2, start);
        String file = dir.resolve("agency.dat").toString();
        agency.save(file);
        RentalAgency loaded = RentalAgency.load(file);
        assertEquals(1, loaded.getCars().size());
        assertEquals(1, loaded.getTransactions().size());
        assertFalse(loaded.findCar("KDA 123A").isAvailable());
    }
}