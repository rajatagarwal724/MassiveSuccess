package lld.rules.discounts;

import org.joda.time.DateTime;

import java.util.List;

public class DiscountCalculator {

    public double calculateDiscountPercentage(Customer customer) {
        var rules = List.of(
                new FirstTimeCustomerRule(),
                new LoyalCustomerRule(),
                new SeniorRule(),
                new VeteranRule(),
                new BirthdayRule()
        );

        var engine = new DiscountRuleEngine(rules);

        return engine.calculateDiscountPercentage(customer);
    }


    public static void main(String[] args) {
        Customer customer = new Customer();
        customer.setVeteran(false);
        customer.setDateOfFirstPurchase(new DateTime().withDate(2011, 9, 29));
        customer.setDateOfBirth(new DateTime().withDate(1993, 7, 15));

        DiscountCalculator discountCalculator = new DiscountCalculator();
        System.out.println(discountCalculator.calculateDiscountPercentage(customer));
    }

//    public double calculateDiscountPercentage(Customer customer) {
//        if (Objects.isNull(customer.getDateOfFirstPurchase())) {
//            return 0.15;
//        } else {
//            int yearsFromDateOfFirstPurchase = Math.abs(Years.yearsBetween(DateTime.now(), customer.getDateOfFirstPurchase()).getYears());
//            if (yearsFromDateOfFirstPurchase > 15) {
//                return 0.15;
//            }
//            if (yearsFromDateOfFirstPurchase > 10) {
//                return 0.12;
//            }
//            if (yearsFromDateOfFirstPurchase > 5) {
//                return 0.10;
//            }
//            if (!customer.isVeteran() && yearsFromDateOfFirstPurchase > 2) {
//                return 0.08;
//            }
//            if (!customer.isVeteran() && yearsFromDateOfFirstPurchase > 1) {
//                return 0.05;
//            }
//        }
//
//        if (customer.isVeteran()) {
//            return .10;
//        }
//
//        int customerAge = Math.abs(Years.yearsBetween(DateTime.now(), customer.getDateOfBirth()).getYears());
//
//        if (customerAge > 65) {
//            return 0.05;
//        }
//        return 0;
//    }
}
