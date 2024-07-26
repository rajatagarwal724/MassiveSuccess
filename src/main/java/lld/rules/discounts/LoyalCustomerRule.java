package lld.rules.discounts;

import org.joda.time.DateTime;
import org.joda.time.Years;

public class LoyalCustomerRule implements IDiscountRule {
    @Override
    public double calculateDiscount(Customer customer, double currentDiscount) {
        int yearsFromDateOfFirstPurchase = Math.abs(
                Years.yearsBetween(DateTime.now(), customer.getDateOfFirstPurchase()).getYears()
        );

        if (yearsFromDateOfFirstPurchase > 15) {
            return 0.15;
        } else if (yearsFromDateOfFirstPurchase > 10) {
            return 0.12;
        } else if (yearsFromDateOfFirstPurchase > 5) {
            return 0.10;
        } else if (yearsFromDateOfFirstPurchase > 2) {
            return 0.08;
        } else if (yearsFromDateOfFirstPurchase > 1) {
            return 0.05;
        }

        return 0;
    }
}
