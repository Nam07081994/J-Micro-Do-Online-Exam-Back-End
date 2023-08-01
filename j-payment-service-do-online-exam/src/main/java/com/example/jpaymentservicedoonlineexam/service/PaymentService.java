package com.example.jpaymentservicedoonlineexam.service;

import com.example.jpaymentservicedoonlineexam.Enum.PaymentStatusType;
import com.example.jpaymentservicedoonlineexam.Enum.RecurringPaymentType;
import com.example.jpaymentservicedoonlineexam.Enum.TransactionStatusType;
import com.example.jpaymentservicedoonlineexam.common.jwt.JwtTokenUtil;
import com.example.jpaymentservicedoonlineexam.common.response.GenerateResponseHelper;
import com.example.jpaymentservicedoonlineexam.config.payment.PaymentConfig;
import com.example.jpaymentservicedoonlineexam.config.websocket.MyWebSocketHandler;
import com.example.jpaymentservicedoonlineexam.constant.StringConstant;
import com.example.jpaymentservicedoonlineexam.dto.PaymentDto;
import com.example.jpaymentservicedoonlineexam.dto.UserDto;
import com.example.jpaymentservicedoonlineexam.dto.VnPayDto;
import com.example.jpaymentservicedoonlineexam.entity.Transaction;
import com.example.jpaymentservicedoonlineexam.repository.TransactionRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static com.example.jpaymentservicedoonlineexam.constant.StringConstant.*;
import static org.hibernate.cfg.AvailableSettings.USER;

@Service
public class PaymentService {
    @Autowired
    TransactionRepository transactionRepository;

    @Autowired
    private MyWebSocketHandler webSocketHandler;

    @Autowired
    RestTemplate restTemplate;
    public ResponseEntity<?> createPayment(Long totalPrice) {
        try {
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
        }catch (Exception e){
            return GenerateResponseHelper.generateDataResponse(
                    HttpStatus.BAD_REQUEST, Map.of(MESSAGE_KEY, "Cannot redirect to payment"));
        }
    }

    public void getTransactionStatusAndResponseCode(Long amount,
                                                    String bankCode,
                                                    String bankTranNo,
                                                    String cardType,
                                                    String orderInfo,
                                                    String responseCode,
                                                    String transactionNo,
                                                    String transactionStatus,
                                                    String txnRef) throws JsonProcessingException {
        if(responseCode.equals(PAYMENT_SUCCESS)){
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> objectMap = mapper.convertValue(new VnPayDto(amount,
                    bankCode,
                    bankTranNo,
                    cardType,
                    orderInfo,
                    responseCode,
                    transactionNo,
                    transactionStatus,
                    txnRef), Map.class);
            String eventJson = mapper.writeValueAsString(objectMap);
            webSocketHandler.sendEventToClients(eventJson);
        } else webSocketHandler.sendEventToClients("Payment Fail");
    }

    public ResponseEntity<?> createTransaction(String token,
            Long amount,
            String bankCode,
            String bankTranNo,
            String cardType,
            String orderInfo,
            String responseCode,
            String transactionNo,
            String transactionStatus,
            String txnRef) throws JsonProcessingException {
                var tokenSub = token.substring(7);
        Long userId = Long.valueOf(JwtTokenUtil.getuserNameFromToken(tokenSub));
        LocalDate currentDate = LocalDate.now();
        var transactionExist = transactionRepository.findFirstByUserIdOrderByCreatedAtDesc(userId);
        if(transactionExist.isPresent() && (currentDate.isAfter(transactionExist.get().getPayDate()) || currentDate.equals(transactionExist.get().getPayDate())) && currentDate.isBefore(transactionExist.get().getExpiredDate())){
                    transactionExist.get().setExpiredDate(amount == MONTH_AMOUNT ? transactionExist.get().getExpiredDate().plusMonths(1) : transactionExist.get().getExpiredDate().plusYears(1));
                    transactionRepository.save(transactionExist.get());
                return GenerateResponseHelper.generateDataResponse(
                        HttpStatus.OK, Map.of(MESSAGE_KEY, "Extend Success"));
        } else {
            var transaction = new Transaction();
            transaction.setTransactionNo(transactionNo);
            transaction.setBankCode(bankCode);
            transaction.setBankTranNo(bankTranNo);
            transaction.setCardType(cardType);
            transaction.setOrderInfo(orderInfo);
            transaction.setTransactionStatus(TransactionStatusType.SUCCESS);
            transaction.setAmount(amount);
            transaction.setResponseCode(PaymentStatusType.SUCCESS);
            transaction.setTnxRef(txnRef);
            transaction.setPayDate(LocalDate.now());
            transaction.setExpiredDate(amount == MONTH_AMOUNT ? LocalDate.now().plusMonths(1) : LocalDate.now().plusYears(1));
            transaction.setUserId(userId);
            HttpStatusCode statusCode = changeRoleOfUserForCheckPayment(userId, PREMIUM_ROLE_NAME, token);
            if (statusCode.isSameCodeAs(HttpStatus.OK)) {
                transactionRepository.save(transaction);
                return GenerateResponseHelper.generateDataResponse(
                        HttpStatus.OK, Map.of(MESSAGE_KEY, "Create Transaction Success", NEW_ACCESS_TOKEN, JwtTokenUtil.generateToken(getUser(userId).getUsername(),getUser(userId).getEmail(), PREMIUM_ROLE_NAME, String.valueOf(userId))));
            } else return GenerateResponseHelper.generateDataResponse(
                    HttpStatus.OK, Map.of(MESSAGE_KEY, "Create Transaction Fail"));
        }
    }


