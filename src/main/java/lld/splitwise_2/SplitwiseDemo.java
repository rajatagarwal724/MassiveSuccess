package lld.splitwise_2;

import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.UUID;

public class SplitwiseDemo {

    public static void main(String[] args) {
        ExpenseManager manager = new ExpenseManager();

        User u1 = new User("u1");
        User u2 = new User("u2");
        User u3 = new User("u3");

        // create users
        manager.addUser(u1);
        manager.addUser(u2);
        manager.addUser(u3);

        // 1. Equal split – u1 pays 300 for u1, u2, u3 (100 each)
        manager.addExpense(u1, 300, SplitType.EQUAL, List.of(
                new EqualSplit(u1),
                new EqualSplit(u2),
                new EqualSplit(u3)
        ));


        // 2. Exact split – u2 pays 125, owes: u1 40, u2 45, u3 40
        manager.addExpense(u2, 125, SplitType.EXACT,
                Arrays.asList(
                        new ExactSplit(u1, 40),
                        new ExactSplit(u2, 45),
                        new ExactSplit(u3, 40)
                )
        );
//
//        // 3. Percent split – u3 pays 120, split 40%, 20%, 40%
        manager.addExpense(u3, 120, SplitType.PERCENT,
                Arrays.asList(
                        new PercentageSplit(u1, 40),
                        new PercentageSplit(u2, 20),
                        new PercentageSplit(u3, 40)
                )
        );

        System.out.println("\n---- Individual Balances ----");
        manager.showBalance(u1);
        manager.showBalance(u2);
        manager.showBalance(u3);

        System.out.println("\n---- All Balances ----");
        manager.showAllBalances();

        System.out.println("\n---- Simplified Settlements ----");
        manager.showSimplifiedDebts();
    }
}

@Data
@RequiredArgsConstructor
class User {
    private final String name;
}

enum SplitType {
    EQUAL, EXACT, PERCENT
}

@Data
abstract class Split {
    protected User user;
    protected double amount;

    public Split(User user) {
        this.user = user;
    }

    abstract void setAmount(double amount);
}

class EqualSplit extends Split {

    public EqualSplit(User user) {
        super(user);
    }

    @Override
    void setAmount(double amount) {
        super.amount = amount;
    }
}

class ExactSplit extends Split {

    public ExactSplit(User user, double amount) {
        super(user);
        setAmount(amount);
    }

    @Override
    void setAmount(double amount) {
        super.amount = amount;
    }
}

@Getter
@Setter
class PercentageSplit extends Split {
    private double percentage;

    public PercentageSplit(User user, double percentage) {
        super(user);
        this.percentage = percentage;
    }


    @Override
    void setAmount(double amount) {
        this.amount = amount;
    }
}

@Data
class Expense {
    private final String id;
    private final User paidBy;
    private final List<Split> splits;
    private final SplitType type;
    private final double totalAmount;

    public Expense(String id, User paidBy, List<Split> splits, SplitType type, double totalAmount) {
        this.id = id;
        this.paidBy = paidBy;
        this.splits = splits;
        this.type = type;
        this.totalAmount = totalAmount;

        validate();
        computeSplitAmounts();
    }

    private void computeSplitAmounts() {
        switch (type) {
            case EQUAL -> {
                double splitAmount = round(totalAmount / splits.size());
                splits.forEach(split -> split.setAmount(splitAmount));
            }
            case PERCENT -> {
                splits.stream().map(split -> (PercentageSplit) split).forEach(split -> {
                    double percentage = split.getPercentage();
                    split.setAmount(round(percentage * totalAmount/100));
                });
            }
        }
    }

    private void validate() {
        switch (type) {
            case EXACT -> {
                double sum = splits
                        .stream()
                        .map(split -> (ExactSplit) split)
                        .mapToDouble(Split::getAmount)
                        .sum();
                if (Math.abs(totalAmount - sum) > 0.01) {
                    throw new IllegalArgumentException("Exact Splits Don't Match");
                }
            }
            case PERCENT -> {
                double totalPercent = splits
                        .stream()
                        .map(split -> (PercentageSplit) split)
                        .mapToDouble(PercentageSplit::getPercentage)
                        .sum();
                if (Math.abs(100 - totalPercent) > 0.01) {
                    throw new IllegalArgumentException("Percent Split doesn't match");
                }
            }
        }
    }

