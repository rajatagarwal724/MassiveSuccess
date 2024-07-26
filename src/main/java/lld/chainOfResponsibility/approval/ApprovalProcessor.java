package lld.chainOfResponsibility.approval;

import lld.chainOfResponsibility.approval.handler.ApprovalHandler;
import lld.chainOfResponsibility.approval.handler.CeoHandler;
import lld.chainOfResponsibility.approval.handler.DirectorHandler;
import lld.chainOfResponsibility.approval.handler.ManagerHandler;
import lld.chainOfResponsibility.approval.handler.VicePresidentHandler;

public class ApprovalProcessor {

    private ApprovalHandler approvalChain;

    public ApprovalProcessor() {
        createChain();
    }

    private void createChain() {
        ApprovalHandler ceoHandler = new CeoHandler(1000000, null);
        ApprovalHandler vicePresidentHandler = new VicePresidentHandler(500000, ceoHandler);
        ApprovalHandler directorHandler = new DirectorHandler(50000, vicePresidentHandler);
        approvalChain = new ManagerHandler(10000, directorHandler);
    }

    public void process(PurchaseRequest purchaseRequest) {
        approvalChain.processRequest(purchaseRequest);
    }

}
