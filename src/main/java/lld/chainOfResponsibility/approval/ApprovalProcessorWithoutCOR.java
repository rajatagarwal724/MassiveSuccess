package lld.chainOfResponsibility.approval;

import lombok.extern.java.Log;

@Log
public class ApprovalProcessorWithoutCOR {

    public boolean proccessRequest(PurchaseRequest request) {
        log.info("Manager approved the request.");
        // manager checks for request raised by employee till date,
        // and that sum should be below his teams budget
        // 1. Employee expense is in individual limit
        // 2. Total budget under Manager doesn't exceed

        double amount = request.getAmount();

        if (amount > 10000 && amount <= Integer.MAX_VALUE) {
            log.info("Director Approved the request.");
            // director shouls approve if it fits his annual budget, and also validates that expenses are approvable
        }

        if (amount > 50000 && amount <= Integer.MAX_VALUE) {
            log.info("Vice President Approved the request.");
            // VP ensures that the request was not forged, and the cross-checks the bills

        }

        if (amount > 500000 && amount <= Integer.MAX_VALUE) {
            log.info("Vice President Approved the request.");
            // CEO seeks a written reason for expense letter from the person for approval.
        }

        if (amount > Integer.MAX_VALUE) {
            log.info("Rejected");
            return false;
        }
        return true;
    }
}
