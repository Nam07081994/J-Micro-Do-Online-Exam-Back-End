package com.example.demo.service;

import com.example.demo.Enum.Currency;
import com.example.demo.command.payment.ChargeResponse;
import com.example.demo.config.payment.PaymentProperties;
import com.example.demo.command.payment.ChargeRequest;
import com.example.demo.mapper.PaymentMapper;
import com.stripe.exception.StripeException;
import jakarta.annotation.PostConstruct;
import com.stripe.Stripe;
import com.stripe.model.Charge;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {
    @Value("${app.payment.secretKey}")
    String secretKey;

    @PostConstruct
    public void init() {
        Stripe.apiKey = secretKey;
    }

    public ChargeResponse charge(ChargeRequest chargeRequest)
            throws StripeException {
        Map<String, Object> chargeParams = new HashMap<>();
        chargeParams.put("amount", chargeRequest.getAmount());
        chargeParams.put("currency", chargeRequest.getCurrency() == null ? Currency.USD : chargeRequest.getCurrency());
        chargeParams.put("description", chargeRequest.getDescription());
        chargeParams.put("source", chargeRequest.getStripeToken());
        return PaymentMapper.INSTANCE.toChargeResponse(Charge.create(chargeParams));
    }
}
