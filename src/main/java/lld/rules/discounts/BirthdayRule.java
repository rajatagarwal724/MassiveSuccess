package lld.rules.discounts;

import org.joda.time.DateTime;

public class BirthdayRule implements IDiscountRule {
    @Override
    public double calculateDiscount(Customer customer, double currentDiscount) {
        var todaysDate = DateTime.now();
        var customerDateOfBirth = customer.getDateOfBirth();
        boolean isBirthday = todaysDate.getMonthOfYear() == customerDateOfBirth.getMonthOfYear()
                && todaysDate.getDayOfMonth() == customerDateOfBirth.getDayOfMonth();
        if (isBirthday) {
            return currentDiscount + 0.10;
        }
        return 0;
    }
}
