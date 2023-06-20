package com.example.demo.command.payment;

import com.example.demo.Enum.Currency;
import lombok.Data;

@Data
public class CheckoutDataResponse {
    private Integer amount;
    private String stripePublicKey;
    private Currency currency;

    public CheckoutDataResponse(Integer amount, String stripePublicKey, Currency currency, Integer discount) {
        this.amount = amount*(100-discount)/100;
        this.stripePublicKey = stripePublicKey;
        this.currency = currency == null ? Currency.EUR : currency;
    }
}
