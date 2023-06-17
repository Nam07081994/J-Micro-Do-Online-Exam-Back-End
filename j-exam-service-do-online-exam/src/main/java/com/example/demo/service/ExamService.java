package com.example.demo.service;

import static com.example.demo.constant.Constant.*;
import static com.example.demo.constant.SQLConstants.*;
import static com.example.demo.constant.TranslationCodeConstants.*;

import com.example.demo.Enum.QuestionType;
import com.example.demo.command.QuerySearchCommand;
import com.example.demo.command.exam.CreateExamCommand;
import com.example.demo.command.exam.EditExamCommand;
import com.example.demo.common.jwt.JwtTokenUtil;
import com.example.demo.common.query.QueryCondition;
import com.example.demo.common.query.QueryDateCondition;
import com.example.demo.common.response.GenerateResponseHelper;
import com.example.demo.dto.exam.ExamCardDto;
import com.example.demo.dto.exam.ExamDto;
import com.example.demo.dto.exam.ExamOptionDto;
import com.example.demo.dto.question.QuestionExamDto;
import com.example.demo.entity.Contest;
import com.example.demo.entity.Exam;
import com.example.demo.entity.Question;
import com.example.demo.exceptions.ExecuteSQLException;
import com.example.demo.repository.CategoryRepository;
import com.example.demo.repository.ContestRepository;
import com.example.demo.repository.ExamRepository;
import com.example.demo.repository.QuestionRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;
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

	public ResponseEntity<?> getAllExam(
			QuerySearchCommand command, String token, String name, String category_ids, int duration)
			throws JsonProcessingException, ExecuteSQLException {
		// TODO: handle logic get exam for normal_user or premium_user
		Map<String, QueryCondition> orParams = new HashMap<>();
		Map<String, QueryCondition> searchParams = new HashMap<>();

		if (token.isEmpty()) {
			searchParams.put(
					EXAM_IS_PRIVATE_SEARCH_KEY,
					QueryCondition.builder().value(EXAM_PUBLIC_FLAG).operation(EQUAL_OPERATOR).build());
		} else {
			Long userID = Long.valueOf(JwtTokenUtil.getUserInfoFromToken(token, USER_ID_TOKEN_KEY));
			String userRoles = JwtTokenUtil.getUserInfoFromToken(token, USER_ROLES_TOKEN_KEY);

			if (userRoles.contains(USER_EXAM_ROLE)) {
				return GenerateResponseHelper.generateMessageResponse(
						HttpStatus.BAD_REQUEST, translationService.getTranslation(USER_NOT_ALLOW_WITH_EXAM));
			}
			if (userRoles.contains(USER_ROLE) || userRoles.contains(USER_PREMIUM_ROLE)) {
				orParams.put(
						EXAM_OWNER_ID_SEARCH_KEY,
						QueryCondition.builder().value(userID).operation(EQUAL_OPERATOR).build());
			}
		}

		if (!name.isEmpty()) {
			searchParams.put(
					EXAM_NAME_SEARCH_KEY,
					QueryCondition.builder().value(name).operation(LIKE_OPERATOR).build());
		}

		if (!category_ids.isEmpty()) {
			String[] arrCategoryIDs = category_ids.split(",");
			var categoryIDs = Arrays.asList(arrCategoryIDs);
			searchParams.put(
					EXAM_CATEGORY_SEARCH_KEY,
					QueryCondition.builder().value(categoryIDs).operation(IN_OPERATOR).build());
		}

		if (duration > 0) {
			searchParams.put(
					EXAM_DURATION_SEARCH_KEY,
					QueryCondition.builder().value(duration).operation(EQUAL_OPERATOR).build());
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

		var examCardDto =
				exams.stream()
						.map(
								e -> {
									var categoryName =
											categoryRepository.findById(e.getCategoryId()).get().getCategoryName();
									return new ExamCardDto(e, categoryName);
								})
						.toList();

		result.put(DATA_KEY, examCardDto);

		return GenerateResponseHelper.generateDataResponse(HttpStatus.OK, result);
	}

	public ResponseEntity<?> getExamsOption(String token) throws JsonProcessingException {
		Long userID = Long.valueOf(JwtTokenUtil.getUserInfoFromToken(token, USER_ID_TOKEN_KEY));
		String userRoles = JwtTokenUtil.getUserInfoFromToken(token, USER_ROLES_TOKEN_KEY);

		if (!userRoles.contains(USER_EXAM_ROLE)
				&& (userRoles.contains(USER_ROLE) || userRoles.contains(USER_PREMIUM_ROLE))) {
			var examsUser = new ArrayList<>();
			for (Exam exam : examRepository.findAllByOwnerId(userID)) {
				ExamOptionDto examOptionDto = new ExamOptionDto(exam);
				examsUser.add(examOptionDto);
			}

			return GenerateResponseHelper.generateDataResponse(
					HttpStatus.OK, Map.of(DATA_KEY, examsUser));
		}

		return GenerateResponseHelper.generateMessageResponse(
				HttpStatus.BAD_REQUEST, translationService.getTranslation(USER_NOT_HAVE_EXAM_OPTIONS));
	}

	@Transactional
	public ResponseEntity<?> createExam(CreateExamCommand command, String token) throws IOException {
		Long userID = Long.valueOf(JwtTokenUtil.getUserInfoFromToken(token, USER_ID_TOKEN_KEY));
		String userRoles = JwtTokenUtil.getUserInfoFromToken(token, USER_ROLES_TOKEN_KEY);
		var exam =
				Exam.builder()
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
			exam.setIsPrivate(EXAM_PRIVATE_FLAG);
			try {
				HttpHeaders headers = new HttpHeaders();
				headers.add(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + token);
				HttpEntity<Object> entity = new HttpEntity<>(headers);
				UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(CHECK_USER_UPLOAD_URI);
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
				command.getQuestions().stream()
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

		Long ownerID = Long.valueOf(JwtTokenUtil.getUserInfoFromToken(token, USER_ID_TOKEN_KEY));
		if ((ownerID.compareTo(examOpt.get().getOwnerId()) != 0)
				&& examOpt.get().getIsPrivate() == EXAM_PRIVATE_FLAG) {
			return GenerateResponseHelper.generateMessageResponse(
					HttpStatus.BAD_REQUEST,
					translationService.getTranslation(NOT_ALLOW_ACCESS_EXAM_INFORMATION));
		}

		var categoryName =
				categoryRepository.findById(examOpt.get().getCategoryId()).get().getCategoryName();

		if (flag) {
			var examCardDto = new ExamCardDto(examOpt.get(), categoryName);

			return GenerateResponseHelper.generateDataResponse(
					HttpStatus.OK, Map.of(DATA_KEY, examCardDto));
		}

		var questionExamDto =
				questionRepository.findQuestionByExamId(examOpt.get().getId()).stream()
						.map(QuestionExamDto::new)
						.toList();

		var exam = new ExamDto(examOpt.get(), categoryName, questionExamDto);

		return GenerateResponseHelper.generateDataResponse(HttpStatus.OK, Map.of(DATA_KEY, exam));
	}

	public ResponseEntity<?> generateAndDownloadExamPDF(String token, Long examId)
			throws JsonProcessingException {
		var pageNo = 0;
		Long userID = Long.valueOf(JwtTokenUtil.getUserInfoFromToken(token, USER_ID_TOKEN_KEY));
		var exam = examRepository.findById(examId);
		if (exam.isEmpty()) {
			return GenerateResponseHelper.generateMessageResponse(
					HttpStatus.BAD_REQUEST, translationService.getTranslation(NOT_FOUND_EXAM_INFORMATION));
		}
		if (exam.get().getIsPrivate() == EXAM_PRIVATE_FLAG
				&& (exam.get().getOwnerId().compareTo(userID) != 0)) {
			return GenerateResponseHelper.generateMessageResponse(
					HttpStatus.BAD_REQUEST,
					translationService.getTranslation(NOT_ALLOW_ACCESS_EXAM_INFORMATION));
		}

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
							? "Thời gian thi: " + exam.get().getDuration() + " phút"
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
										? " (Câu hỏi chọn nhiều đáp án)"
										: " (Câu hỏi chọn một đáp án)"))
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
						question.getCorrectAnswers().stream().map(n -> n + 1L).collect(Collectors.toList());
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

	public ResponseEntity<?> deleteExam(Long id, String token) throws JsonProcessingException {
		String userRoles = JwtTokenUtil.getUserInfoFromToken(token, USER_ROLES_TOKEN_KEY);
		Long userID = Long.valueOf(JwtTokenUtil.getUserInfoFromToken(token, USER_ID_TOKEN_KEY));

		Optional<Exam> examOpt = examRepository.findById(id);
		if (examOpt.isEmpty()) {
			return GenerateResponseHelper.generateMessageResponse(
					HttpStatus.BAD_REQUEST, translationService.getTranslation(NOT_FOUND_EXAM_INFORMATION));
		}

		if (userRoles.contains(USER_EXAM_ROLE)) {
			return GenerateResponseHelper.generateMessageResponse(
					HttpStatus.BAD_REQUEST, translationService.getTranslation(USER_NOT_ALLOW_WITH_EXAM));
		}

		if (userRoles.contains(ADMIN_ROLE) && examOpt.get().getIsPrivate() == EXAM_PRIVATE_FLAG
				|| (userID.compareTo(examOpt.get().getOwnerId()) != 0
						&& examOpt.get().getIsPrivate() == EXAM_PRIVATE_FLAG)) {
			return GenerateResponseHelper.generateMessageResponse(
					HttpStatus.BAD_REQUEST, translationService.getTranslation(NOT_ALLOW_REMOVE_EXAM));
		}

		if (checkExamValidToModify(examOpt.get().getId())) {
			return GenerateResponseHelper.generateMessageResponse(
					HttpStatus.BAD_REQUEST, translationService.getTranslation(EXAM_IS_BEING_USE));
		}

		examRepository.delete(examOpt.get());
		contestRepository.deleteAllByExamId(id);
		questionRepository.deleteQuestionByExamId(id);

		return GenerateResponseHelper.generateMessageResponse(
				HttpStatus.OK, translationService.getTranslation(DELETE_EXAM_INFORMATION_SUCCESS));
	}

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

		return null;
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
}
