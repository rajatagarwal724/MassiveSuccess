package lld.vendingmachine;

interface VendingMachineState {
    void handleRequest();
}

class ReadyState implements VendingMachineState {
    
    @Override
    public void handleRequest() {
        System.out.println("Ready to dispense");
    }
}

class ProductSelectedState implements VendingMachineState {
    @Override
    public void handleRequest() {
        System.out.println("Product selected");
    }
}

class DispensingState implements VendingMachineState {
    @Override
    public void handleRequest() {
        System.out.println("Dispensing");
    }
}

class PaymentPendingState implements VendingMachineState {
    @Override
    public void handleRequest() {
        System.out.println("Payment pending");
    }
}

class OutOfStockState implements VendingMachineState {
    @Override
    public void handleRequest() {
        System.out.println("Out of stock");
    }
}

class VendingMachineContext {
    private VendingMachineState currentState;

    public void setState(VendingMachineState state) {
        this.currentState = state;
    }

    public void request() {
        currentState.handleRequest();
    }
}

public class VendingMachine {
    public static void main(String[] args) {
        VendingMachineContext context = new VendingMachineContext();
        
        context.setState(new ReadyState());
        context.request();

        context.setState(new ProductSelectedState());
        context.request();

        context.setState(new PaymentPendingState());
        context.request();

        context.setState(new DispensingState());
        context.request();
        
        context.setState(new OutOfStockState());
        context.request();
    }
}
