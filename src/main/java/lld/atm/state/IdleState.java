package lld.atm.state;

import lld.atm.ATM;
import lld.atm.ATMCard;

public class IdleState extends ATMState {

    public IdleState() {
        super(State.Idle);
    }

    @Override
    public void exit() {

    }

    @Override
    public void returnCard() {

    }

    public void insertCard(ATM atm, ATMCard card) {
        // definition
    }

}
