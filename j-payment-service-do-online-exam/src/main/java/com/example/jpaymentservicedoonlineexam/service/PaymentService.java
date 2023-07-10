package com.example.jpaymentservicedoonlineexam.service;

import com.example.jpaymentservicedoonlineexam.Enum.PaymentStatusType;
import com.example.jpaymentservicedoonlineexam.Enum.RecurringPaymentType;
import com.example.jpaymentservicedoonlineexam.Enum.TransactionStatusType;
import com.example.jpaymentservicedoonlineexam.common.response.GenerateResponseHelper;
import com.example.jpaymentservicedoonlineexam.config.payment.PaymentConfig;
import com.example.jpaymentservicedoonlineexam.constant.StringConstant;
import com.example.jpaymentservicedoonlineexam.dto.PaymentDto;
import com.example.jpaymentservicedoonlineexam.entity.Transaction;
import com.example.jpaymentservicedoonlineexam.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static com.example.jpaymentservicedoonlineexam.constant.StringConstant.*;

@Service
public class PaymentService {
    @Autowired
    TransactionRepository transactionRepository;
    public ResponseEntity<?> createPayment(Long totalPrice) throws UnsupportedEncodingException {
        String vnp_TxnRef = PaymentConfig.getRandomNumber(8);
        String vnp_TmnCode = PaymentConfig.vnp_TmnCode;
        Long amount = totalPrice * 100;
        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", PaymentConfig.vnp_Version);
        vnp_Params.put("vnp_Command", PaymentConfig.vnp_Command);
        vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
        vnp_Params.put("vnp_Amount", String.valueOf(amount));
        vnp_Params.put("vnp_CurrCode", PaymentConfig.vnp_CurrCode);
        vnp_Params.put("vnp_BankCode", PaymentConfig.vnp_BankCode);
        vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
        vnp_Params.put("vnp_OrderInfo", "Thanh toan don hang:" + vnp_TxnRef);
        vnp_Params.put("vnp_Locale", PaymentConfig.vnp_Locale);
        vnp_Params.put("vnp_IpAddr", PaymentConfig.vnp_IpAddress);
        vnp_Params.put("vnp_OrderType", PaymentConfig.vnp_OrderType);
        vnp_Params.put("vnp_ReturnUrl", PaymentConfig.vnp_Returnurl);

        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone(UTC));
        SimpleDateFormat formatter = new SimpleDateFormat(DATE_TIME_PATTERN);
        String vnp_CreateDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

        cld.add(Calendar.MINUTE, 15);
        String vnp_ExpireDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);

        List fieldNames = new ArrayList(vnp_Params.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();
        Iterator itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = (String) itr.next();
            String fieldValue = (String) vnp_Params.get(fieldName);
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                hashData.append(fieldName);
                hashData.append(EQUAL_STRING);
                hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString()));
                query.append(EQUAL_STRING);
                query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                if (itr.hasNext()) {
                    query.append(AMPERSAND_STRING);
                    hashData.append(AMPERSAND_STRING);
                }
            }
        }
        String queryUrl = query.toString().replaceAll(REGEX_PAYMENT_STRING, PLUS_STRING);
        String vnp_SecureHash = PaymentConfig.hmacSHA512(PaymentConfig.vnp_HashSecret, hashData.toString());
        queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;
        String paymentUrl = PaymentConfig.vnp_PayUrl + QUESTION_MARK_STRING + queryUrl;
        PaymentDto dto = new PaymentDto();
        dto.setStatus("00");
        dto.setMessage("Success");
        dto.setUrl(paymentUrl);
        return GenerateResponseHelper.generateDataResponse(
                HttpStatus.OK, Map.of(StringConstant.DATA_KEY, dto));
    }

    public ResponseEntity<?> createTransactionStatusAndResponseCode(Long amount,
                                                                    String bankCode,
                                                                    String bankTranNo,
                                                                    String cardType,
                                                                    String orderInfo,
                                                                    String payDate,
                                                                    String responseCode,
                                                                    String tmnCode,
                                                                    String transactionNo,
                                                                    String transactionStatus,
                                                                    String txnRef,
                                                                    String secureHash){
        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime payTime = LocalDateTime.parse(payDate, inputFormatter);
        if(responseCode.equals(PAYMENT_SUCCESS) && transactionStatus.equals(PAYMENT_SUCCESS)){
            RecurringPaymentType recurringType;
            if(amount == MONTH_AMOUNT){
                recurringType = RecurringPaymentType.MONTH;
            } else {
                recurringType = RecurringPaymentType.YEAR;
            }
            var transaction = Transaction.builder()
                    .transactionNo(transactionNo)
                    .bankCode(bankCode)
                    .bankTranNo(bankTranNo)
                    .cardType(cardType)
                    .orderInfo(orderInfo)
                    .transactionStatus(TransactionStatusType.SUCCESS)
                    .amount(amount)
                    .responseCode(PaymentStatusType.SUCCESS)
                    .tnxRef(txnRef)
                    .payDate(payTime)
                    .recurringPaymentType(recurringType)
                    .build();
            transactionRepository.save(transaction);
            return GenerateResponseHelper.generateDataResponse(
                    HttpStatus.OK, Map.of(MESSAGE_KEY, "Payment Success"));
        }

        return GenerateResponseHelper.generateDataResponse(
                HttpStatus.BAD_REQUEST, Map.of(MESSAGE_KEY, "Payment Failure"));
    }
}
