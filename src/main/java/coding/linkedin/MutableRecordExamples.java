package coding.linkedin;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Examples showing different ways to achieve mutability in Java records
 * 
 * Note: Record fields are implicitly final, so direct field mutability is not possible.
 * However, we can use mutable objects as record fields.
 */
public class MutableRecordExamples {
    
    // ========== 1. MUTABLE COLLECTIONS ==========
    
    /**
     * Using mutable collections - the reference is final, but the content can change
     */
    public record PersonWithHobbies(String name, List<String> hobbies) {
        // Custom constructor for defensive copying (optional)
        public PersonWithHobbies(String name, List<String> hobbies) {
            this.name = name;
            this.hobbies = new ArrayList<>(hobbies); // Defensive copy
        }
        
        // Convenience methods for mutation
        public void addHobby(String hobby) {
            hobbies.add(hobby);
        }
        
        public void removeHobby(String hobby) {
            hobbies.remove(hobby);
        }
    }
    
    /**
     * Using Map for mutable key-value pairs
     */
    public record Configuration(String appName, Map<String, String> settings) {
        public Configuration(String appName, Map<String, String> settings) {
            this.appName = appName;
            this.settings = new HashMap<>(settings); // Defensive copy
        }
        
        public void updateSetting(String key, String value) {
            settings.put(key, value);
        }
        
        public String getSetting(String key) {
            return settings.get(key);
        }
    }
    
    // ========== 2. ATOMIC CLASSES ==========
    
    /**
     * Using AtomicInteger for thread-safe mutable integers
     */
    public record Counter(String name, AtomicInteger count) {
        public Counter(String name, int initialCount) {
            this(name, new AtomicInteger(initialCount));
        }
        
        public void increment() {
            count.incrementAndGet();
        }
        
        public void decrement() {
            count.decrementAndGet();
        }
        
        public int getValue() {
            return count.get();
        }
    }
    
    /**
     * Using AtomicReference for thread-safe mutable objects
     */
    public record MutablePerson(String id, AtomicReference<String> name, AtomicReference<Integer> age) {
        public MutablePerson(String id, String name, int age) {
            this(id, new AtomicReference<>(name), new AtomicReference<>(age));
        }
        
        public void setName(String newName) {
            name.set(newName);
        }
        
        public void setAge(int newAge) {
            age.set(newAge);
        }
        
        public String getName() {
            return name.get();
        }
        
        public int getAge() {
            return age.get();
        }
    }
    
    // ========== 3. WRAPPER CLASSES ==========
    
    /**
     * Custom wrapper class for mutable values
     */
    public static class MutableValue<T> {
        private T value;
        
        public MutableValue(T value) {
            this.value = value;
        }
        
        public T get() {
            return value;
        }
        
        public void set(T value) {
            this.value = value;
        }
        
        @Override
        public String toString() {
            return "MutableValue{" + value + "}";
        }
    }
    
    /**
     * Record using wrapper class
     */
    public record Account(String accountId, MutableValue<Double> balance) {
        public Account(String accountId, double balance) {
            this(accountId, new MutableValue<>(balance));
        }
        
        public void deposit(double amount) {
            balance.set(balance.get() + amount);
        }
        
        public void withdraw(double amount) {
            balance.set(balance.get() - amount);
        }
        
        public double getBalance() {
            return balance.get();
        }
    }
    
    // ========== 4. ARRAYS ==========
    
    /**
     * Using arrays - array reference is final, but elements can be modified
     */
    public record GameBoard(String gameName, int[][] board) {
        public GameBoard(String gameName, int rows, int cols) {
            this(gameName, new int[rows][cols]);
        }
        
        public void setCell(int row, int col, int value) {
            board[row][col] = value;
        }
        
        public int getCell(int row, int col) {
            return board[row][col];
        }
        
        public void printBoard() {
            System.out.println("Game: " + gameName);
            for (int[] row : board) {
                System.out.println(Arrays.toString(row));
            }
        }
    }
    
    // ========== 5. BUILDER PATTERN WITH RECORDS ==========
    
