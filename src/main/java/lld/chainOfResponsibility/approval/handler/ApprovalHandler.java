package lld.chainOfResponsibility.approval.handler;

import lld.chainOfResponsibility.approval.PurchaseRequest;
import lombok.Data;
import lombok.extern.java.Log;

@Log
@Data
public abstract class ApprovalHandler {

    private final double maxLimit = 5000000;

    private final double approvalLimit;

    private final ApprovalHandler nextHandler;

    public ApprovalHandler(double approvalLimit, ApprovalHandler nextHandler) {
        this.approvalLimit = approvalLimit;
        this.nextHandler = nextHandler;
    }

    public abstract boolean processRequest(PurchaseRequest request);

    protected boolean validateRequest(PurchaseRequest request) {
        double requestAmount = request.getAmount();
        if (requestAmount > getMaxLimit()) {
            log.info("Rejected, Request amount higher than the MAX LIMIT.");
            return false;
        }
        return true;
    }
}
