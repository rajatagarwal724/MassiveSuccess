package lld.chainOfResponsibility.approval;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PurchaseRequest {
    private double amount;
}
