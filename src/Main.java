import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

public class Main {
    private static final String DATA_FILE = "agency.dat";
    private static final Scanner in = new Scanner(System.in);

    public static void main(String[] args) {
        RentalAgency agency = loadOrCreate();
        System.out.println("Welcome to the Car Rental System");

        while (true) {
            printMenu();
            String choice = prompt("Choose");
            try {
                switch (choice) {
                    case "1":  addCar(agency); break;
                    case "2":  agency.removeCar(prompt("Registration no")); System.out.println("Car removed."); break;
                    case "3":  show(agency.getCars(), "No cars."); break;
                    case "4":  show(agency.getAvailableCars(), "No cars available."); break;
                    case "5":  addCustomer(agency); break;
                    case "6":  show(agency.getCustomers(), "No customers."); break;
                    case "7":  rentCar(agency); break;
                    case "8":  returnCar(agency); break;
                    case "9":  showRentals(agency); break;
                    case "10": show(agency.getTransactions(), "No transactions yet."); break;
                    case "11": show(agency.getRentalsForCustomer(prompt("Driver's license")), "No rentals for that customer."); break;
                    case "12": report(agency); break;
                    case "0":
                        save(agency);
                        System.out.println("Data saved. Goodbye!");
                        return;
                    default:   System.out.println("Invalid option.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Error: please enter a valid number.");
            } catch (DateTimeParseException e) {
                System.out.println("Error: use the date format yyyy-mm-dd (e.g. 2026-12-01).");
            } catch (IllegalArgumentException | IllegalStateException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private static void printMenu() {
        System.out.println("\n========== CAR RENTAL SYSTEM ==========");
        System.out.println(" CARS       1 Add car     2 Remove car   3 List all   4 List available");
        System.out.println(" CUSTOMERS  5 Add customer  6 List customers");
        System.out.println(" RENTALS    7 Rent car    8 Return car  9 Active/overdue  10 History  11 By customer");
        System.out.println(" REPORTS    12 Revenue & stats");
        System.out.println(" 0 Save & exit");
    }

    private static void addCar(RentalAgency agency) {
        String make = prompt("Make");
        String model = prompt("Model");
        int year = Integer.parseInt(prompt("Year"));
        String reg = prompt("Registration no");
        double rate = Double.parseDouble(prompt("Daily rate ($)"));
        agency.addCar(new Car(make, model, year, reg, rate));
        System.out.println("Car added.");
    }

    private static void addCustomer(RentalAgency agency) {
        agency.addCustomer(new Customer(prompt("Name"), prompt("Driver's license"), prompt("Phone")));
        System.out.println("Customer added.");
    }

    private static void rentCar(RentalAgency agency) {
        String reg = prompt("Car registration no");
        String dl = prompt("Driver's license");
        int days = Integer.parseInt(prompt("Rental days"));
        RentalTransaction t = agency.rentCar(reg, dl, days);
        System.out.println("Success! " + t);
        if (days >= RentalTransaction.DISCOUNT_MIN_DAYS) {
            System.out.println("(A " + (int) (RentalTransaction.LONG_RENTAL_DISCOUNT * 100) + "% long-rental discount was applied.)");
        }
    }

    private static void returnCar(RentalAgency agency) {
        String reg = prompt("Car registration no");
        String d = prompt("Return date yyyy-mm-dd (Enter for today)");
        LocalDate date = d.isEmpty() ? LocalDate.now() : LocalDate.parse(d);
        System.out.println("Returned. " + agency.returnCar(reg, date));
    }

    private static void showRentals(RentalAgency agency) {
        System.out.println("--- Active rentals ---");
        show(agency.getActiveRentals(), "None.");
        System.out.println("--- Overdue ---");
        show(agency.getOverdueRentals(LocalDate.now()), "None.");
    }

    private static void report(RentalAgency agency) {
        System.out.println("Total cars:       " + agency.getCars().size());
        System.out.println("Available:        " + agency.getAvailableCars().size());
        System.out.println("Active rentals:   " + agency.getActiveRentals().size());
        System.out.println("Total customers:  " + agency.getCustomers().size());
        System.out.println("Total rentals:    " + agency.getTransactions().size());
        System.out.printf("Revenue (completed rentals): $%.2f%n", agency.getTotalRevenue());
    }

    private static <T> void show(List<T> items, String emptyMsg) {
        if (items.isEmpty()) System.out.println(emptyMsg);
        else items.forEach(System.out::println);
    }

    private static String prompt(String label) {
        System.out.print(label + ": ");
        return in.hasNextLine() ? in.nextLine().trim() : "0";
    }

    private static RentalAgency loadOrCreate() {
        File f = new File(DATA_FILE);
        if (f.exists()) {
            try {
                RentalAgency a = RentalAgency.load(DATA_FILE);
                System.out.println("Loaded saved data from " + DATA_FILE);
                return a;
            } catch (IOException | ClassNotFoundException e) {
                System.out.println("Could not read saved data (" + e.getMessage() + "). Starting fresh.");
            }
        }
        RentalAgency a = new RentalAgency();
        a.addCar(new Car("Toyota", "Corolla", 2020, "KDA 123A", 50));
        a.addCar(new Car("Nissan", "Note", 2018, "KDB 456B", 40));
        a.addCar(new Car("Subaru", "Forester", 2021, "KDC 789C", 80));
        a.addCustomer(new Customer("Chrispinus Langat", "DL-1001", "0700000000"));
        return a;
    }

    private static void save(RentalAgency agency) {
        try {
            agency.save(DATA_FILE);
        } catch (IOException e) {
            System.out.println("Warning: could not save data: " + e.getMessage());
        }
    }
}