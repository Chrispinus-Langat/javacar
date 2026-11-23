# Car Rental System (Java)
**Chrispinus Langat – BSE-05-0246/2023**

A console-based car rental system in Java 8+.

## Features
- **Cars**: add, remove (only if not rented), list all / available only, per-car daily rate
- **Customers**: add and list, with phone number
- **Rentals**: rent with start date and due date, **return** cars, late-fee calculation
- **Pricing**: 10% discount for rentals of 7+ days; late days charged at 1.5x the daily rate
- **Reports**: active & overdue rentals, history per customer, revenue and stats
- **Persistence**: data is saved to `agency.dat` on exit and reloaded on the next start
- **Validation**: duplicate cars/customers, unknown IDs, double-renting, bad numbers/dates

## Classes
| Class | Role |
|---|---|
| `Car` | make, model, year, registration, daily rate, availability |
| `Customer` | name, driver's license, phone |
| `RentalTransaction` | id, car, customer, dates, `calculateRentalCost`, `calculateLateFee`, `getTotalCharge` |
| `RentalAgency` | `addCar`, `removeCar`, `addCustomer`, `rentCar`, `returnCar`, `findCar`, `findCustomer`, `getTransactions`, reports, `save`/`load` |
| `Main` | interactive menu |

## Run
```bash
mkdir out
javac -d out src/*.java
java -cp out Main
```

## Tests (JUnit 5)
Add JUnit 5 to your IDE project (or Maven/Gradle `junit-jupiter`) and run `test/RentalAgencyTest.java`.

## Usage
1. Add cars and customers (menu 1 and 5)
2. Rent: registration number, driver's license, days (menu 7)
3. Return: registration number and optional return date (menu 8)
4. View active/overdue rentals, history and revenue (menus 9–12)