    private double round(double amount) {
        return Math.round((amount/100.0) * 100.0);
    }
}

class ExpenseManager {

    private final Map<String, User> users = new HashMap<>();
    private final Map<User, Map<User, Double>> balanceSheet = new HashMap<>();


    public void addUser(User user) {
        users.computeIfAbsent(user.getName(), s -> user);
    }

    public void addExpense(User user, double totalAmount, SplitType splitType, List<Split> splits) {
        Expense expense = new Expense(
                UUID.randomUUID().toString(),
                user,
                splits,
                splitType,
                totalAmount
        );
        updateBalance(expense);
    }

    private void updateBalance(Expense expense) {
        for (Split split: expense.getSplits()) {
            User paidBy = expense.getPaidBy();
            User paidTo = split.getUser();
            double amount = split.getAmount();
            addBalance(paidBy, paidTo, amount);
        }
    }

    private void addBalance(User from, User to, double amount) {

        if (to.getName().equals(from.getName()) || amount == 0) {
            return;
        }

        balanceSheet.putIfAbsent(from, new HashMap<>());
        balanceSheet.putIfAbsent(to, new HashMap<>());

        double fromPrevAmountOwed = balanceSheet.getOrDefault(from, Collections.emptyMap())
                .getOrDefault(to, 0.0);
        double toPrevAmountOwed = balanceSheet.getOrDefault(to, Collections.emptyMap())
                .getOrDefault(from, 0.0);

        double net = round(toPrevAmountOwed - fromPrevAmountOwed + amount);

        if (net >= 0) {
            balanceSheet.get(to).put(from, net);
            balanceSheet.get(from).put(to, 0.0);
        } else {
            balanceSheet.get(from).put(to, -net);
            balanceSheet.get(to).put(from, 0.0);
        }
    }

    public void showBalance(User user) {
        Map<User, Double> balances = balanceSheet.getOrDefault(user, Collections.emptyMap());
        if (!balances.isEmpty()) {
            balances
                    .entrySet()
                    .stream()
                    .filter(userDoubleEntry -> userDoubleEntry.getValue() > 0)
                    .forEach(userDoubleEntry -> {
                        System.out.println(user.getName() + " owes " + userDoubleEntry.getKey().getName() + " Amount Rs " + userDoubleEntry.getValue());
                    });
        }
    }

    public void showAllBalances() {
        users.forEach((s, user) -> showBalance(user));
    }

    private static double round(double v) { return Math.round(v * 100.0) / 100.0; }

    public void showSimplifiedDebts() {
        Queue<Pair> creditorsMaxHeap = new PriorityQueue<>(Comparator.comparing(Pair::getAmount).reversed());
        Queue<Pair> debtorsMaxHeap = new PriorityQueue<>(Comparator.comparing(Pair::getAmount).reversed());

        for (User user: users.values()) {
            double netBalance = calculateNetBalance(user);

            if (netBalance > 0.0) {
                creditorsMaxHeap.offer(new Pair(user, netBalance));
            } else if (netBalance < 0.0) {
                debtorsMaxHeap.offer(new Pair(user, -netBalance));
            }
        }

        while (!creditorsMaxHeap.isEmpty() && !debtorsMaxHeap.isEmpty()) {
            var creditorPoll = creditorsMaxHeap.poll();
            var debtorPoll = debtorsMaxHeap.poll();
        }
    }

    private double calculateNetBalance(User user) {
        double netBalance = 0.0;

        Map<User, Double> balanceOutgoing = balanceSheet.getOrDefault(user, Collections.emptyMap());
        if (!balanceOutgoing.isEmpty()) {
            double totalOutgoingBalance = balanceOutgoing.values().stream().mapToDouble(aDouble -> aDouble).sum();
            netBalance = round(netBalance - totalOutgoingBalance);
        }

        for (Map<User, Double> incomingBalance: balanceSheet.values()) {
            double incoming = incomingBalance.getOrDefault(user, 0.0);
            netBalance = round(netBalance + incoming);
        }
        return netBalance;
    }

    @Data
    @RequiredArgsConstructor
    private static class Pair {
        private final User user;
        private final double amount;
    }
}
