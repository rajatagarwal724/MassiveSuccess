package lld.rules.discounts;

import lombok.Data;
import org.joda.time.DateTime;

@Data
public class Customer {
    private DateTime dateOfFirstPurchase;
    private DateTime dateOfBirth;
    private boolean isVeteran;
}
