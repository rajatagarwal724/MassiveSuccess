package companies.coinbase;

public class Bank {
    long[] balances;
    public Bank(long[] balance) {
        this.balances = balance;
    }

    public boolean transfer(int account1, int account2, long money) {
        if (account1 > balances.length || account2 > (balances.length)) {
            return false;
        }

        if (balances[account1 - 1] < money) {
            return false;
        }

        balances[account2 - 1]+= balances[account1 - 1];
        return true;
    }

    public boolean deposit(int account, long money) {
        if (account > (balances.length)) {
            return false;
        }
        balances[account - 1] += money;
        return true;
    }

    public boolean withdraw(int account, long money) {
        if (account > (balances.length) || balances[account - 1] < money) {
            return false;
        }
        balances[account - 1]-=money;
        return true;
    }

    public static void main(String[] args) {
        var bankSystem = new Bank(new long[] {10, 100, 20, 50, 30});
        System.out.println(bankSystem.withdraw(3, 10));
        System.out.println(bankSystem.transfer(5, 1, 10));
        System.out.println(bankSystem.deposit(5, 20));
        System.out.println(bankSystem.transfer(3, 4, 15));
        System.out.println(bankSystem.withdraw(10, 50));
    }
}
