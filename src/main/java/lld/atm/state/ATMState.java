package lld.atm.state;

public abstract class ATMState {
//    public abstract void insertCard(ATM atm, ATMCard card);
//    public abstract void authenticatePin(ATM atm, ATMCard card, int pin);
//    public abstract void selectOperation(ATM atm, ATMCard card, TransactionType tType);
//    public abstract void cashWithdrawal(ATM atm, ATMCard card, int withdrawAmount);
//    public abstract void displayBalance(ATM atm, ATMCard card);
//    public abstract void transferMoney(ATM atm, ATMCard card, int accountNumber, int transferAmount);
//    public abstract void exit(ATM atm);

    private final State state;

    public ATMState(State state) {
        this.state = state;
    }

    public abstract void exit();
    public abstract void returnCard();
}
