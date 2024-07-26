package lld.rules.discounts;

import org.joda.time.DateTime;
import org.joda.time.Years;

public class Test {

    public static void main(String[] args) {
        Customer customer = new Customer();
        customer.setVeteran(false);
//        Calendar calendar = new GregorianCalendar(1993, Calendar.JULY, 15);
        DateTime dateTime = new DateTime().withDate(1993, 7, 15);

        customer.setDateOfBirth(new DateTime().withDate(1993, 7, 15));
        customer.setDateOfFirstPurchase(new DateTime().withDate(2011, 9, 5));

        System.out.println(dateTime);


        var dateTime1 = new DateTime().withDate(2011, 9, 29);
        var dateTime2 = DateTime.now();

        System.out.println(Years.yearsBetween(dateTime1, dateTime2).getYears());
//        DateTime.now().minus(customer.getDateOfFirstPurchase().getMillis()).getYear()

        DiscountCalculator discountCalculator = new DiscountCalculator();
        System.out.println(discountCalculator.calculateDiscountPercentage(customer));
    }
}
