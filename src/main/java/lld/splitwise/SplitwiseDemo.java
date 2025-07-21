package lld.splitwise;

import lombok.Data;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Core Splitwise model – keeps net balances in O(1) per operation.
 *  • User               – participant
 *  • Split (Equal/Exact/Percent)
 *  • Expense            – records payment & splits
 *  • ExpenseManager     – high-level APIs: addExpense(), showBalances()
 */
public class SplitwiseDemo {
    public static void main(String[] args) {
        ExpenseManager manager = new ExpenseManager();

        // Create users
        User u1 = manager.addUser("u1", "User1", "u1@email.com");
        User u2 = manager.addUser("u2", "User2", "u2@email.com");
        User u3 = manager.addUser("u3", "User3", "u3@email.com");

        // Create a group
        Group group = manager.createGroup("Trip", "Weekend Trip", Arrays.asList(u1, u2, u3));

        // 1. Equal split – u1 pays 300 for u1, u2, u3 (100 each)
        manager.addExpenseEqual(u1, new BigDecimal("300.00"), Arrays.asList(u1, u2, u3), "Lunch", group.getId());

        // 2. Exact split – u2 pays 125, owes: u1 40, u2 45, u3 40
        manager.addExpenseExact(u2, new BigDecimal("125.00"),
                Arrays.asList(
                        new ExactSplit(u1, new BigDecimal("40.00")),
                        new ExactSplit(u2, new BigDecimal("45.00")),
                        new ExactSplit(u3, new BigDecimal("40.00"))
                ), "Cab", group.getId());

        // 3. Percent split – u3 pays 120, split 40%, 20%, 40%
        manager.addExpensePercent(u3, new BigDecimal("120.00"),
                Arrays.asList(
                        new PercentSplit(u1, new BigDecimal("40.00")),
                        new PercentSplit(u2, new BigDecimal("20.00")),
                        new PercentSplit(u3, new BigDecimal("40.00"))
                ), "Dinner", group.getId());

        System.out.println("\n---- Individual Balances ----");
        manager.showBalance(u1);
        manager.showBalance(u2);
        manager.showBalance(u3);

        System.out.println("\n---- All Balances ----");
        manager.showBalances();

        System.out.println("\n---- Group Expenses ----");
        manager.showGroupExpenses(group.getId());

        System.out.println("\n---- Simplified Settlements ----");
        manager.showSimplifiedDebts();
    }
}


// ==================== MODEL ====================
@Data
class User {
    private final String id;
    private final String name;
    private final String email;

    public User(String id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }
}

@Data
class Group {
    private final String id;
    private final String name;
    private final Set<String> memberIds;

    public Group(String id, String name, List<User> members) {
        this.id = id;
        this.name = name;
        this.memberIds = members.stream()
                .map(User::getId)
                .collect(Collectors.toSet());
    }

    @Override public String toString() { return name; }
}

abstract class Split {
    private final User user;
    private BigDecimal amount; // computed for Equal / Percent

    protected Split(User user) { 
        this.user = user; 
        this.amount = BigDecimal.ZERO;
    }
    
    public User getUser() { return user; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
}

class EqualSplit extends Split {
    public EqualSplit(User user) { super(user); }
}

class ExactSplit extends Split {
    public ExactSplit(User user, BigDecimal amount) {
        super(user);
        setAmount(amount);
    }
}

class PercentSplit extends Split {
    private final BigDecimal percent;
    
    public PercentSplit(User user, BigDecimal percent) {
        super(user);
        this.percent = percent;
    }
    
    public BigDecimal getPercent() {
        return percent;
    }
}

enum ExpenseType {EQUAL, EXACT, PERCENT}

class Expense {
    private final String id;
    private final String description;
    private final User paidBy;
    private final BigDecimal amount;
    private final List<Split> splits;
    private final ExpenseType type;
    private final String groupId; // Optional - can be null for non-group expenses
    private final java.time.LocalDateTime createdAt;

