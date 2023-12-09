import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

class Vehicle {
    private String vehicleID;
    private String type;
    private String brand;
    private String model;
    private double rentalPrice;

    public Vehicle(String vehicleID, String type, String brand, String model, double rentalPrice) {
        this.vehicleID = vehicleID;
        this.type = type;
        this.brand = brand;
        this.model = model;
        this.rentalPrice = rentalPrice;
    }

    public String getVehicleID() {
        return vehicleID;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public double getRentalPrice() {
        return rentalPrice;
    }

    public void setRentalPrice(double rentalPrice) {
        this.rentalPrice = rentalPrice;
    }

    @Override
    public String toString() {
        return "Vehicle ID: " + vehicleID + ", Type: " + type + ", Brand: " + brand + ", Model: " + model +
                ", Rental Price: $" + rentalPrice;
    }
}

class Customer {
    private String name;
    private String contactInfo;
    private String licenseNumber;

    public Customer(String name, String contactInfo, String licenseNumber) {
        this.name = name;
        this.contactInfo = contactInfo;
        this.licenseNumber = licenseNumber;
    }

    public String getLicenseNumber() {
        return licenseNumber;
    }
    public String getName() {
        return name;
    }

    public String getContactInfo() {
        return contactInfo;
    }

    @Override
    public String toString() {
        return "Name: " + name + ", Contact Info: " + contactInfo + ", License Number: " + licenseNumber;
    }
}

class Rental {
    private Vehicle vehicle;
    private Customer customer;
    private Date startDate;
    private Date endDate;
    private double totalCost;

