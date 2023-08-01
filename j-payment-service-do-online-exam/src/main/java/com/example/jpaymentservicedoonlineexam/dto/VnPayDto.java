package com.example.jpaymentservicedoonlineexam.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class VnPayDto {
    private Long amount;
    private String bankCode;
    private String bankTranNo;
    private String cardType;
    private String orderInfo;
    private String responseCode;
    private String transactionNo;
    private String transactionStatus;
    private String txnRef;
}
