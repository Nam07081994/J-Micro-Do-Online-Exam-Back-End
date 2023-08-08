package com.example.jpaymentservicedoonlineexam.constant;

public class StringConstant {
    public static String EQUAL_STRING = "=";
    public static String AMPERSAND_STRING = "&";
    public static String DATE_TIME_PATTERN = "yyyyMMddHHmmss";
    public static String UTC = "Etc/GMT+7";
    public static String QUESTION_MARK_STRING = "?";
    public static String PLUS_STRING = "+";
    public static String REGEX_PAYMENT_STRING = "%20";
    public static final String MESSAGE_KEY = "message";
    public static final String NEW_ACCESS_TOKEN = "accessToken";
    public static final String DATA_KEY = "data";
    public static final String BEARER_PREFIX = "Bearer ";
    public static final String PAYMENT_SUCCESS = "00";
    public static final Long MONTH_AMOUNT = 200000L;
    public static final String PREMIUM_ROLE_NAME = "USER_PREMIUM";
    public static final String USER_ROLE_NAME = "USER";

    public static final String AUTHORIZATION = "Authorization";

    public static final String EMAIL_SUBJECT = "DO-ONLINE: PREMIUM EXPIRY NOTICE";

    public static final String EMAIL_BODY_EXPIRE =
            "<html>"
                    + "<body>"
                    + "<p>Dear %s,</p>"
                    + "<p>First of all, we would like to thank you for trusting us. We are pleased to inform that your Premium Account registered on: <b>%s</b> will expire on <b>%s</b>.</p>"
                    + "<p>To continue using the features from the DO_ONLINE app, please renew your Premium Account.</p>"
                    + "<p>Thank you and have a good day</p>"
                    + "<p>DO-ONLINE</p>"
                    + "</body>"
                    + "</html>";

    public static final String EMAIL_BODY_EXPIRED =
            "<html>"
                    + "<body>"
                    + "<p>Dear %s,</p>"
                    + "<p>First of all, we would like to thank you for trusting us. We are pleased to inform that your Premium Account registered on: <b>%s</b> will expire on <b>TODAY</b>.</p>"
                    + "<p>To continue using the features from the DO_ONLINE app, please renew your Premium Account.</p>"
                    + "<p>Thank you and have a good day</p>"
                    + "<p>DO-ONLINE</p>"
                    + "</body>"
                    + "</html>";
}
