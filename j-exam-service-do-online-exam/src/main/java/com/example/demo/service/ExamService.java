package com.example.demo.service;

import static com.example.demo.constant.Constant.*;
import static com.example.demo.constant.SQLConstants.*;
import static com.example.demo.constant.TranslationCodeConstants.*;

import com.example.demo.Enum.ExamType;
import com.example.demo.Enum.QuestionType;
import com.example.demo.command.QuerySearchCommand;
import com.example.demo.command.exam.CreateExamCommand;
import com.example.demo.command.exam.EditExamCommand;
import com.example.demo.command.exam.SubmitExamCommand;
import com.example.demo.command.exam.UpdateExamThumbnailCommand;
import com.example.demo.common.jwt.JwtTokenUtil;
import com.example.demo.common.query.QueryCondition;
import com.example.demo.common.query.QueryDateCondition;
import com.example.demo.common.response.GenerateResponseHelper;
import com.example.demo.dto.exam.EditExamDto;
import com.example.demo.dto.exam.ExamCardDto;
import com.example.demo.dto.exam.ExamDto;
import com.example.demo.dto.exam.ExamOptionDto;
import com.example.demo.dto.question.QuestionDto;
import com.example.demo.dto.question.QuestionExamDto;
import com.example.demo.entity.*;
import com.example.demo.exceptions.ExecuteSQLException;
import com.example.demo.exceptions.InvalidDateFormatException;
import com.example.demo.repository.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@EnableTransactionManagement
public class ExamService {

	@Value("${app.url.check-upload-exam-endpoint}")
	private String CHECK_USER_UPLOAD_URI;

	@Value("${app.url.upload-img-endpoint}")
	private String UPLOAD_IMAGE_URI;

	@Value("${app.url.update-img-endpoint}")
	private String UPDATE_IMAGE_URI;

	@Autowired private RestTemplate restTemplate;

	@Autowired private ExamRepository examRepository;

	@Autowired private QuestionRepository questionRepository;

	@Autowired private TranslationService translationService;

	@Autowired private CategoryRepository categoryRepository;

	@Autowired private ExportFileService exportFileService;

	@Autowired private ContestRepository contestRepository;

	@Autowired private ResultRepository resultRepository;

	// TODO: need check
	public ResponseEntity<?> getAllExam(
			QuerySearchCommand command, String token, String name, String category_ids, String duration)
			throws JsonProcessingException, ExecuteSQLException, InvalidDateFormatException {
		Map<String, QueryCondition> orParams = new HashMap<>();
		Map<String, QueryCondition> searchParams = new HashMap<>();

		searchParams.put(
				EXAM_TYPE_SEARCH_KEY,
				QueryCondition.builder()
						.value(ExamType.PRIVATE.name())
						.operation(NOT_LIKE_OPERATOR)
						.build());

		if ((token != null && !JwtTokenUtil.getTokenWithoutBearer(token).equals("null"))) {
			Long userID =
					Long.valueOf(
							JwtTokenUtil.getUserInfoFromToken(
									JwtTokenUtil.getTokenWithoutBearer(token), USER_ID_TOKEN_KEY));
			String userRoles =
					JwtTokenUtil.getUserInfoFromToken(
							JwtTokenUtil.getTokenWithoutBearer(token), USER_ROLES_TOKEN_KEY);

			if (userRoles.contains(USER_EXAM_ROLE)) {
				return GenerateResponseHelper.generateMessageResponse(
						HttpStatus.BAD_REQUEST, translationService.getTranslation(USER_NOT_ALLOW_WITH_EXAM));
			}

			if (userRoles.contains(ADMIN_ROLE)) {
				searchParams.remove(EXAM_TYPE_SEARCH_KEY);
			}

			if (userRoles.contains(USER_ROLE) || userRoles.contains(USER_PREMIUM_ROLE)) {
				orParams.put(
						EXAM_OWNER_ID_SEARCH_KEY,
						QueryCondition.builder().value(userID).operation(EQUAL_OPERATOR).build());
			}
		}

		if (!StringUtils.isEmpty(name)) {
			searchParams.put(
					EXAM_NAME_SEARCH_KEY,
					QueryCondition.builder().value(name).operation(LIKE_OPERATOR).build());
		}

		if (!StringUtils.isEmpty(category_ids)) {
			String[] arrCategoryIDs = category_ids.split(COMMA_STRING_CHARACTER);
			try {
				Long.valueOf(arrCategoryIDs[0]);
				searchParams.put(
						EXAM_CATEGORY_ID_SEARCH_KEY,
						QueryCondition.builder()
								.value(Arrays.stream(arrCategoryIDs).map(sx -> Long.parseLong(sx.trim())).toList())
								.operation(IN_OPERATOR)
								.build());
			} catch (NumberFormatException ex) {
				searchParams.put(
						EXAM_CATEGORY_NAME_SEARCH_KEY,
						QueryCondition.builder()
								.value(Arrays.asList(arrCategoryIDs))
								.operation(IN_OPERATOR)
								.build());
			}
		}

		if (!StringUtils.isEmpty(duration)) {
			String[] arrDurations = duration.split(COMMA_STRING_CHARACTER);
			try {
				searchParams.put(
						EXAM_DURATION_SEARCH_KEY,
						QueryCondition.builder()
								.value(Arrays.stream(arrDurations).map(sx -> Integer.parseInt(sx.trim())).toList())
								.operation(IN_OPERATOR)
								.build());
			} catch (Exception ex) {
				GenerateResponseHelper.generateMessageResponse(HttpStatus.BAD_REQUEST, EXECUTE_SQL_ERROR);
			}
		}

		if (QueryDateCondition.generate(command, searchParams))
			return GenerateResponseHelper.generateMessageResponse(
					HttpStatus.BAD_REQUEST, translationService.getTranslation(FROM_DATE_TO_DATE_INVALID));

		var result =
				examRepository.search(
						searchParams,
						orParams,
						command.getOrder_by(),
						command.getPage_size(),
						command.getPage_index(),
						Exam.class);

		List<Exam> exams = (List<Exam>) result.get(DATA_KEY);

		var examCardDto = exams.stream().map(ExamCardDto::new).toList();

		result.put(DATA_KEY, examCardDto);

		return GenerateResponseHelper.generateDataResponse(HttpStatus.OK, result);
	}

