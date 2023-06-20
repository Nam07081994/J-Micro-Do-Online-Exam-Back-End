package com.example.demo.command.payment;

import com.example.demo.Enum.Currency;
import com.example.demo.Enum.UserRoleType;
import lombok.Data;

@Data
public class CheckoutRequest {
    private UserRoleType roleUpdate;
    private Integer discount;   // 50(%)
    private Currency currency;
}
