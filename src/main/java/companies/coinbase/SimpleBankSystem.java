package companies.coinbase;

public class SimpleBankSystem {

    private long[] balances;

    int maxAccountNo;

    public SimpleBankSystem(long[] balance) {
        this.balances = new long[balance.length + 1];
        for (int i = 0; i < balance.length; i++) {
            this.balances[i + 1] = balance[i];
        }

        maxAccountNo = balance.length;
    }

    private boolean isValidAccount(int accountNo) {
        if (1 <= accountNo && accountNo <= maxAccountNo) {
            return true;
        }

        return false;
    }

    private boolean isValidTx(int accountNo, long amount) {
        if (balances[accountNo] < amount) {
            return false;
        }

        return true;
    }

    public boolean transfer(int account1, int account2, long money) {
        if (!isValidAccount(account1) || !isValidAccount(account2)) {
            return false;
        }

        if (!isValidTx(account1, money)) {
            return false;
        }

        balances[account1] -= money;
        balances[account2] += money;

        return true;
    }

    public boolean deposit(int account, long money) {
        if (!isValidAccount(account)) {
            return false;
        }

        balances[account] += money;
        return true;
    }

    public boolean withdraw(int account, long money) {
        if (!isValidAccount(account)) {
            return false;
        }

        if (!isValidTx(account, money)) {
            return false;
        }

        balances[account] -= money;
        return true;
    }
}