	// TODO: need check
	public ResponseEntity<?> getExamsOption(String token) throws JsonProcessingException {
		Long userID =
				Long.valueOf(
						JwtTokenUtil.getUserInfoFromToken(
								JwtTokenUtil.getTokenWithoutBearer(token), USER_ID_TOKEN_KEY));
		String userRoles =
				JwtTokenUtil.getUserInfoFromToken(
						JwtTokenUtil.getTokenWithoutBearer(token), USER_ROLES_TOKEN_KEY);

		if (!userRoles.contains(USER_EXAM_ROLE)) {
			var examsUser = new ArrayList<>();
			List<Exam> examsGet = new ArrayList<>();
			if (userRoles.contains(USER_ROLE) || userRoles.contains(USER_PREMIUM_ROLE)) {
				examsGet = examRepository.findAllByOwnerId(userID);
			} else if (userRoles.contains(ADMIN_ROLE)) {
				examsGet = examRepository.findAllByExamTypeIsNotLike(ExamType.PRIVATE.name());
			}

			for (Exam exam : examsGet) {
				ExamOptionDto examOptionDto = new ExamOptionDto(exam);
				examsUser.add(examOptionDto);
			}

			return GenerateResponseHelper.generateDataResponse(
					HttpStatus.OK, Map.of(DATA_KEY, examsUser));
		}

		return GenerateResponseHelper.generateMessageResponse(
				HttpStatus.BAD_REQUEST, translationService.getTranslation(USER_NOT_HAVE_EXAM_OPTIONS));
	}

	// TODO: need check
	public ResponseEntity<?> fetchExamByCategory() {
		Map<String, List<ExamCardDto>> result = new HashMap<>();
		var query = examRepository.fetchExamByCategory();
		for (Exam ex : query) {
			if (result.containsKey(ex.getCategoryName())) {
				result.get(ex.getCategoryName()).add(new ExamCardDto(ex));
			} else {
				List<ExamCardDto> examsDto = new ArrayList<>();
				examsDto.add(new ExamCardDto(ex));
				result.put(ex.getCategoryName(), examsDto);
			}
		}

		return GenerateResponseHelper.generateDataResponse(HttpStatus.OK, Map.of(DATA_KEY, result));
	}

	// TODO: need check
	@Transactional
	public ResponseEntity<?> createExam(CreateExamCommand command, String token) throws IOException {
		ObjectMapper objectMapper = new ObjectMapper();
		List<QuestionDto> listQuestionDtoConvert =
				objectMapper.readValue(command.getQuestions(), new TypeReference<>() {});
		Long userID =
				Long.valueOf(
						JwtTokenUtil.getUserInfoFromToken(
								JwtTokenUtil.getTokenWithoutBearer(token), USER_ID_TOKEN_KEY));
		String userRoles =
				JwtTokenUtil.getUserInfoFromToken(
						JwtTokenUtil.getTokenWithoutBearer(token), USER_ROLES_TOKEN_KEY);
		Optional<Category> cateOpt = categoryRepository.findById(command.getCategoryId());
		Optional<Exam> examOpt = examRepository.findExamByExamName(command.getTitle());

		if (cateOpt.isEmpty()) {
			return GenerateResponseHelper.generateMessageResponse(
					HttpStatus.BAD_REQUEST,
					translationService.getTranslation(NOT_FOUND_CATEGORY_INFORMATION));
		}

		if (examOpt.isPresent()) {
			return GenerateResponseHelper.generateMessageResponse(
					HttpStatus.BAD_REQUEST, translationService.getTranslation(EXAM_NAME_EXIST));
		}

		var exam =
				Exam.builder()
						.categoryName(cateOpt.get().getCategoryName())
						.categoryId(command.getCategoryId())
						.examName(command.getTitle())
						.ownerId(userID)
						.duration(command.getDuration())
						.description(command.getDescription())
						.build();

		if (userRoles.contains(USER_EXAM_ROLE)) {
			return GenerateResponseHelper.generateMessageResponse(
					HttpStatus.BAD_REQUEST, translationService.getTranslation(USER_NOT_ALLOW_WITH_EXAM));
		}

		if (userRoles.contains(USER_ROLE) || userRoles.contains(USER_PREMIUM_ROLE)) {
			exam.setExamType(ExamType.PRIVATE.name());
			try {
				HttpHeaders headers = new HttpHeaders();
				headers.add(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + token);
				HttpEntity<Object> entity = new HttpEntity<>(headers);
				UriComponentsBuilder builder =
						UriComponentsBuilder.fromUriString(CHECK_USER_UPLOAD_URI)
								.queryParam(FLAG_KEY, CREATE_EXAM_FLAG);
				String url = builder.toUriString();
				ResponseEntity<String> resp =
						restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
				if (!resp.getStatusCode().is2xxSuccessful()) {
					return GenerateResponseHelper.generateMessageResponse(
							HttpStatus.BAD_REQUEST, translationService.getTranslation(NOT_ALLOW_CREATE_EXAM));
				}

			} catch (Exception ex) {
				return GenerateResponseHelper.generateMessageResponse(
						HttpStatus.BAD_REQUEST, translationService.getTranslation(ERROR_CREATE_EXAM));
			}
		} else if (userRoles.contains(ADMIN_ROLE)) {
			exam.setExamType(command.getExamType().name());
		}

		// save thumbnail
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.MULTIPART_FORM_DATA);
		MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
		body.add(DOMAIN_KEY, EXAM_DOMAIN_NAME);
		body.add(FILE_TYPE_KEY, IMAGE_FOLDER_TYPE);
		ByteArrayResource contentsAsResource =
				new ByteArrayResource(command.getFile().getBytes()) {
					@Override
					public String getFilename() {
						return command.getFile().getOriginalFilename();
					}
				};
		body.add(FILE_KEY, contentsAsResource);
		HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
		ResponseEntity<String> response =
				restTemplate.exchange(UPLOAD_IMAGE_URI, HttpMethod.POST, requestEntity, String.class);

