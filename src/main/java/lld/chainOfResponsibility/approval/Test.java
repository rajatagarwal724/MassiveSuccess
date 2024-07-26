package lld.chainOfResponsibility.approval;

public class Test {

    public static void main(String[] args) {
        ApprovalProcessor processor = new ApprovalProcessor();
        var request = new PurchaseRequest(5000000);
        processor.process(request);

//        var request1 = new PurchaseRequest(50000);
//        processor.process(request1);
    }
}
