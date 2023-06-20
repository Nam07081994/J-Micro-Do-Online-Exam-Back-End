package com.example.demo.controller;

import com.example.demo.command.payment.CheckoutDataResponse;
import com.example.demo.command.payment.CheckoutRequest;
import com.example.demo.config.payment.PaymentProperties;
import com.example.demo.command.payment.ChargeRequest;
import com.example.demo.service.PaymentService;
import com.stripe.exception.StripeException;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

@RestController
@AllArgsConstructor
public class PaymentController {
    private PaymentProperties paymentProperties;
    private PaymentService paymentService;

    @PostMapping("/checkout")
    public CheckoutDataResponse createPaymentIntent(@RequestBody @Valid CheckoutRequest chargeRequest) {
        return new CheckoutDataResponse(chargeRequest.getRoleUpdate().getPrice(), paymentProperties.getPublicKey(), chargeRequest.getCurrency(), chargeRequest.getDiscount());
    }

    @PostMapping("/charge")
    public ResponseEntity<?> charge(ChargeRequest chargeRequest) throws StripeException {
        return ResponseEntity.ok(paymentService.charge(chargeRequest));
    }
}
