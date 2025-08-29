package concepts;

import java.util.Objects;

/**
 * This file demonstrates the easiest and most common ways to implement equals() and hashCode() in Java.
 */
public class EqualsAndHashCode {

    // --- 1. The Easiest Way: Java Records (Java 14+) ---
    // The compiler automatically generates a canonical constructor, public accessors,
    // and correct implementations of equals(), hashCode(), and toString().
    public record UserRecord(long id, String username) {}

    // --- 2. The Modern Way for Classes: java.util.Objects Helpers (Java 7+) ---
    // Use this for mutable classes or when you can't use records.
    public static class UserWithHelpers {
        private final long id;
        private final String username;

        public UserWithHelpers(long id, String username) {
            this.id = id;
            this.username = username;
        }

        @Override
        public boolean equals(Object o) {
            // 1. Reference equality check (optimization)
            if (this == o) return true;
            // 2. Null and class type check
            if (o == null || getClass() != o.getClass()) return false;
            // 3. Cast and compare fields
            UserWithHelpers that = (UserWithHelpers) o;
            return id == that.id && Objects.equals(username, that.username);
        }

        @Override
        public int hashCode() {
            // The Objects.hash() method handles nulls and combines hash codes correctly.
            return Objects.hash(id, username);
        }
    }

    // --- 3. The IDE-Generated Way ---
    // This is what an IDE like IntelliJ or Eclipse would typically generate.
    // It's robust and follows best practices, similar to the `Objects` helper approach.
    public static class UserWithIDE {
        private final long id;
        private final String username;

        public UserWithIDE(long id, String username) {
            this.id = id;
            this.username = username;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            UserWithIDE that = (UserWithIDE) o;

            if (id != that.id) return false;
            return Objects.equals(username, that.username);
        }

        @Override
        public int hashCode() {
            int result = (int) (id ^ (id >>> 32));
            result = 31 * result + (username != null ? username.hashCode() : 0);
            return result;
        }
    }

    public static void main(String[] args) {
        System.out.println("--- 1. Testing Records ---");
        UserRecord r1 = new UserRecord(1L, "alice");
        UserRecord r2 = new UserRecord(1L, "alice");
        UserRecord r3 = new UserRecord(2L, "bob");
        System.out.println("r1.equals(r2): " + r1.equals(r2)); // true
        System.out.println("r1.equals(r3): " + r1.equals(r3)); // false
        System.out.println("r1.hashCode() == r2.hashCode(): " + (r1.hashCode() == r2.hashCode())); // true

        System.out.println("\n--- 2. Testing Class with Objects Helpers ---");
        UserWithHelpers u1 = new UserWithHelpers(1L, "alice");
        UserWithHelpers u2 = new UserWithHelpers(1L, "alice");
        UserWithHelpers u3 = new UserWithHelpers(2L, "bob");
        System.out.println("u1.equals(u2): " + u1.equals(u2)); // true
        System.out.println("u1.equals(u3): " + u1.equals(u3)); // false
        System.out.println("u1.hashCode() == u2.hashCode(): " + (u1.hashCode() == u2.hashCode())); // true
    }
}
