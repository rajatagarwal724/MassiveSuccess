package lld.chainOfResponsibility.approval.handler;

import lld.chainOfResponsibility.approval.PurchaseRequest;
import lombok.extern.java.Log;

import java.util.Objects;

@Log
public class VicePresidentHandler extends ApprovalHandler {

    public VicePresidentHandler(double approvalLimit, ApprovalHandler nextHandler) {
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
            log.info("Approved by Vice President");
            return true;
        }

        if (Objects.nonNull(getNextHandler())) {
            log.info("Vice President approved the request, but it requires further approval.");
            return getNextHandler().processRequest(request);
        }
        log.info("Vice President rejected the request.");
        return false;
    }
}
