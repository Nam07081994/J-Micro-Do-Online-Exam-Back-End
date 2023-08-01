package com.example.jpaymentservicedoonlineexam.schedule;

import com.example.jpaymentservicedoonlineexam.service.PaymentService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
public class TransactionSchedule {
    private PaymentService paymentService;

    @Scheduled(cron = "0 18 21 * * *")
    public void changeExpiredAccount() throws JsonProcessingException {
        paymentService.sendEmailForAccountExpire();
        paymentService.changeExpiredAccount();
    }
}
