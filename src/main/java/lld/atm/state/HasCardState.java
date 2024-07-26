package lld.atm.state;

import lld.atm.ATM;
import lld.atm.ATMCard;

public class HasCardState extends ATMState {
    public HasCardState() {
        super(State.HasCard);
    }

    public void authenticatePin(ATM atm, ATMCard card, int pin) {

    }

    @Override
    public void exit() {

    }

    @Override
    public void returnCard() {

    }
}
