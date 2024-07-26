package lld.atm.account;

public abstract class BankAccount {
    private long accountNumber;
    private double availableBalance;
    private double totalBalance;

    public double getAvailableBalance() {
        return totalBalance;
    }

    public abstract double withdrawLimit();
}
