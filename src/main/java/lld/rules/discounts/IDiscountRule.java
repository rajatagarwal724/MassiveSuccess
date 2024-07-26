package lld.rules.discounts;

public interface IDiscountRule {
    double calculateDiscount(Customer customer, double currentDiscount);
}
