package lld.atm.state;

public class SelectOperationState extends ATMState {

    public SelectOperationState() {
        super(State.SelectionOption);
    }

//    public void selectOperation(ATM atm, ATMCard card, TransactionType tType) {
//        // definition
//    }

    @Override
    public void exit() {

    }

    @Override
    public void returnCard() {

    }
}
