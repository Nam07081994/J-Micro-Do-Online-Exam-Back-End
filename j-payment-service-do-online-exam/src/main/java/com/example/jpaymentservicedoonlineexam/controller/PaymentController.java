package com.example.jpaymentservicedoonlineexam.controller;

import com.example.jpaymentservicedoonlineexam.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.io.UnsupportedEncodingException;

@RestController
@RequestMapping("/api/v1/payment")
public class PaymentController {
    @Autowired
    PaymentService paymentService;


    @GetMapping("/create_payment")
    public ResponseEntity<?> createPayment(@RequestParam(name = "totalPrice") Long totalPrice) throws UnsupportedEncodingException {
        return paymentService.createPayment(totalPrice);
    }

    @GetMapping("/transaction_info")
    public ResponseEntity<?> getTransactionInfo(@RequestParam(name = "vnp_Amount") Long amount,
                                                @RequestParam(name = "vnp_BankCode") String bankCode,
                                                @RequestParam(name = "vnp_BankTranNo") String bankTranNo,
                                                @RequestParam(name = "vnp_CardType") String cardType,
                                                @RequestParam(name = "vnp_OrderInfo") String orderInfo,
                                                @RequestParam(name = "vnp_PayDate") String payDate,
                                                @RequestParam(name = "vnp_ResponseCode") String responseCode,
                                                @RequestParam(name = "vnp_TmnCode") String tmnCode,
                                                @RequestParam(name = "vnp_TransactionNo") String transactionNo,
                                                @RequestParam(name = "vnp_TransactionStatus") String transactionStatus,
                                                @RequestParam(name = "vnp_TxnRef") String txnRef,
                                                @RequestParam(name = "vnp_SecureHash") String secureHash){
        return paymentService.createTransactionStatusAndResponseCode(amount,
                                                                    bankCode,
                                                                    bankTranNo,
                                                                    cardType,
                                                                    orderInfo,
                                                                    payDate,
                                                                    responseCode,
                                                                    tmnCode,
                                                                    transactionNo,
                                                                    transactionStatus,
                                                                    txnRef,
                                                                    secureHash);
    }

}