    private Expense(String id, String description, User paidBy, BigDecimal amount, 
                   List<Split> splits, ExpenseType type, String groupId) {
        this.id = id;
        this.description = description;
        this.paidBy = paidBy;
        this.amount = amount;
        this.splits = new ArrayList<>(splits);
        this.type = type;
        this.groupId = groupId;
        this.createdAt = java.time.LocalDateTime.now();
        validate();
        computeSplitAmounts();
    }

    private void validate() {
        switch (type) {
            case EXACT:
                BigDecimal sum = splits.stream()
                    .map(Split::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
                if (sum.subtract(amount).abs().compareTo(new BigDecimal("0.01")) > 0) {
                    throw new IllegalArgumentException("Exact splits don't sum to total: " + sum + " vs " + amount);
                }
                break;
            case PERCENT:
                BigDecimal percent = splits.stream()
                    .filter(s -> s instanceof PercentSplit)
                    .map(s -> ((PercentSplit) s).getPercent())
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
                if (percent.subtract(new BigDecimal("100.00")).abs().compareTo(new BigDecimal("0.01")) > 0) {
                    throw new IllegalArgumentException("Percent splits don't sum to 100: " + percent);
                }
                break;
            default:
        }
    }

    private void computeSplitAmounts() {
        switch (type) {
            case EQUAL:
                BigDecimal equalAmount = amount.divide(new BigDecimal(splits.size()), 2, RoundingMode.HALF_UP);
                splits.forEach(s -> s.setAmount(equalAmount));
                break;
            case PERCENT:
                splits.forEach(s -> {
                    if (s instanceof PercentSplit) {
                        BigDecimal splitAmount = amount.multiply(((PercentSplit) s).getPercent())
                            .divide(new BigDecimal("100.00"), 2, RoundingMode.HALF_UP);
                        s.setAmount(splitAmount);
                    }
                });
                break;
            default:
        }
    }

    public String getId() { return id; }
    public String getDescription() { return description; }
    public User getPaidBy() { return paidBy; }
    public BigDecimal getAmount() { return amount; }
    public List<Split> getSplits() { return Collections.unmodifiableList(splits); }
    public ExpenseType getType() { return type; }
    public String getGroupId() { return groupId; }
    public java.time.LocalDateTime getCreatedAt() { return createdAt; }
    
    // Builder pattern for creating expenses
    public static class Builder {
        private User paidBy;
        private BigDecimal amount;
        private String description;
        private List<Split> splits = new ArrayList<>();
        private ExpenseType type;
        private String groupId;
        
        public Builder paidBy(User user) { 
            this.paidBy = user; 
            return this; 
        }
        
        public Builder amount(BigDecimal amount) { 
            this.amount = amount; 
            return this; 
        }
        
        public Builder description(String description) { 
            this.description = description; 
            return this; 
        }
        
        public Builder splits(List<? extends Split> splits) { 
            this.splits.addAll(splits); 
            return this; 
        }
        
        public Builder type(ExpenseType type) { 
            this.type = type; 
            return this; 
        }
        
        public Builder groupId(String groupId) { 
            this.groupId = groupId; 
            return this; 
        }
        
        public Expense build() {
            return new Expense(UUID.randomUUID().toString(), description, paidBy, 
                             amount, splits, type, groupId);
        }
    }
}

// ==================== MANAGER ====================
class ExpenseManager {
    // userId -> User
    private final Map<String, User> users = new HashMap<>();
    
    // groupId -> Group
    private final Map<String, Group> groups = new HashMap<>();

    // net balanceMap[from][to] -> amount (>0 means 'from' owes 'to')
    private final Map<String, Map<String, BigDecimal>> balanceMap = new HashMap<>();
    
    // Track all expenses for history
    private final List<Expense> expenseHistory = new ArrayList<>();

    /**
     * Adds a new user to the system
     * @throws IllegalArgumentException if user with same id already exists
     */
    public User addUser(String id, String name, String email) {
        Objects.requireNonNull(id, "User ID cannot be null");
        Objects.requireNonNull(name, "User name cannot be null");
        Objects.requireNonNull(email, "User email cannot be null");
        
        if (users.containsKey(id)) {
            throw new IllegalArgumentException("User with ID " + id + " already exists");
        }
        
        User u = new User(id, name, email);
        users.put(id, u);
        return u;
    }
    
