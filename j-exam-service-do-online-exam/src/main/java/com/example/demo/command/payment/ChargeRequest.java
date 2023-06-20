package com.example.demo.command.payment;

import com.example.demo.Enum.Currency;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class  ChargeRequest {

    private String description;

    private Currency currency;

    @NotBlank
    private int amount;

    @NotBlank
    private String stripeEmail;

    @NotBlank
    private String stripeToken;
}
