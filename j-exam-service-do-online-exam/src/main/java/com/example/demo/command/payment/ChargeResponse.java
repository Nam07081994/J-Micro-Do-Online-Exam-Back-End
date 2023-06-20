package com.example.demo.command.payment;

import lombok.Data;

@Data
public class ChargeResponse {
    private String id;
    private String status;
    private String balanceTransaction;
}
