package lld.atm.account;

public class CurrentAccount extends BankAccount {

    @Override
    public double withdrawLimit() {
        return 0;
    }
}