		exam.setThumbnail(response.getBody());
		var savedExam = examRepository.saveAndFlush(exam);
		var savedExamId = savedExam.getId();

		List<Question> questions =
				listQuestionDtoConvert.stream()
						.map(
								questionRequest -> {
									Question question = new Question();
									question.setQuestionPoint(questionRequest.getQuestionPoint());
									question.setQuestion(questionRequest.getQuestion());
									question.setQuestionType(QuestionType.valueOf(questionRequest.getQuestionType()));
									question.setAnswers(questionRequest.getAnswers());
									question.setCorrectAnswers(questionRequest.getCorrectAnswers());
									question.setExamId(savedExamId);
									return question;
								})
						.collect(Collectors.toList());

		if (questions.isEmpty()) {
			return GenerateResponseHelper.generateMessageResponse(
					HttpStatus.BAD_REQUEST, translationService.getTranslation(LIST_QUESTION_INVALID));
		}
		questionRepository.saveAll(questions);

		return GenerateResponseHelper.generateMessageResponse(
				HttpStatus.OK, translationService.getTranslation(SAVE_EXAM_INFORMATION_SUCCESS));
	}

	public ResponseEntity<?> getExamByName(String token, String name, Boolean flag)
			throws JsonProcessingException {
		Optional<Exam> examOpt = examRepository.findExamByExamName(name);
		if (examOpt.isEmpty()) {
			return GenerateResponseHelper.generateMessageResponse(
					HttpStatus.BAD_REQUEST, translationService.getTranslation(NOT_FOUND_EXAM_INFORMATION));
		}

		if ((token != null && !JwtTokenUtil.getTokenWithoutBearer(token).equals("null")) && !flag) {
			String userRoles =
					JwtTokenUtil.getUserInfoFromToken(
							JwtTokenUtil.getTokenWithoutBearer(token), USER_ROLES_TOKEN_KEY);
			Long userID =
					Long.valueOf(
							JwtTokenUtil.getUserInfoFromToken(
									JwtTokenUtil.getTokenWithoutBearer(token), USER_ID_TOKEN_KEY));

			String userEmail =
					JwtTokenUtil.getUserInfoFromToken(
							JwtTokenUtil.getTokenWithoutBearer(token), USER_EMAIL_TOKEN_KEY);

			Boolean isExceptToAccessExam = false;

			if (userRoles.contains(USER_PREMIUM_ROLE)) {
				if (examOpt.get().getOwnerId().compareTo(userID) == 0
						|| !examOpt.get().getExamType().equals(ExamType.PRIVATE.name())) {
					isExceptToAccessExam = true;
				}
			} else if (userRoles.contains(USER_EXAM_ROLE)) {
				// TODO: check validate user exam
				Boolean isValidParticipant = false;
				Optional<Contest> contest = contestRepository.findById(userID);
				if (contest.isEmpty()) {
					return null;
				}

				var listParticipants = contest.get().getParticipants();
				for (String participant : listParticipants) {
					if (participant.contains(userEmail)) {
						isValidParticipant = true;
						break;
					}
				}

				if (!isValidParticipant) {
					return null;
				}

			} else if (userRoles.contains(USER_ROLE)) {
				if (examOpt.get().getExamType().equals(ExamType.FREE.name())
						|| examOpt.get().getOwnerId().compareTo(userID) == 0) {
					isExceptToAccessExam = true;
				}
			}

			if (isExceptToAccessExam) {
				var questionExamDto =
						questionRepository.findQuestionByExamId(examOpt.get().getId()).stream()
								.map(QuestionExamDto::new)
								.toList();

				var exam = new ExamDto(examOpt.get(), questionExamDto);

				return GenerateResponseHelper.generateDataResponse(HttpStatus.OK, Map.of(DATA_KEY, exam));
			}
		} else if (flag) {
			var examCardDto = new ExamCardDto(examOpt.get());

			return GenerateResponseHelper.generateDataResponse(
					HttpStatus.OK, Map.of(DATA_KEY, examCardDto));
		}

		return GenerateResponseHelper.generateMessageResponse(
				HttpStatus.BAD_REQUEST, translationService.getTranslation(USER_NOT_ALLOW_WITH_EXAM));
	}

	public ResponseEntity<?> generateAndDownloadExamPDF(String token, Long examId)
			throws IOException {
		var pageNo = 0;
		//        Long userID =
		//                Long.valueOf(
		//                        JwtTokenUtil.getUserInfoFromToken(
		//                                JwtTokenUtil.getTokenWithoutBearer(token),
		// USER_ID_TOKEN_KEY));
		var exam = examRepository.findById(examId);
		if (exam.isEmpty()) {
			return GenerateResponseHelper.generateMessageResponse(
					HttpStatus.BAD_REQUEST, translationService.getTranslation(NOT_FOUND_EXAM_INFORMATION));
		}

		//        if ((exam.get().getOwnerId().compareTo(userID) != 0)) {
		//            return GenerateResponseHelper.generateMessageResponse(
		//                    HttpStatus.BAD_REQUEST,
		//                    translationService.getTranslation(NOT_ALLOW_ACCESS_EXAM_INFORMATION));
		//        }

		var questions = questionRepository.findQuestionByExamId(examId);

		try {
			var language = exportFileService.containsVietnameseChar(exam.get().getExamName());
			PDDocument document = new PDDocument();
			PDPage page = new PDPage(PDRectangle.A4);
			document.addPage(page);

			PDRectangle pageSize = page.getMediaBox();
			InputStream fontStreamManual =
					getClass().getClassLoader().getResourceAsStream(ARIAL_UNICODE_MS_PATH);
			InputStream fontStreamBold = getClass().getClassLoader().getResourceAsStream(ARIAL_BOLD_PATH);
			InputStream fontStreamBoldItalic =
					getClass().getClassLoader().getResourceAsStream(ARIAL_BOLD_ITALIC_PATH);
			PDType0Font customFontManual = PDType0Font.load(document, fontStreamManual);
			PDType0Font customFontBold = PDType0Font.load(document, fontStreamBold);
			PDType0Font customFontBoldItalic = PDType0Font.load(document, fontStreamBoldItalic);

			float startY = 780;
			float margin = 10;
			float borderY = pageSize.getLowerLeftY() + margin;
			float borderHeight = pageSize.getHeight() - 2 * margin;
			PDPageContentStream contentStream = new PDPageContentStream(document, page);
			// PageNo
			exportFileService.drawPageNumber(contentStream, ++pageNo, customFontManual, 11, 25, 13);
			// FooterContent
			exportFileService.drawFooterContent(
					contentStream,
					FOOTER_PDF_VI,
					FOOTER_PDF_EN,
					customFontBoldItalic,
					language,
					Color.RED,
					Color.BLACK,
					10,
					200,
					13);
			// Title
			exportFileService.drawContent(
					contentStream, exam.get().getExamName(), customFontBold, Color.RED, 20, 25, startY);
			// ExamId, Name
			String examIdPdf =
					language
							? "Mã bài thi: EX" + exam.get().getId() + LocalDate.now().getYear()
							: "Exam ID: EX" + exam.get().getId();
			String namePdf = language ? "Họ và tên: " + ".".repeat(50) : "Name: " + ".".repeat(50);
			exportFileService.drawContentInline(
					contentStream,
					examIdPdf,
					namePdf,
					customFontManual,
					Color.BLACK,
					12,
					475,
					startY + 35,
					25);
			// Duration
			String durationPdf =
					language
							? "Thời gian: " + exam.get().getDuration() + " phút"
							: "Duration: " + exam.get().getDuration() + " minutes";
			exportFileService.drawContent(
					contentStream, durationPdf, customFontManual, Color.BLACK, 11, 25, startY - 15);
			// Set the positions and dimensions
			String pointHeader = language ? TOTAL_POINT_CONTENT_VI : TOTAL_POINT_CONTENT_EN;
			String commentHeader = language ? COMMENT_CONTENT_VI : COMMENT_CONTENT_EN;
			exportFileService.drawTableOfPointAndComment(
					contentStream,
					pointHeader,
					commentHeader,
					380,
					page.getMediaBox().getWidth() - 25,
					720,
					15,
					customFontManual,
					12);

			int startYUpParent = 170;
			int questionNo = 0;
			int maxLinesPerPage = 45; // Số dòng tối đa trên một trang
			int remainingLines = maxLinesPerPage;

			for (Question question : questions) {
				String questionText =
						language
								? "Câu " + (++questionNo) + ": " + question.getQuestion()
								: "Question " + (++questionNo) + ": " + question.getQuestion();
				String questionType =
						language
								? ((question.getQuestionType() == QuestionType.MULTI
										? " (Chọn nhiều đáp án)"
										: " (Chọn một đáp án)"))
								: ((question.getQuestionType() == QuestionType.MULTI
										? " (Multiple choices question)"
										: " (Single choice question)"));
				questionText += questionType;

				if (startY - startYUpParent - 30 < borderY) {
					// Tạo trang mới nếu dòng hiện tại vượt quá giới hạn dưới cùng
					page = new PDPage(PDRectangle.A4);
					document.addPage(page);
					contentStream.close();
					contentStream = new PDPageContentStream(document, page);
					exportFileService.drawPageNumber(contentStream, ++pageNo, customFontManual, 11, 25, 13);
					exportFileService.drawFooterContent(
							contentStream,
							FOOTER_PDF_VI,
							FOOTER_PDF_EN,
							customFontBoldItalic,
							language,
							Color.RED,
							Color.BLACK,
							10,
							200,
							13);
					startY = 950;
					startYUpParent = 170;
					remainingLines = maxLinesPerPage;
				}

				if (startY - startYUpParent < borderY) {
					// Chuyển xuống dòng tiếp theo nếu dòng hiện tại vượt quá giới hạn trên cùng
					startYUpParent = 20;
					remainingLines--;
				}

				// Tách questionText thành các dòng
				List<String> lines =
						exportFileService.splitTextIntoLines(questionText, customFontBold, 13, 590);

				for (String line : lines) {
					if (remainingLines <= 0) {
						// Tạo trang mới nếu không đủ không gian
						page = new PDPage(PDRectangle.A4);
						document.addPage(page);
						contentStream.close();
						contentStream = new PDPageContentStream(document, page);
						exportFileService.drawPageNumber(contentStream, ++pageNo, customFontManual, 11, 25, 13);
						exportFileService.drawFooterContent(
								contentStream,
								FOOTER_PDF_VI,
								FOOTER_PDF_EN,
								customFontBoldItalic,
								language,
								Color.RED,
								Color.BLACK,
								10,
								200,
								13);
						startY = 950;
						startYUpParent = 170;
						remainingLines = maxLinesPerPage;
						startYUpParent += 20;
						remainingLines--;
					}

					contentStream.beginText();
					contentStream.setFont(customFontBold, 12);
					contentStream.newLineAtOffset(25, startY - startYUpParent);
					contentStream.showText(line);
					contentStream.endText();
					startYUpParent += 20;
					remainingLines--;
				}

				// Bắt đầu vẽ câu trả lời
				if (question.getAnswers() != null && !question.getAnswers().isEmpty()) {
					for (String answer : question.getAnswers()) {
						String answerText = "\u2610 " + answer;

						if (startY - startYUpParent - 30 < borderY) {
							// Tạo trang mới nếu dòng hiện tại vượt quá giới hạn dưới cùng
							page = new PDPage(PDRectangle.A4);
							document.addPage(page);
							contentStream.close();
							contentStream = new PDPageContentStream(document, page);
							exportFileService.drawPageNumber(
									contentStream, ++pageNo, customFontManual, 11, 25, 13);
							exportFileService.drawFooterContent(
									contentStream,
									FOOTER_PDF_VI,
									FOOTER_PDF_EN,
									customFontBoldItalic,
									language,
									Color.RED,
									Color.BLACK,
									10,
									200,
									13);
							startY = 950;
							startYUpParent = 170;
							remainingLines = maxLinesPerPage;
						}

						if (startY - startYUpParent < borderY) {
							// Chuyển xuống dòng tiếp theo nếu dòng hiện tại vượt quá giới hạn trên cùng
							startYUpParent = 20;
							remainingLines--;
						}

						List<String> answerLines =
								exportFileService.splitTextIntoLines(answerText, customFontManual, 12, 545);

						for (String line : answerLines) {
							if (remainingLines <= 0) {
								// Tạo trang mới nếu không đủ không gian
								page = new PDPage(PDRectangle.A4);
								document.addPage(page);
								contentStream.close();
								contentStream = new PDPageContentStream(document, page);
								exportFileService.drawPageNumber(
										contentStream, ++pageNo, customFontManual, 11, 25, 13);
								exportFileService.drawFooterContent(
										contentStream,
										FOOTER_PDF_VI,
										FOOTER_PDF_EN,
										customFontBoldItalic,
										language,
										Color.RED,
										Color.BLACK,
										10,
										200,
										13);
								startY = 950;
								startYUpParent = 170;
								remainingLines = maxLinesPerPage;
								startYUpParent += 20;
								remainingLines--;
							}

							contentStream.beginText();
							contentStream.setFont(customFontManual, 11);
							contentStream.newLineAtOffset(35, startY - startYUpParent);
							contentStream.showText(line);
							contentStream.endText();
							startYUpParent += 16;
							remainingLines--;
						}

						startYUpParent += 10;
						remainingLines--;
					}

					startYUpParent += 20;
					remainingLines--;
				}
			}

			page = new PDPage(PDRectangle.A4);
			document.addPage(page);
			contentStream.close();
			contentStream = new PDPageContentStream(document, page);
			exportFileService.drawPageNumber(contentStream, ++pageNo, customFontManual, 11, 25, 13);

			// Correct answers
			exportFileService.drawFooterContent(
					contentStream,
					FOOTER_PDF_VI,
					FOOTER_PDF_EN,
					customFontBoldItalic,
					language,
					Color.RED,
					Color.BLACK,
					10,
					200,
					13);

			// Table answers
			float columnWidth1 = 270f;
			float columnWidth2 = 270f;

			// Define row height
			float rowHeight = 20f;

			// Define table position
			float startXColumn = 25f;
			float startYColumn = 780;

			// Draw table content
			var questionNoAnswer = 0;
			for (Question question : questions) {
				// Draw table borders
				float endXColumn = startXColumn + columnWidth1 + columnWidth2;
				float endYColumn = startYColumn - rowHeight;

				contentStream.setStrokingColor(Color.BLACK);
				contentStream.setLineWidth(1);

				// Draw horizontal line
				contentStream.moveTo(startXColumn, startYColumn);
				contentStream.lineTo(endXColumn, startYColumn);
				contentStream.stroke();

				// Draw vertical lines
				contentStream.moveTo(startXColumn, startYColumn);
				contentStream.lineTo(startXColumn, endYColumn);
				contentStream.stroke();

				contentStream.moveTo(startXColumn + columnWidth1, startYColumn);
				contentStream.lineTo(startXColumn + columnWidth1, endYColumn);
				contentStream.stroke();

				contentStream.moveTo(endXColumn, startYColumn);
				contentStream.lineTo(endXColumn, endYColumn);
				contentStream.stroke();

				// Adjust startY for table content
				startYColumn -= rowHeight;

				// Get data for each row
				var incrementedIndexAnswer =
						question.getCorrectAnswers().stream().map(n -> n + 1L).toList();
				var correctAnswer = incrementedIndexAnswer.toString().replaceAll("\\[|\\]", "");

				String dataColumn1 =
						(language
								? "Câu " + ++questionNoAnswer
								: "Question "
										+ ++questionNoAnswer); // Replace with your logic to get data for column 1
				String dataColumn2 =
						(language
								? "Đáp án: " + correctAnswer
								: "Correct answers: "
										+ correctAnswer); // Replace with your logic to get data for column 2

				// Draw row content
				contentStream.beginText();
				contentStream.setFont(customFontBold, 14);
				contentStream.newLineAtOffset(startXColumn + 5, startYColumn + 5);
				contentStream.showText(dataColumn1);
				contentStream.newLineAtOffset(columnWidth1 + 5, 0);
				contentStream.showText(dataColumn2);
				contentStream.endText();

				// Draw horizontal line after each row
				contentStream.moveTo(startXColumn, endYColumn);
				contentStream.lineTo(endXColumn, endYColumn);
				contentStream.stroke();

				if (startYColumn - rowHeight < borderY + 25) {
					// Add a new page and reset the startYColumn
					page = new PDPage(PDRectangle.A4);
					document.addPage(page);
					contentStream.close();
					contentStream = new PDPageContentStream(document, page);
					exportFileService.drawPageNumber(contentStream, ++pageNo, customFontManual, 11, 297, 13);
					startYColumn = borderHeight - 20;
				}
			}

			contentStream.close();
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			document.save(baos);
			document.close();

			return ResponseEntity.ok()
					.header(
							HttpHeaders.CONTENT_DISPOSITION,
							"attachment;  filename=\"" + exam.get().getExamName() + ".pdf\"; charset=UTF-8")
					.contentType(MediaType.APPLICATION_PDF)
					.body(baos.toByteArray());

		} catch (Exception e) {
			return GenerateResponseHelper.generateMessageResponse(
					HttpStatus.BAD_REQUEST, translationService.getTranslation(EXPORT_EXAM_FAIL));
		}
	}

	// TODO: need test
	public ResponseEntity<?> checkExam(String token, SubmitExamCommand command)
			throws JsonProcessingException {
		// TODO handle save history exam
		LocalDateTime timeSubmit = LocalDateTime.now();
		String userRoles =
				JwtTokenUtil.getUserInfoFromToken(
						JwtTokenUtil.getTokenWithoutBearer(token), USER_ROLES_TOKEN_KEY);
		Optional<Exam> examOpt = examRepository.findById(command.getId());

		if (examOpt.isEmpty()) {
			return GenerateResponseHelper.generateMessageResponse(
					HttpStatus.BAD_REQUEST, translationService.getTranslation(NOT_FOUND_EXAM_INFORMATION));
		}
		if (command.getAnswers().size() <= 0) {
			return GenerateResponseHelper.generateMessageResponse(
					HttpStatus.BAD_REQUEST, translationService.getTranslation(EXAM_ANSWERS_INVALID));
		}

		// check answer
		double mark = 0d;
		double totalMark = 0d;
		List<Question> questions = questionRepository.findQuestionByExamId(command.getId());
		for (Question question : questions) {
			totalMark = totalMark + question.getQuestionPoint();
			for (var j = 0; j < command.getAnswers().size(); j++) {
				if (question.getId().compareTo(command.getAnswers().get(j).getId()) == 0) {
					var listAnswer1 = question.getCorrectAnswers();
					var listAnswer2 = command.getAnswers().get(j).getAnswers();
					Collections.sort(listAnswer1);
					Collections.sort(listAnswer2);
					if (listAnswer1.equals(listAnswer2)) {
						mark = mark + question.getQuestionPoint();
					}
				}
			}
		}

		var newResult = new Result();
		if (userRoles.contains(USER_EXAM_ROLE)) {
			Long contestID =
					Long.valueOf(
							JwtTokenUtil.getUserInfoFromToken(
									JwtTokenUtil.getTokenWithoutBearer(token), CONTEST_ID_TOKEN_KEY));
			newResult.setContestId(contestID);
			Optional<Contest> contestOpt = contestRepository.findById(contestID);
			if (timeSubmit.isAfter(contestOpt.get().getEndAt())) {
				return GenerateResponseHelper.generateMessageResponse(
						HttpStatus.BAD_REQUEST, translationService.getTranslation(INVALID_TIME_SUBMIT_CONTEST));
			}
		}

		String email =
				JwtTokenUtil.getUserInfoFromToken(
						JwtTokenUtil.getTokenWithoutBearer(token), USER_EMAIL_TOKEN_KEY);
		newResult.setExamId(command.getId());
		newResult.setTotalPoint(mark / totalMark * 100);
		newResult.setEmailExaminee(email);

		resultRepository.save(newResult);

		return GenerateResponseHelper.generateMessageResponse(
				HttpStatus.OK, translationService.getTranslation(SUBMIT_EXAM_SUCCESS));
	}

	// TODO: need test
	@Transactional
	public ResponseEntity<?> deleteExam(Long id, String token) throws JsonProcessingException {
		String userRoles =
				JwtTokenUtil.getUserInfoFromToken(
						JwtTokenUtil.getTokenWithoutBearer(token), USER_ROLES_TOKEN_KEY);
		Long userID =
				Long.valueOf(
						JwtTokenUtil.getUserInfoFromToken(
								JwtTokenUtil.getTokenWithoutBearer(token), USER_ID_TOKEN_KEY));

		Optional<Exam> examOpt = examRepository.findById(id);
		if (examOpt.isEmpty()) {
			return GenerateResponseHelper.generateMessageResponse(
					HttpStatus.BAD_REQUEST, translationService.getTranslation(NOT_FOUND_EXAM_INFORMATION));
		}

		if (userRoles.contains(USER_EXAM_ROLE)) {
			return GenerateResponseHelper.generateMessageResponse(
					HttpStatus.BAD_REQUEST, translationService.getTranslation(USER_NOT_ALLOW_WITH_EXAM));
		}

		if (checkExamValidToModify(examOpt.get().getId())) {
			return GenerateResponseHelper.generateMessageResponse(
					HttpStatus.BAD_REQUEST, translationService.getTranslation(EXAM_IS_BEING_USE));
		}

		if (userRoles.contains(ADMIN_ROLE)
						&& !examOpt.get().getExamType().equals(ExamType.PRIVATE.name())
				|| userID.compareTo(examOpt.get().getOwnerId()) == 0) {
			examRepository.delete(examOpt.get());
			contestRepository.deleteAllByExamId(id);
			questionRepository.deleteQuestionByExamId(id);

			return GenerateResponseHelper.generateMessageResponse(
					HttpStatus.OK, translationService.getTranslation(DELETE_EXAM_INFORMATION_SUCCESS));
		}

		return GenerateResponseHelper.generateMessageResponse(
				HttpStatus.BAD_REQUEST, translationService.getTranslation(NOT_ALLOW_REMOVE_EXAM));
	}

	// TODO: need test
	@Transactional
	public ResponseEntity<?> editExam(String token, EditExamCommand command)
			throws JsonProcessingException {
		var examOpt = examRepository.findById(command.getId());
		if (examOpt.isEmpty()) {
			return GenerateResponseHelper.generateMessageResponse(
					HttpStatus.BAD_REQUEST, translationService.getTranslation(NOT_FOUND_EXAM_INFORMATION));
		}

		if (checkExamValidToModify(examOpt.get().getId())) {
			return GenerateResponseHelper.generateMessageResponse(
					HttpStatus.BAD_REQUEST, translationService.getTranslation(EXAM_IS_BEING_USE));
		}

		String userRoles = JwtTokenUtil.getUserInfoFromToken(token, USER_ROLES_TOKEN_KEY);
		Long userID = Long.valueOf(JwtTokenUtil.getUserInfoFromToken(token, USER_ID_TOKEN_KEY));

		if ((userRoles.contains(ADMIN_ROLE)
						&& !examOpt.get().getExamType().equals(ExamType.PRIVATE.name()))
				|| (userID.compareTo(examOpt.get().getOwnerId()) == 0)) {
			Optional<Category> categoryOpt = categoryRepository.findById(command.getCategoryId());
			if (categoryOpt.isEmpty()) {
				return GenerateResponseHelper.generateMessageResponse(
						HttpStatus.BAD_REQUEST,
						translationService.getTranslation(NOT_FOUND_CATEGORY_INFORMATION));
			}

			if (command.getCategoryId().compareTo(examOpt.get().getCategoryId()) != 0) {
				examOpt.get().setCategoryId(command.getCategoryId());
			}

			if (!Objects.equals(command.getDuration(), examOpt.get().getDuration())) {
				examOpt.get().setDuration(command.getDuration());
			}

			if (!Objects.equals(command.getDescription(), examOpt.get().getDescription())) {
				examOpt.get().setDescription(command.getDescription());
			}

			if (!Objects.equals(command.getTitle(), examOpt.get().getExamName())) {
				Optional<Exam> examNameExist = examRepository.findExamByExamName(command.getTitle());
				if (examNameExist.isPresent()) {
					return GenerateResponseHelper.generateMessageResponse(
							HttpStatus.BAD_REQUEST, translationService.getTranslation(EXAM_NAME_EXIST));
				}
				examOpt.get().setExamName(command.getTitle());
			}

			if (command.getQuestions().size() <= 0) {
				return GenerateResponseHelper.generateMessageResponse(
						HttpStatus.BAD_REQUEST, translationService.getTranslation(LIST_QUESTION_INVALID));
			}
			questionRepository.deleteQuestionByExamId(command.getId());
			try {
				List<Question> questions =
						command.getQuestions().stream()
								.map(
										questionRequest -> {
											Question question = new Question();

											question.setQuestionPoint(questionRequest.getQuestionPoint());
											question.setQuestion(questionRequest.getQuestion());
											question.setQuestionType(
													QuestionType.valueOf(questionRequest.getQuestionType()));
											question.setAnswers(questionRequest.getAnswers());
											question.setCorrectAnswers(questionRequest.getCorrectAnswers());
											question.setExamId(command.getId());
											return question;
										})
								.toList();
				questionRepository.saveAll(questions);
				examRepository.save(examOpt.get());
			} catch (Exception ex) {
				return GenerateResponseHelper.generateMessageResponse(
						HttpStatus.BAD_REQUEST, translationService.getTranslation(INVALID_EXAM_QUESTION));
			}

			return GenerateResponseHelper.generateMessageResponse(
					HttpStatus.OK, translationService.getTranslation(UPDATE_EXAM_INFO_SUCCESS));
		}

		return GenerateResponseHelper.generateMessageResponse(
				HttpStatus.BAD_REQUEST, translationService.getTranslation(USER_NOT_ALLOW_WITH_EXAM));
	}

	public ResponseEntity<?> getExamsDurationOption() {
		return GenerateResponseHelper.generateDataResponse(
				HttpStatus.OK, Map.of(DATA_KEY, examRepository.getListDuration()));
	}

	private boolean checkExamValidToModify(Long id) {
		List<Contest> contests =
				contestRepository.findAllByExamIdAndEndAtAfter(id, LocalDateTime.now());
		return contests.size() > 0;
	}

	public ResponseEntity<?> updateExamThumbnail(String token, UpdateExamThumbnailCommand command)
			throws IOException {
		Optional<Exam> examOpt = examRepository.findById(command.getId());
		String userRoles =
				JwtTokenUtil.getUserInfoFromToken(
						JwtTokenUtil.getTokenWithoutBearer(token), USER_ROLES_TOKEN_KEY);
		Long userID =
				Long.valueOf(
						JwtTokenUtil.getUserInfoFromToken(
								JwtTokenUtil.getTokenWithoutBearer(token), USER_ID_TOKEN_KEY));

		if (examOpt.isEmpty()) {
			return GenerateResponseHelper.generateMessageResponse(
					HttpStatus.BAD_REQUEST, translationService.getTranslation(NOT_FOUND_EXAM_INFORMATION));
		}

		if ((userRoles.contains(ADMIN_ROLE)
						&& !examOpt.get().getExamType().equals(ExamType.PRIVATE.name()))
				|| (userID.compareTo(examOpt.get().getOwnerId()) == 0)) {
			try {
				HttpHeaders headers = new HttpHeaders();
				headers.setContentType(MediaType.MULTIPART_FORM_DATA);
				MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
				body.add(DOMAIN_KEY, EXAM_DOMAIN_NAME);
				body.add(OLD_IMAGE_PATH_KEY, examOpt.get().getThumbnail());
				ByteArrayResource contentsAsResource =
						new ByteArrayResource(command.getFile().getBytes()) {
							@Override
							public String getFilename() {
								return command.getFile().getOriginalFilename();
							}
						};
				body.add(FILE_KEY, contentsAsResource);
				HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

				ResponseEntity<String> response =
						restTemplate.exchange(UPDATE_IMAGE_URI, HttpMethod.POST, requestEntity, String.class);

				examOpt.get().setThumbnail(response.getBody());

			} catch (Exception ex) {
				return GenerateResponseHelper.generateMessageResponse(
						HttpStatus.BAD_REQUEST, translationService.getTranslation(ERROR_UPDATE_EXAM_THUMBNAIL));
			}
		}

		return GenerateResponseHelper.generateMessageResponse(
				HttpStatus.BAD_REQUEST, translationService.getTranslation(USER_NOT_ALLOW_WITH_EXAM));
	}

	// TODO: need test
	public ResponseEntity<?> getExamEdit(String token, Long id) throws JsonProcessingException {
		Optional<Exam> examOpt = examRepository.findById(id);
		if (examOpt.isEmpty()) {
			GenerateResponseHelper.generateMessageResponse(
					HttpStatus.BAD_REQUEST, translationService.getTranslation(NOT_FOUND_EXAM_INFORMATION));
		}

		String userRoles =
				JwtTokenUtil.getUserInfoFromToken(
						JwtTokenUtil.getTokenWithoutBearer(token), USER_ROLES_TOKEN_KEY);
		Long userID =
				Long.valueOf(
						JwtTokenUtil.getUserInfoFromToken(
								JwtTokenUtil.getTokenWithoutBearer(token), USER_ID_TOKEN_KEY));

		if (userID.compareTo(examOpt.get().getOwnerId()) == 0
				|| (userRoles.contains(ADMIN_ROLE)
						&& !examOpt.get().getExamType().equals(ExamType.PRIVATE.name()))) {
			List<Question> questions = questionRepository.findQuestionByExamId(examOpt.get().getId());
			List<QuestionDto> questionsDto = questions.stream().map(QuestionDto::new).toList();

			return GenerateResponseHelper.generateDataResponse(
					HttpStatus.OK, Map.of(DATA_KEY, new EditExamDto(examOpt.get(), questionsDto)));
		}

		return GenerateResponseHelper.generateMessageResponse(
				HttpStatus.BAD_REQUEST, translationService.getTranslation(USER_NOT_ALLOW_WITH_EXAM));
	}
}
