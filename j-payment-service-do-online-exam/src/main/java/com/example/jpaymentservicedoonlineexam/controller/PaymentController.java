package com.example.jpaymentservicedoonlineexam.controller;

import com.example.jpaymentservicedoonlineexam.config.websocket.MyWebSocketHandler;
import com.example.jpaymentservicedoonlineexam.service.PaymentService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.example.jpaymentservicedoonlineexam.constant.StringConstant.AUTHORIZATION;

@RestController
@RequestMapping("/api/v1/payment")
public class PaymentController {
    @Autowired
    private PaymentService paymentService;

    @Autowired
    private MyWebSocketHandler webSocketHandler;


    @GetMapping("/create_payment")
    public ResponseEntity<?> createPayment(@RequestParam(name = "totalPrice") Long totalPrice) {
        return paymentService.createPayment(totalPrice);
    }

    @GetMapping("/transaction_info")
    public void getTransactionInfo(
                                                @RequestParam(name = "vnp_Amount") Long amount,
                                                @RequestParam(name = "vnp_BankCode") String bankCode,
                                                @RequestParam(name = "vnp_BankTranNo") String bankTranNo,
                                                @RequestParam(name = "vnp_CardType") String cardType,
                                                @RequestParam(name = "vnp_OrderInfo") String orderInfo,
                                                @RequestParam(name = "vnp_ResponseCode") String responseCode,
                                                @RequestParam(name = "vnp_TransactionNo") String transactionNo,
                                                @RequestParam(name = "vnp_TransactionStatus") String transactionStatus,
                                                @RequestParam(name = "vnp_TxnRef") String txnRef
    ) throws JsonProcessingException {
        paymentService.getTransactionStatusAndResponseCode(amount,
                                                            bankCode,
                                                            bankTranNo,
                                                            cardType,
                                                            orderInfo,
                                                            responseCode,
                                                            transactionNo,
                                                            transactionStatus,
                                                            txnRef
        );
    }

    @PostMapping("/create-transaction")
    public ResponseEntity<?> createTransaction(@RequestHeader(AUTHORIZATION) String token,
                                               @RequestParam(name = "vnp_Amount") Long amount,
                                               @RequestParam(name = "vnp_BankCode") String bankCode,
                                               @RequestParam(name = "vnp_BankTranNo") String bankTranNo,
                                               @RequestParam(name = "vnp_CardType") String cardType,
                                               @RequestParam(name = "vnp_OrderInfo") String orderInfo,
                                               @RequestParam(name = "vnp_ResponseCode") String responseCode,
                                               @RequestParam(name = "vnp_TransactionNo") String transactionNo,
                                               @RequestParam(name = "vnp_TransactionStatus") String transactionStatus,
                                               @RequestParam(name = "vnp_TxnRef") String txnRef) throws JsonProcessingException {
        return paymentService.createTransaction(token,
                                                amount,
                                                bankCode,
                                                bankTranNo,
                                                cardType,
                                                orderInfo,
                                                responseCode,
                                                transactionNo,
                                                transactionStatus,
                                                txnRef);
    }
}