    /**
     * Creates a new group with the given members
     */
    public Group createGroup(String id, String name, List<User> members) {
        Objects.requireNonNull(id, "Group ID cannot be null");
        Objects.requireNonNull(name, "Group name cannot be null");
        Objects.requireNonNull(members, "Members cannot be null");
        
        if (groups.containsKey(id)) {
            throw new IllegalArgumentException("Group with ID " + id + " already exists");
        }
        
        if (members.isEmpty()) {
            throw new IllegalArgumentException("Group must have at least one member");
        }
        
        Group group = new Group(id, name, members);
        groups.put(id, group);
        return group;
    }

    // ============ Public Add Expense APIs ============
    public void addExpenseEqual(User paidBy, BigDecimal amount, List<User> participants, String desc, String groupId) {
        List<Split> splits = participants.stream()
            .map(EqualSplit::new)
            .collect(Collectors.toList());
            
        addExpense(new Expense.Builder()
            .paidBy(paidBy)
            .amount(amount)
            .description(desc)
            .splits(splits)
            .type(ExpenseType.EQUAL)
            .groupId(groupId)
            .build());
    }

    public void addExpenseExact(User paidBy, BigDecimal amount, List<ExactSplit> splits, String desc, String groupId) {
        addExpense(new Expense.Builder()
            .paidBy(paidBy)
            .amount(amount)
            .description(desc)
            .splits(splits)
            .type(ExpenseType.EXACT)
            .groupId(groupId)
            .build());
    }

    public void addExpensePercent(User paidBy, BigDecimal amount, List<PercentSplit> splits, String desc, String groupId) {
        addExpense(new Expense.Builder()
            .paidBy(paidBy)
            .amount(amount)
            .description(desc)
            .splits(splits)
            .type(ExpenseType.PERCENT)
            .groupId(groupId)
            .build());
    }

    // ============ Core Logic ============
    private void addExpense(Expense expense) {
        expenseHistory.add(expense);
        updateBalances(expense);
    }

    private void updateBalances(Expense expense) {
        expense.getSplits().forEach(split -> {
            String owedId = split.getUser().getId();
            String paidId = expense.getPaidBy().getId();
            BigDecimal amount = split.getAmount();
            addBalance(owedId, paidId, amount);
        });
    }

    /**
     * Updates the balance between two users
     * Maintains an anti-symmetric matrix where only one direction stores non-zero value
     */
    private void addBalance(String from, String to, BigDecimal amount) {
        if (from.equals(to) || amount.compareTo(BigDecimal.ZERO) == 0) return;

        balanceMap.putIfAbsent(from, new HashMap<>());
        balanceMap.putIfAbsent(to, new HashMap<>());

        BigDecimal fromPrev = Optional.ofNullable(balanceMap.get(from).get(to)).orElse(BigDecimal.ZERO);
        BigDecimal toPrev = Optional.ofNullable(balanceMap.get(to).get(from)).orElse(BigDecimal.ZERO);
        
        // Consolidate opposite directions
        BigDecimal net = toPrev.subtract(fromPrev).add(amount).setScale(2, RoundingMode.HALF_UP);

        if (net.compareTo(BigDecimal.ZERO) >= 0) {
            balanceMap.get(from).put(to, BigDecimal.ZERO);
            balanceMap.get(to).put(from, net);
        } else {
            balanceMap.get(to).put(from, BigDecimal.ZERO);
            balanceMap.get(from).put(to, net.negate());
        }
    }

    // ============ Query APIs ============
    public void showBalance(User user) {
        Map<String, BigDecimal> map = balanceMap.getOrDefault(user.getId(), Collections.emptyMap());
        boolean none = true;
        
        for (Map.Entry<String, BigDecimal> e : map.entrySet()) {
            if (e.getValue().compareTo(BigDecimal.ZERO) > 0) {
                none = false;
                System.out.printf("%s owes %s : %.2f\n", user, users.get(e.getKey()), e.getValue());
            }
        }
        
        if (none) System.out.printf("%s has no balances\n", user);
    }

