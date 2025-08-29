package companies.coinbase;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.ObjectMapper;

public class IncrementalBank {

    public class Account {
        String id;
        int balance;
        List<Transaction> transactions;

        public Account(String id, int balance) {
            this.id = id;
            this.balance = balance;
            this.transactions = new ArrayList<>();
        }

        public void addTx(Transaction transaction) {
            this.transactions.add(transaction);
        }
    }

    public class Transaction {
        String id;
        String fromAccount;
        String toAccount;
        int amount;
        TransactionStatus status;
        boolean isCompleted;
        LocalDateTime lastUpdatedAt;

        public Transaction(String id, String fromAccount, String toAccount, int amount, TransactionStatus status, boolean isCompleted, LocalDateTime lastUpdatedAt) {
            this.id = id;
            this.fromAccount = fromAccount;
            this.toAccount = toAccount;
            this.amount = amount;
            this.status = status;
            this.isCompleted = isCompleted;
            this.lastUpdatedAt = lastUpdatedAt;
        }
    }

    public enum TransactionStatus {
        WITHDRAWL, DEPOSIT, TRANSFER, REJECTED
    }

    private Map<String, Account> accounts;
    private Map<String, Transaction> pendingTransactions;

    AtomicLong idGenererator = new AtomicLong();

    public void createAccount(String id, int balance) {
        var user = new Account(id, balance);
        accounts.put(user.id, user);
    }

    public boolean deposit(String accountId, int amount) {
        if (!accounts.containsKey(accountId) || amount <= 0) {
            return false;
        }
//        String id, String fromAccount, String toAccount, int amount, TransactionStatus status, boolean isCompleted, LocalDateTime lastUpdatedAt
        var transaction = new Transaction(
                String.valueOf(idGenererator.incrementAndGet()), null, accountId,
                amount, TransactionStatus.DEPOSIT, true, LocalDateTime.now()
        );

        var account = accounts.get(accountId);
        account.balance += amount;
        account.addTx(transaction);
        return true;
    }

    public boolean withdraw(String accountId, int amount) {
        if (!accounts.containsKey(accountId) || amount <= 0 || accounts.get(accountId).balance < amount) {
            return false;
        }
//        String id, String fromAccount, String toAccount, int amount, TransactionStatus status, boolean isCompleted, LocalDateTime lastUpdatedAt
        var transaction = new Transaction(
                String.valueOf(idGenererator.incrementAndGet()), null, accountId,
                amount, TransactionStatus.WITHDRAWL, true, LocalDateTime.now()
        );

        var account = accounts.get(accountId);
        account.balance -= amount;
        account.addTx(transaction);
        return true;
    }

    public List<String> getTopKAccounts(int k) {
        return accounts.values().stream()
                .sorted((a, b) -> getCompletedTxs(b) - getCompletedTxs(a))
                .limit(k)
                .map(account -> account.id)
                .collect(Collectors.toList());
    }

    private int getCompletedTxs(Account account) {
        return (int) account.transactions
                .stream()
                .filter(transaction -> transaction.isCompleted)
                .count();
    }

    public boolean beginTransfer(String fromAccount, String toAccount, int amount) {
        if (!accounts.containsKey(fromAccount) || !accounts.containsKey(toAccount) || amount <= 0
        || accounts.get(fromAccount).balance < amount) {
            return false;
        }

        var transaction = new Transaction(
                String.valueOf(idGenererator.incrementAndGet()), fromAccount, toAccount,
                amount, TransactionStatus.TRANSFER, false, LocalDateTime.now()
        );
        pendingTransactions.put(transaction.id, transaction);
        var account = accounts.get(fromAccount);
        account.addTx(transaction);
        account.balance -= amount;
        return true;
    }

    public boolean acceptTransfer(String toAccountId, String transactionId) {
        if (!pendingTransactions.containsKey(transactionId)
                || !accounts.containsKey(toAccountId)
                || !toAccountId.equals(pendingTransactions.get(transactionId).toAccount)
        ) {
            return false;
        }
        var pendingTx = pendingTransactions.get(transactionId);
        var transaction = new Transaction(
                String.valueOf(idGenererator.incrementAndGet()),
                pendingTx.fromAccount, toAccountId,
                pendingTx.amount, TransactionStatus.DEPOSIT,
                true, LocalDateTime.now()
        );
        var toAccount = accounts.get(toAccountId);
        toAccount.balance += pendingTx.amount;
        toAccount.addTx(transaction);
        pendingTx.isCompleted = true;
        pendingTx.lastUpdatedAt = LocalDateTime.now();
        pendingTransactions.remove(transactionId);
        return true;
    }

    public boolean rejectTransfer(String toAccountId, String transactionId) {
        if (!pendingTransactions.containsKey(transactionId)
                || !accounts.containsKey(toAccountId)
                || !toAccountId.equals(pendingTransactions.get(transactionId).toAccount)
        ) {
            return false;
        }

        var pendingTx = pendingTransactions.get(transactionId);
        var fromAccount = accounts.get(pendingTx.fromAccount);
        fromAccount.balance += pendingTx.amount;

        fromAccount.transactions.remove(pendingTx);

        pendingTransactions.remove(transactionId);
        return true;
    }

    public boolean mergeAccounts(String primaryId, String secondaryId) {
        if (!accounts.containsKey(primaryId) || !accounts.containsKey(secondaryId)) {
            return false;
        }

        var primaryAccount = accounts.get(primaryId);
        var secondaryAccount = accounts.get(secondaryId);

        primaryAccount.balance += secondaryAccount.balance;
        secondaryAccount.balance = 0;
        secondaryAccount.transactions.clear();

        primaryAccount.transactions.addAll(secondaryAccount.transactions);
        return true;
    }

    public int getBalance(String accountId, LocalDateTime atTime) {
        var transactions = accounts.get(accountId).transactions;
        int balance = 0;

        for (var transaction: transactions) {
            if (transaction.lastUpdatedAt.isAfter(atTime) || !transaction.isCompleted) {
                continue;
            }
            if (TransactionStatus.DEPOSIT.equals(transaction.status)) {
                balance += transaction.amount;
            } else {
                balance -= transaction.amount;
            }
        }

        return balance;
    }
}
