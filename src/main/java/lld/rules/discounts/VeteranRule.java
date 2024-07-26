package lld.rules.discounts;

public class VeteranRule implements IDiscountRule {
    @Override
    public double calculateDiscount(Customer customer, double currentDiscount) {
        if (customer.isVeteran()) {
            return 0.1;
        }
        return 0;
    }
}