    public void showBalances() {
        users.values().forEach(this::showBalance);
    }
    
    /**
     * Shows all expenses for a specific group
     */
    public void showGroupExpenses(String groupId) {
        if (!groups.containsKey(groupId)) {
            System.out.println("Group not found");
            return;
        }
        
        Group group = groups.get(groupId);
        System.out.println("Expenses for group: " + group.getName());
        
        expenseHistory.stream()
            .filter(e -> groupId.equals(e.getGroupId()))
            .forEach(e -> {
                System.out.printf("%s paid %.2f for %s (%s)\n", 
                    e.getPaidBy().getName(), 
                    e.getAmount(), 
                    e.getDescription(),
                    e.getType());
            });
    }

    /** 
     * Greedy settle-up algorithm producing minimal transactions (O(n log n))
     * 
     * Algorithm:
     * 1. Calculate net balance for each user (positive = creditor, negative = debtor)
     * 2. Use priority queues to match largest creditors with largest debtors
     * 3. Settle maximum possible amount in each match
     * 4. Continue until all debts are settled
     */
    public void showSimplifiedDebts() {
        // Max heap for creditors (positive balances)
        PriorityQueue<Pair> creditors = new PriorityQueue<>(
            Comparator.comparing((Pair p) -> p.amount).reversed()
        );
        
        // Max heap for debtors (negative balances, stored as positive for heap)
        PriorityQueue<Pair> debtors = new PriorityQueue<>(
            Comparator.comparing((Pair p) -> p.amount).reversed());

        // Calculate net balance for each user
        for (String userId : users.keySet()) {
            BigDecimal net = calculateNetBalance(userId);
            
            if (net.compareTo(new BigDecimal("0.01")) > 0) {
                creditors.add(new Pair(userId, net));
            } else if (net.compareTo(new BigDecimal("-0.01")) < 0) {
                debtors.add(new Pair(userId, net.negate())); // Store as positive
            }
        }

        // Match creditors with debtors to minimize transactions
        while (!creditors.isEmpty() && !debtors.isEmpty()) {
            Pair creditor = creditors.poll();
            Pair debtor = debtors.poll();
            
            // Amount to settle is minimum of creditor's and debtor's amount
            BigDecimal settleAmount = creditor.amount.min(debtor.amount);
            
            // Update remaining balances
            creditor.amount = creditor.amount.subtract(settleAmount);
            debtor.amount = debtor.amount.subtract(settleAmount);
            
            System.out.printf("%s pays %s : %.2f\n", 
                users.get(debtor.userId), 
                users.get(creditor.userId), 
                settleAmount);
            
            // If either still has remaining balance, add back to queue
            if (creditor.amount.compareTo(new BigDecimal("0.01")) > 0) {
                creditors.add(creditor);
            }
            
            if (debtor.amount.compareTo(new BigDecimal("0.01")) > 0) {
                debtors.add(debtor);
            }
        }
    }
    
    /**
     * Calculate net balance for a user across all balances
     * Positive means others owe this user, negative means this user owes others
     */
    private BigDecimal calculateNetBalance(String userId) {
        BigDecimal net = BigDecimal.ZERO;
        
        // Subtract what this user owes others
        Map<String, BigDecimal> outgoing = balanceMap.getOrDefault(userId, Collections.emptyMap());
        for (BigDecimal amount : outgoing.values()) {
            net = net.subtract(amount);
        }
        
        // Add what others owe this user
        for (Map.Entry<String, Map<String, BigDecimal>> entry : balanceMap.entrySet()) {
            BigDecimal incoming = entry.getValue().getOrDefault(userId, BigDecimal.ZERO);
            net = net.add(incoming);
        }
        
        return net;
    }

    /**
     * Helper class for the simplified debt algorithm
     */
    private static class Pair {
        String userId;
        BigDecimal amount;
        
        Pair(String id, BigDecimal amt) { 
            this.userId = id; 
            this.amount = amt; 
        }
    }
}
