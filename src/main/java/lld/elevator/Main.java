package lld.elevator;

public class Main {

    public static void main(String[] args) {
        var controller = new Controller(3, 10);
        controller.handleRequest(new Request(5));
    }
}