    /**
     * Using Builder pattern to create immutable records with complex initialization
     */
    public record ImmutablePerson(String firstName, String lastName, int age, List<String> hobbies) {
        // Defensive copy in constructor
        public ImmutablePerson(String firstName, String lastName, int age, List<String> hobbies) {
            this.firstName = firstName;
            this.lastName = lastName;
            this.age = age;
            this.hobbies = hobbies != null ? List.copyOf(hobbies) : List.of();
        }
        
        // Builder for easier construction
        public static class Builder {
            private String firstName;
            private String lastName;
            private int age;
            private List<String> hobbies = new ArrayList<>();
            
            public Builder firstName(String firstName) {
                this.firstName = firstName;
                return this;
            }
            
            public Builder lastName(String lastName) {
                this.lastName = lastName;
                return this;
            }
            
            public Builder age(int age) {
                this.age = age;
                return this;
            }
            
            public Builder addHobby(String hobby) {
                this.hobbies.add(hobby);
                return this;
            }
            
            public ImmutablePerson build() {
                return new ImmutablePerson(firstName, lastName, age, hobbies);
            }
        }
        
        public static Builder builder() {
            return new Builder();
        }
    }
    
    // ========== 6. WHAT NOT TO DO ==========
    
    /**
     * This will NOT work - compilation error!
     */
    /*
    public record InvalidRecord(String name, int age) {
        // This would cause compilation error - cannot reassign final field
        public void setAge(int newAge) {
            this.age = newAge; // ERROR: Cannot assign a value to final variable age
        }
    }
    */
    
    // ========== MAIN METHOD WITH EXAMPLES ==========
    
    public static void main(String[] args) {
        System.out.println("=== Java Record Mutability Examples ===\n");
        
        // 1. Mutable Collections
        System.out.println("1. Mutable Collections:");
        PersonWithHobbies person = new PersonWithHobbies("Alice", List.of("reading", "coding"));
        System.out.println("Initial: " + person);
        person.addHobby("swimming");
        System.out.println("After adding hobby: " + person);
        
        Configuration config = new Configuration("MyApp", Map.of("debug", "true"));
        System.out.println("Initial config: " + config);
        config.updateSetting("version", "1.0.0");
        System.out.println("After update: " + config);
        
        // 2. Atomic Classes
        System.out.println("\n2. Atomic Classes:");
        Counter counter = new Counter("PageViews", 0);
        System.out.println("Initial: " + counter.name() + " = " + counter.getValue());
        counter.increment();
        counter.increment();
        System.out.println("After increments: " + counter.name() + " = " + counter.getValue());
        
        MutablePerson mutablePerson = new MutablePerson("001", "John", 25);
        System.out.println("Initial: " + mutablePerson.getName() + ", age " + mutablePerson.getAge());
        mutablePerson.setName("Johnny");
        mutablePerson.setAge(26);
        System.out.println("After updates: " + mutablePerson.getName() + ", age " + mutablePerson.getAge());
        
        // 3. Wrapper Classes
        System.out.println("\n3. Wrapper Classes:");
        Account account = new Account("ACC001", 1000.0);
        System.out.println("Initial balance: $" + account.getBalance());
        account.deposit(500.0);
        System.out.println("After deposit: $" + account.getBalance());
        account.withdraw(200.0);
        System.out.println("After withdrawal: $" + account.getBalance());
        
        // 4. Arrays
        System.out.println("\n4. Arrays:");
        GameBoard ticTacToe = new GameBoard("Tic-Tac-Toe", 3, 3);
        ticTacToe.setCell(0, 0, 1);
        ticTacToe.setCell(1, 1, 2);
        ticTacToe.setCell(2, 2, 1);
        ticTacToe.printBoard();
        
        // 5. Builder Pattern
        System.out.println("\n5. Builder Pattern:");
        ImmutablePerson builtPerson = ImmutablePerson.builder()
            .firstName("Jane")
            .lastName("Smith")
            .age(30)
            .addHobby("photography")
            .addHobby("traveling")
            .build();
        System.out.println("Built person: " + builtPerson);
        
        // Demonstrating immutability
        System.out.println("\n=== Immutability Demonstration ===");
        System.out.println("Trying to modify the hobbies list directly:");
        try {
            builtPerson.hobbies().add("hacking"); // This will throw UnsupportedOperationException
        } catch (UnsupportedOperationException e) {
            System.out.println("Cannot modify - list is immutable: " + e.getMessage());
        }
    }
} 