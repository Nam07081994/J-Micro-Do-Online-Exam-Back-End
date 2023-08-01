package com.example.demo.constant;

public class StringConstant {

	//
	public static final Integer EXAM_UPLOAD_KEY = 1;

	public static final Integer CONTEST_UPLOAD_KEY = 2;

	//
	public static final String USERNAME_TOKEN_KEY = "username";

	public static final String DATA_KEY = "data";

	public static final String VALIDATE_KEY = "validate";

	public static final String MESSAGE_KEY = "message";

	public static final String ACCESS_TOKEN_KEY = "access-token";

	public static final String REFRESH_TOKEN_KEY = "refresh-token";

	public static final String DOMAIN_KEY = "domain";

	public static final String FILE_TYPE_KEY = "fileType";

	public static final String FILE_KEY = "file";

	public static final String OLD_IMAGE_PATH_KEY = "oldImagePath";

	public static final String PAGINATION_KEY = "pagination";

	public static final String TOTAL_RECORDS_KEY = "totals";

	public static final String PAGES_KEY = "pages";

	public static final String PAGE_INDEX = "index";

	public static final String ENDPOINT_KEY = "endPoint";

	public static final String EMAIL_KEY = "email";

	public static final String USERNAME_KEY = "userName";

	public static final String PHONE_KEY = "phone";

	public static final String CREATED_AT_KEY = "createdAt";

	public static final String ROLE_NAME_KEY = "roleName";

	public static final String BEARER_KEY = "Bearer ";
	public static final String AUTHENTICATION_KEY = "Authorization";

	// Role string constants
	public static final String USER_ROLE_STRING = "USER";

	public static final String USER_EXAM_ROLE_STRING = "USER_EXAM";

	public static final String USER_PREMIUM_ROLE_STRING = "USER_PREMIUM";

	public static final String USER_DOMAIN_NAME = "users";

	public static final String IMAGE_FOLDER_TYPE = "images/";

	public static final String COMMA_STRING_CHARACTER = ",";

	public static final String HYPHEN_STRING_CHARACTER = "-";

	public static final String DOT_STRING_CHARACTER = ".";

	public static final String EMPTY_STRING = "";

	public static final String SPACE_STRING = " ";

	// SQL string constants
	public static final String AND_STATEMENT = " AND ";

	public static final String WHERE_STATEMENT = " WHERE ";

	public static final String ORDER_STATEMENT = " ORDER BY ";

	public static final String LIKE_OPERATOR = "LIKE";

	public static final String EQUAL_OPERATOR = "=";

	public static final String GREATER_THAN_OPERATION = ">=";

	public static final String LESS_THAN_OPERATOR = "<=";

	public static final String PERCENT_OPERATOR = "%";

	public static final String EMAIL_SUBJECT = "DO-ONLINE CONTEST INFORMATION";

	public static final String EMAIL_BODY =
			"<html>"
					+ "<body>"
					+ "<p>Dear %s,</p>"
					+ "<p>You successfully submitted your contest. Each time you submit a contest, you receive a unique login information. You can view your information below.</p>"
					+ "<h3>Account Contest details:</h3>"
					+ "<ul>"
					+ "<li>Contest ID: %s</li>"
					+ "<li>User name: %s</li>"
					+ "<li>Password: %s</li>"
					+ "<li>Link login: <a href=\"%s\">Click here to login</a></li>"
					+ "</ul>"
					+ "<h3>Note:</h3>"
					+ "<ol>"
					+ "<li>Please log in on time for the contest. If you log in after the test starts or after the test ends, your test will not be recorded.</li>"
					+ "<ul>"
					+ "<li>Contest start time: %s</li>"
					+ "<li>Contest end time: %s</li>"
					+ "</ul>"
					+ "<li>If you quit midway through the contest, you cannot enter the contest again.</li>"
					+ "</ol>"
					+ "<p>Good luck with the contest!</p>"
					+ "<p>DO-ONLINE</p>"
					+ "</body>"
					+ "</html>";

	public static final String LOGIN_CONTEST_LINK = "http://localhost:3000/contest/login?token=";

	public static final String EMAIL_WHILE_SENDING_ERROR = "Error while sending mail!!!";

	public static final String EMAIL_SENDING_SUCCESS = "Send email success";

	public static final String EXAM_USERNAME_PREFIX = "USER";

	public static final String EXAM_PASSWORD_PREFIX = "PWD";
}
