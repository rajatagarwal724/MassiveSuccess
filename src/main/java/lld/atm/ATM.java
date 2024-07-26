package lld.atm;

import lld.atm.state.ATMState;

public class ATM {
    private static ATM atm = new ATM();

    private ATMState currentAtmState;

    private int atmBalance;
    private int numberOfFiveHunderedNotes;
    private int numberOfTwoHunderedNotes;
    private int numberOfOneHunderedNotes;

    private CardReader cardReader;
    private CashDispenser cashDispenser;
    private Keypad keypad;
    private Screen screen;
    private Printer printer;

    public void displayCurrentState() {

    }

    public void initialize(int numberOfFiveHunderedNotes, int numberOfTwoHunderedNotes, int numberOfOneHunderedNotes) {

    }

}