    public Rental(Vehicle vehicle, Customer customer, Date startDate, Date endDate, double totalCost) {
        this.vehicle = vehicle;
        this.customer = customer;
        this.startDate = startDate;
        this.endDate = endDate;
        this.totalCost = totalCost;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public Customer getCustomer() {
        return customer;
    }

    public Date getStartDate() {
        return startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public double getTotalCost() {
        return totalCost;
    }

    @Override
    public String toString() {
        return "Rental - Vehicle: " + vehicle.getVehicleID() + ", Customer: " + customer.getName() +
                ", Start Date: " + startDate + ", End Date: " + endDate + ", Total Cost: $" + totalCost;
    }
}

@SuppressWarnings("ALL")
class VehicleRentalSystem {
    private List<Vehicle> vehicles = new ArrayList<>();
    private List<Customer> customers = new ArrayList<>();
    private List<Rental> rentals = new ArrayList<>();

    private static final String VEHICLES_FILE = "vehicles.txt";
    private static final String CUSTOMERS_FILE = "customers.txt";
    private static final String RENTALS_FILE = "rentals.txt";

    public VehicleRentalSystem() {
        loadVehicles();
        loadCustomers();
        loadRentals();
    }

    public void addVehicle(Vehicle vehicle) {
        vehicles.add(vehicle);
        saveVehicles();
    }

    public void updateVehicleDetails(String vehicleID, String brand, String model, double rentalPrice) {
        for (Vehicle vehicle : vehicles) {
            if (vehicle.getVehicleID().equals(vehicleID)) {
                vehicle.setBrand(brand);
                vehicle.setModel(model);
                vehicle.setRentalPrice(rentalPrice);
                System.out.println("Vehicle details updated successfully.");
                saveVehicles();
                return;
            }
        }
        System.out.println("Vehicle not found.");
    }

    public void deleteVehicle(String vehicleID) {
        vehicles.removeIf(vehicle -> vehicle.getVehicleID().equals(vehicleID));
        System.out.println("Vehicle deleted successfully.");
        saveVehicles();
    }

    public void rentVehicle(String customerName, String vehicleID, String startDateStr, String endDateStr) {
        Vehicle vehicle = findVehicleById(vehicleID);
        Customer customer = findCustomerByName(customerName);

        if (vehicle != null && customer != null) {
            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                Date startDate = dateFormat.parse(startDateStr);
                Date endDate = dateFormat.parse(endDateStr);

                double totalCost = calculateRentalCost(vehicle, startDate, endDate);
                Rental rental = new Rental(vehicle, customer, startDate, endDate, totalCost);
                rentals.add(rental);
                System.out.println("Rental successful. Total cost: $" + totalCost);
                saveRentals();
            } catch (ParseException e) {
                System.out.println("Invalid date format. Use yyyy-MM-dd.");
            }
        } else {
            System.out.println("Vehicle or customer not found.");
        }
    }

    public void returnVehicle(String vehicleID) {
        Rental rental = findRentalByVehicleId(vehicleID);
        if (rental != null) {
            System.out.println("Vehicle returned. Total cost: $" + rental.getTotalCost());
            rentals.remove(rental);
            saveRentals();
        } else {
            System.out.println("Rental not found for the specified vehicle.");
        }
    }

    public void registerCustomer(String name, String contactInfo, String licenseNumber) {
        Customer customer = new Customer(name, contactInfo, licenseNumber);
        customers.add(customer);
        System.out.println("Customer registered successfully.");
        saveCustomers();
    }

    public void viewRentalHistory(String vehicleID) {
        for (Rental rental : rentals) {
            if (rental.getVehicle().getVehicleID().equals(vehicleID)) {
                System.out.println(rental);
            }
        }
    }

    public void viewCustomerHistory(String customerName) {
        for (Rental rental : rentals) {
            if (rental.getCustomer().getName().equals(customerName)) {
                System.out.println(rental);
            }
        }
    }

    private double calculateRentalCost(Vehicle vehicle, Date startDate, Date endDate) {
        long diffInMillies = Math.abs(endDate.getTime() - startDate.getTime());
        long diffInDays = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
        return diffInDays * vehicle.getRentalPrice();
    }

    private Vehicle findVehicleById(String vehicleID) {
        for (Vehicle vehicle : vehicles) {
            if (vehicle.getVehicleID().equals(vehicleID)) {
                return vehicle;
            }
        }
        return null;
    }

    private Customer findCustomerByName(String customerName) {
        for (Customer customer : customers) {
            if (customer.getName().equals(customerName)) {
                return customer;
            }
        }
        return null;
    }

    private Rental findRentalByVehicleId(String vehicleID) {
        for (Rental rental : rentals) {
            if (rental.getVehicle().getVehicleID().equals(vehicleID)) {
                return rental;
            }
        }
        return null;
    }

    private void saveVehicles() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(VEHICLES_FILE))) {
            for (Vehicle vehicle : vehicles) {
                writer.println(vehicle.getVehicleID() + "," + vehicle.getBrand() + "," + vehicle.getModel() + "," + vehicle.getRentalPrice());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadVehicles() {
        try (Scanner scanner = new Scanner(new File(VEHICLES_FILE))) {
            while (scanner.hasNextLine()) {
                String[] parts = scanner.nextLine().split(",");
                if (parts.length == 4) {
                    Vehicle vehicle = new Vehicle(parts[0], "unknown", parts[1], parts[2], Double.parseDouble(parts[3]));
                    vehicles.add(vehicle);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void saveCustomers() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(CUSTOMERS_FILE))) {
            for (Customer customer : customers) {
                writer.println(customer.getName() + "," + customer.getContactInfo() + "," + customer.getLicenseNumber());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadCustomers() {
        try (Scanner scanner = new Scanner(new File(CUSTOMERS_FILE))) {
            while (scanner.hasNextLine()) {
                String[] parts = scanner.nextLine().split(",");
                if (parts.length == 3) {
                    Customer customer = new Customer(parts[0], parts[1], parts[2]);
                    customers.add(customer);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void saveRentals() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(RENTALS_FILE))) {
            for (Rental rental : rentals) {
                writer.println(rental.getVehicle().getVehicleID() + "," +
                        rental.getCustomer().getName() + "," +
                        rental.getStartDate().getTime() + "," +
                        rental.getEndDate().getTime() + "," +
                        rental.getTotalCost());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadRentals() {
        try (Scanner scanner = new Scanner(new File(RENTALS_FILE))) {
            while (scanner.hasNextLine()) {
                String[] parts = scanner.nextLine().split(",");
                if (parts.length == 5) {
                    Vehicle vehicle = findVehicleById(parts[0]);
                    Customer customer = findCustomerByName(parts[1]);
                    if (vehicle != null && customer != null) {
                        Date startDate = new Date(Long.parseLong(parts[2]));
                        Date endDate = new Date(Long.parseLong(parts[3]));
                        double totalCost = Double.parseDouble(parts[4]);
                        Rental rental = new Rental(vehicle, customer, startDate, endDate, totalCost);
                        rentals.add(rental);
                    }
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new VehicleRentalSystem();
    }
}