package lld.chainOfResponsibility.approval.handler;

import lld.chainOfResponsibility.approval.PurchaseRequest;
import lombok.extern.java.Log;

import java.util.Objects;

@Log
public class ManagerHandler extends ApprovalHandler {
    public ManagerHandler(double approvalLimit, ApprovalHandler nextHandler) {
        super(approvalLimit, nextHandler);
    }

    @Override
    public boolean processRequest(PurchaseRequest request) {
        double requestAmount = request.getAmount();

        if (requestAmount > getMaxLimit()) {
            log.info("Rejected, Request amount higher than the MAX LIMIT.");
            return false;
        }

        if (requestAmount <= getApprovalLimit()) {
            log.info("Approved by Manager");
            return true;
        }

        if (Objects.nonNull(getNextHandler())) {
            log.info("Manager Approved the Request, but it requires Further Approval.");
            return getNextHandler().processRequest(request);
        }
        log.info("Manager rejected the request.");
        return false;
    }
}
