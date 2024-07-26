package lld.rules.discounts;

import java.util.Objects;

public class FirstTimeCustomerRule implements IDiscountRule {
    @Override
    public double calculateDiscount(Customer customer, double currentDiscount) {
        if (Objects.isNull(customer.getDateOfFirstPurchase())) {
            return 0.15;
        }
        return 0;
    }
}
