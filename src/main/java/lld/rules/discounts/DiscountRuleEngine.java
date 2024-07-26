package lld.rules.discounts;

import java.util.ArrayList;
import java.util.List;

public class DiscountRuleEngine {

    private List<IDiscountRule> rules = new ArrayList<>();

    public DiscountRuleEngine(List<IDiscountRule> rules) {
        this.rules = rules;
    }

    public double calculateDiscountPercentage(Customer customer) {
        double discount = 0;
        for (IDiscountRule discountRule: rules) {
            discount = Math.max(discount, discountRule.calculateDiscount(customer, discount));
        }
        return discount;
    }
}
