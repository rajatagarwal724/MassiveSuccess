package lld.rules.discounts;

import org.joda.time.DateTime;
import org.joda.time.Years;

public class SeniorRule implements IDiscountRule{
    @Override
    public double calculateDiscount(Customer customer, double currentDiscount) {
        int customerAge = Math.abs(
                Years.yearsBetween(DateTime.now(), customer.getDateOfBirth()).getYears()
        );
        if (customerAge > 65) {
            return 0.05;
        }
        return 0;
    }
}
