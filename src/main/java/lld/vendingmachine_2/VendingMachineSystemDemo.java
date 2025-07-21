package lld.vendingmachine_2;

import java.util.*;

// Item class to represent products in the vending machine
class Item {
    private String name;
    private double price;
    private int quantity;

    public Item(String name, double price, int quantity) {
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }

    public String getName() { return name; }
    public double getPrice() { return price; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
}

// Coin class to represent different denominations
class Coin {
    private double value;
    private int quantity;

    public Coin(double value, int quantity) {
        this.value = value;
        this.quantity = quantity;
    }

    public double getValue() { return value; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
}

// VendingMachine class to handle the core functionality
class VendingMachine {
    private List<Item> items;
    private List<Coin> coins;
    private double currentBalance;
    private Item selectedItem;

    public VendingMachine() {
        items = new ArrayList<>();
        coins = new ArrayList<>();
        currentBalance = 0.0;
        initializeItems();
        initializeCoins();
    }

    private void initializeItems() {
        items.add(new Item("Coke", 1.25, 10));
        items.add(new Item("Pepsi", 1.25, 10));
        items.add(new Item("Chips", 0.75, 15));
        items.add(new Item("Candy", 0.50, 20));
    }

    private void initializeCoins() {
        coins.add(new Coin(0.25, 20)); // Quarters
        coins.add(new Coin(0.10, 20)); // Dimes
        coins.add(new Coin(0.05, 20)); // Nickels
        coins.add(new Coin(0.01, 20)); // Pennies
    }

    public void displayItems() {
        System.out.println("\nAvailable Items:");
        for (int i = 0; i < items.size(); i++) {
            Item item = items.get(i);
            System.out.printf("%d. %s - $%.2f (Quantity: %d)%n", 
                i + 1, item.getName(), item.getPrice(), item.getQuantity());
        }
    }

    public boolean selectItem(int itemNumber) {
        if (itemNumber < 1 || itemNumber > items.size()) {
            System.out.println("Invalid item selection!");
            return false;
        }

        selectedItem = items.get(itemNumber - 1);
        if (selectedItem.getQuantity() <= 0) {
            System.out.println("Item is out of stock!");
            return false;
        }

        System.out.printf("Selected: %s - $%.2f%n", 
            selectedItem.getName(), selectedItem.getPrice());
        return true;
    }

    public void insertCoin(double amount) {
        currentBalance += amount;
        System.out.printf("Current balance: $%.2f%n", currentBalance);
    }

    public boolean makePurchase() {
        if (selectedItem == null) {
            System.out.println("Please select an item first!");
            return false;
        }

        if (currentBalance < selectedItem.getPrice()) {
            System.out.println("Insufficient balance!");
            return false;
        }

        // Process the purchase
        selectedItem.setQuantity(selectedItem.getQuantity() - 1);
        double change = currentBalance - selectedItem.getPrice();
        currentBalance = 0;
        
        System.out.printf("Dispensing %s...%n", selectedItem.getName());
        if (change > 0) {
            System.out.printf("Returning change: $%.2f%n", change);
            returnChange(change);
        }
        
        selectedItem = null;
        return true;
    }

    private void returnChange(double amount) {
        // Simple change return logic
        System.out.println("Returning change in coins:");
        for (Coin coin : coins) {
            while (amount >= coin.getValue() && coin.getQuantity() > 0) {
                amount -= coin.getValue();
                coin.setQuantity(coin.getQuantity() - 1);
                System.out.printf("$%.2f coin%n", coin.getValue());
            }
        }
    }

    public void cancelTransaction() {
        if (currentBalance > 0) {
            System.out.printf("Returning balance: $%.2f%n", currentBalance);
            returnChange(currentBalance);
            currentBalance = 0;
        }
        selectedItem = null;
    }
}

public class VendingMachineSystemDemo {
    public static void main(String[] args) {
        VendingMachine vendingMachine = new VendingMachine();
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\n=== Vending Machine ===");
            System.out.println("1. Display Items");
            System.out.println("2. Select Item");
            System.out.println("3. Insert Coin");
            System.out.println("4. Make Purchase");
            System.out.println("5. Cancel Transaction");
            System.out.println("6. Exit");
            System.out.print("Enter your choice: ");

            int choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    vendingMachine.displayItems();
                    break;
                case 2:
                    System.out.print("Enter item number: ");
                    int itemNumber = scanner.nextInt();
                    vendingMachine.selectItem(itemNumber);
                    break;
                case 3:
                    System.out.print("Enter coin amount (e.g., 0.25 for quarter): ");
                    double amount = scanner.nextDouble();
                    vendingMachine.insertCoin(amount);
                    break;
                case 4:
                    vendingMachine.makePurchase();
                    break;
                case 5:
                    vendingMachine.cancelTransaction();
                    break;
                case 6:
                    System.out.println("Thank you for using the vending machine!");
                    scanner.close();
                    return;
                default:
                    System.out.println("Invalid choice!");
            }
        }
    }
}