    public void changeExpiredAccount() throws JsonProcessingException {
        List<Transaction> transactions = transactionRepository.findAllByExpiredDate(LocalDate.now());
        if(!transactions.isEmpty()){
            for (Transaction tran: transactions) {
                String userEmail = getEmailFromUser(tran.getUserId());
                changeRoleOfUser(tran.getUserId(),USER_ROLE_NAME);
                sendEmailForAccountExpired(userEmail, tran.getPayDate().toString(), tran.getExpiredDate().toString());
            }
        }
    }

    public void sendEmailForAccountExpire() throws JsonProcessingException {
        val currentDatePlusMonth = LocalDate.now().plusWeeks(1);
        List<Transaction> transactions = transactionRepository.findAllByExpiredDate(currentDatePlusMonth);
        if(!transactions.isEmpty()){
            for (Transaction tran: transactions) {
                String userEmail = getEmailFromUser(tran.getUserId());
                sendEmailForAccountExpire(userEmail, tran.getPayDate().toString(), tran.getExpiredDate().toString());
            }
        }
    }

    private HttpStatusCode changeRoleOfUserForCheckPayment(Long userId, String roleName, String token){
        HttpHeaders headers = new HttpHeaders();
        headers.set(AUTHORIZATION, token);
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("userId", userId);
        body.put("roleName", roleName);
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(body, headers);
        ResponseEntity<String> response = restTemplate.exchange("http://localhost:8764/api/v1/auth/update/role", HttpMethod.POST, requestEntity, String.class);
        HttpStatusCode statusCode = response.getStatusCode();
        return statusCode;
    }

    private void changeRoleOfUser(Long userId, String roleName){
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("userId", userId);
        body.put("roleName", roleName);
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(body);
        restTemplate.exchange("http://localhost:8764/api/v1/auth/update/role", HttpMethod.POST, requestEntity, String.class);
    }

    private void sendEmailForAccountExpire(String email, String registedDate, String expiredDate){
        String emailBody =
                String.format(
                        EMAIL_BODY_EXPIRE,
                        "Customer",
                        registedDate,
                        expiredDate
                        );
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("email", email);
        body.put("subject", EMAIL_SUBJECT);
        body.put("body", emailBody);
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(body);
        restTemplate.exchange("http://localhost:8764/api/v1/auth/accounts-exam/sendEmail", HttpMethod.POST,requestEntity, String.class);
    }

    private void sendEmailForAccountExpired(String email, String registedDate, String expiredDate){
        String emailBody =
                String.format(
                        EMAIL_BODY_EXPIRED,
                        "Customer",
                        registedDate,
                        expiredDate
                );
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("email", email);
        body.put("subject", EMAIL_SUBJECT);
        body.put("body", emailBody);
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(body);
        restTemplate.exchange("http://localhost:8764/api/v1/auth/accounts-exam/sendEmail", HttpMethod.POST,requestEntity, String.class);
    }

    private String getEmailFromUser(Long userId) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        queryParams.add("userId", String.valueOf(userId));
        String url = UriComponentsBuilder.fromUriString("http://localhost:8764/api/v1/auth/user-info")
                .queryParams(queryParams)
                .build()
                .toString();
        String body = restTemplate.exchange(url, HttpMethod.GET, null, String.class).getBody();
        UserDto dto = mapper.readValue(body, UserDto.class);
        return dto.getEmail();
    }


    private UserDto getUser(Long userId) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        queryParams.add("userId", String.valueOf(userId));
        String url = UriComponentsBuilder.fromUriString("http://localhost:8764/api/v1/auth/user-info")
                .queryParams(queryParams)
                .build()
                .toString();
        String body = restTemplate.exchange(url, HttpMethod.GET, null, String.class).getBody();
        UserDto dto = mapper.readValue(body, UserDto.class);
        return dto;
    }
}
