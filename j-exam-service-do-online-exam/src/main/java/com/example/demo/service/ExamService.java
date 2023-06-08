package com.example.demo.service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.example.demo.Enum.QuestionType;
import com.example.demo.command.CreateExamCommand;
import com.example.demo.common.response.CommonResponse;
import com.example.demo.entity.Exam;
import com.example.demo.entity.Question;
import com.example.demo.repository.ExamRepository;
import com.example.demo.repository.QuestionRepository;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;

import static com.example.demo.constant.Constant.ARIAL_BOLD_ITALIC_PATH;
import static com.example.demo.constant.Constant.ARIAL_BOLD_PATH;
import static com.example.demo.constant.Constant.ARIAL_UNICODE_MS_PATH;
import static com.example.demo.constant.Constant.COMMENT_CONTENT_EN;
import static com.example.demo.constant.Constant.COMMENT_CONTENT_VI;
import static com.example.demo.constant.Constant.FOOTER_PDF_EN;
import static com.example.demo.constant.Constant.FOOTER_PDF_VI;
import static com.example.demo.constant.Constant.TOTAL_POINT_CONTENT_EN;
import static com.example.demo.constant.Constant.TOTAL_POINT_CONTENT_VI;

@Service
@EnableTransactionManagement
public class ExamService {
    @Autowired
    private ExamRepository examRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Transactional
    public ResponseEntity<?> createExam(CreateExamCommand command){
        var exam = Exam.builder()
                .categoryId(command.getCategoryId())
                .examName(command.getTitle())
                .duration(command.getDuration())
                .description(command.getDescription())
                .build();

        var savedExam = examRepository.saveAndFlush(exam);
        var savedExamId = savedExam.getId();

        List<Question> questions = command.getQuestions().stream()
                .map(questionRequest -> {
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

        if(questions.isEmpty()){
            //TODO: need add i18n message
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(CommonResponse.builder()
                    .body(Map.of("message", "Cannot save question"))
                    .build()
                    .getBody());
        }
        questionRepository.saveAll(questions);

        //TODO: need add i18n message
        return ResponseEntity.status(HttpStatus.OK).body(CommonResponse.builder()
                .body(Map.of("message", "Create Exam Success"))
                .build()
                .getBody());
    }

    private boolean containsVietnameseChar(String text) {
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (Character.UnicodeBlock.of(c) == Character.UnicodeBlock.LATIN_EXTENDED_ADDITIONAL) {
                return true;
            }
        }
        return false;
    }

    private List<String> splitTextIntoLines(String text, PDFont font, int fontSize, int maxWidth) throws IOException {
        List<String> lines = new ArrayList<>();
        int textWidth = (int) (font.getStringWidth(text) / 1000 * fontSize);

        if (textWidth <= maxWidth) {
            // Chuỗi không vượt quá giới hạn chiều rộng
            lines.add(text);
        } else {
            // Phân chia chuỗi thành các dòng
            StringBuilder currentLine = new StringBuilder();
            String[] words = text.split("\\s+"); // Tách chuỗi thành từng từ

            for (String word : words) {
                String tempLine = currentLine + " " + word;
                int tempWidth = (int) (font.getStringWidth(tempLine) / 1000 * fontSize);

                if (tempWidth <= maxWidth) {
                    // Thêm từ vào dòng hiện tại nếu chiều rộng không vượt quá giới hạn
                    currentLine.append(" ").append(word);
                } else {
                    // Thêm dòng hiện tại vào danh sách dòng và bắt đầu một dòng mới
                    lines.add(currentLine.toString().trim());
                    currentLine = new StringBuilder(word);
                }
            }

            // Thêm dòng cuối cùng vào danh sách dòng
            lines.add(currentLine.toString().trim());
        }
        return lines;
    }

    public ResponseEntity<?> generateAndDownloadExamPDF(Long examId) {
        var pageNo = 0;
        var questions = questionRepository.findQuestionByExamId(examId);
        var exam = examRepository.findById(examId);
        if(!exam.isPresent()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(CommonResponse.builder()
                    .body(Map.of("message", "Exam is not exist!!"))
                    .build()
                    .getBody());
        } else if(questions.isEmpty()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(CommonResponse.builder()
                    .body(Map.of("message", "Question is not exist!!"))
                    .build()
                    .getBody());
        }

        try {
            var language  = containsVietnameseChar(exam.get().getExamName());
            PDDocument document = new PDDocument();
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            PDRectangle pageSize = page.getMediaBox();
            InputStream fontStreamManual = getClass().getClassLoader().getResourceAsStream(ARIAL_UNICODE_MS_PATH);
            InputStream fontStreamBold = getClass().getClassLoader().getResourceAsStream(ARIAL_BOLD_PATH);
            InputStream fontStreamBoldItalic = getClass().getClassLoader().getResourceAsStream(ARIAL_BOLD_ITALIC_PATH);
            PDType0Font customFontManual = PDType0Font.load(document, fontStreamManual);
            PDType0Font customFontBold = PDType0Font.load(document, fontStreamBold);
            PDType0Font customFontBoldItalic = PDType0Font.load(document, fontStreamBoldItalic);
            float startY = 780;
            float margin = 10;
            float borderY = pageSize.getLowerLeftY() + margin;
            float borderHeight = pageSize.getHeight() - 2 * margin;
            PDPageContentStream contentStream = new PDPageContentStream(document, page);
            // PageNo
            drawPageNumber(contentStream, ++pageNo, customFontManual, 11, 25, 13);
            // FooterContent
            drawFooterContent(contentStream, FOOTER_PDF_VI, FOOTER_PDF_EN, customFontBoldItalic, language, Color.RED, Color.BLACK, 10, 200, 13);
            //Title
            drawContent(contentStream, exam.get().getExamName(), customFontBold, Color.RED, 20, 25, startY);
            //ExamId, Name
            String examIdPdf = language ? "Mã bài thi: EX" + exam.get().getId() + LocalDate.now().getYear() : "Exam ID: EX" + exam.get().getId();
            String namePdf = language ? "Họ và tên: " + ".".repeat(50) : "Name: " + ".".repeat(50);
            drawContentInline(contentStream, examIdPdf, namePdf, customFontManual, Color.BLACK, 12, 475, startY + 35, 25);
            // Duration
            String durationPdf = language ? "Thời gian thi: " + exam.get().getDuration()+ " phút" : "Duration: " + exam.get().getDuration() + " minutes";
            drawContent(contentStream, durationPdf, customFontManual, Color.BLACK, 11, 25, startY - 15);
            // Set the positions and dimensions
            String pointHeader = language ? TOTAL_POINT_CONTENT_VI : TOTAL_POINT_CONTENT_EN;
            String commentHeader = language ? COMMENT_CONTENT_VI : COMMENT_CONTENT_EN;
            drawTableOfPointAndComment(contentStream, pointHeader, commentHeader, 380, page.getMediaBox().getWidth() - 25, 720, 15, customFontManual, 12);

            int startYUpParent = 170;
            int questionNo = 0;
            int maxLinesPerPage = 45; // Số dòng tối đa trên một trang
            int remainingLines = maxLinesPerPage;

            for (Question question : questions) {
                String questionText = language ? "Câu " + (++questionNo) + ": " + question.getQuestion() : "Question " + (++questionNo) + ": " + question.getQuestion();
                String questionType = language ? ((question.getQuestionType() == QuestionType.MULTI ? " (Câu hỏi chọn nhiều đáp án)" : " (Câu hỏi chọn một đáp án)")) : ((question.getQuestionType() == QuestionType.MULTI ? " (Multiple choices question)" : " (Single choice question)"));
                questionText += questionType;

                if (startY - startYUpParent - 30 < borderY) {
                    // Tạo trang mới nếu dòng hiện tại vượt quá giới hạn dưới cùng
                    page = new PDPage(PDRectangle.A4);
                    document.addPage(page);
                    contentStream.close();
                    contentStream = new PDPageContentStream(document, page);
                    drawPageNumber(contentStream, ++pageNo, customFontManual, 11, 25, 13);
                    drawFooterContent(contentStream, FOOTER_PDF_VI, FOOTER_PDF_EN, customFontBoldItalic, language, Color.RED, Color.BLACK, 10, 200, 13);
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
                List<String> lines = splitTextIntoLines(questionText, customFontBold, 13, 590);

                for (String line : lines) {
                    if (remainingLines <= 0) {
                        // Tạo trang mới nếu không đủ không gian
                        page = new PDPage(PDRectangle.A4);
                        document.addPage(page);
                        contentStream.close();
                        contentStream = new PDPageContentStream(document, page);
                        drawPageNumber(contentStream, ++pageNo, customFontManual, 11, 25, 13);
                        drawFooterContent(contentStream, FOOTER_PDF_VI, FOOTER_PDF_EN, customFontBoldItalic, language, Color.RED, Color.BLACK, 10, 200, 13);
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
                            drawPageNumber(contentStream, ++pageNo, customFontManual, 11, 25, 13);
                            drawFooterContent(contentStream, FOOTER_PDF_VI, FOOTER_PDF_EN, customFontBoldItalic, language, Color.RED, Color.BLACK, 10, 200, 13);
                            startY = 950;
                            startYUpParent = 170;
                            remainingLines = maxLinesPerPage;
                        }

                        if (startY - startYUpParent < borderY) {
                            // Chuyển xuống dòng tiếp theo nếu dòng hiện tại vượt quá giới hạn trên cùng
                            startYUpParent = 20;
                            remainingLines--;
                        }

                        List<String> answerLines = splitTextIntoLines(answerText, customFontManual, 12, 545);

                        for (String line : answerLines) {
                            if (remainingLines <= 0) {
                                // Tạo trang mới nếu không đủ không gian
                                page = new PDPage(PDRectangle.A4);
                                document.addPage(page);
                                contentStream.close();
                                contentStream = new PDPageContentStream(document, page);
                                drawPageNumber(contentStream, ++pageNo, customFontManual, 11, 25, 13);
                                drawFooterContent(contentStream, FOOTER_PDF_VI, FOOTER_PDF_EN, customFontBoldItalic, language, Color.RED, Color.BLACK, 10, 200, 13);
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
            drawPageNumber(contentStream, ++pageNo, customFontManual, 11, 25, 13);

            // Correct answers
            drawFooterContent(contentStream, FOOTER_PDF_VI, FOOTER_PDF_EN, customFontBoldItalic, language, Color.RED, Color.BLACK, 10, 200, 13);

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
                var incrementedIndexAnswer  = question.getCorrectAnswers().stream().map(n -> n+1L).collect(Collectors.toList());
                var correctAnswer = incrementedIndexAnswer.toString().replaceAll("\\[|\\]", "");

                String dataColumn1 = (language ? "Câu " + ++questionNoAnswer : "Question " + ++questionNoAnswer); // Replace with your logic to get data for column 1
                String dataColumn2 = (language ? "Đáp án: " + correctAnswer : "Correct answers: " + correctAnswer); // Replace with your logic to get data for column 2

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
                    drawPageNumber(contentStream, ++pageNo, customFontManual, 11, 297, 13);
                    startYColumn = borderHeight - 20;
                }
            }

            contentStream.close();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            document.save(baos);
            document.close();

            if (baos.size() > 0) {
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION,
                                "attachment;  filename=\""+exam.get().getExamName()+".pdf\"; charset=UTF-8")
                        .contentType(MediaType.APPLICATION_PDF)
                        .body(baos.toByteArray());
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(CommonResponse.builder()
                        .body(Map.of("message", "Cannot convert data to pdf!!"))
                        .build()
                        .getBody());
            }

        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(CommonResponse.builder()
                    .body(Map.of("message", "Something error!!"))
                    .build()
                    .getBody());
        }
    }


    private void drawTableOfPointAndComment(PDPageContentStream contentStream,
                                            String pointHeader,
                                            String commentHeader,
                                            float leftMargin,
                                            float rightMargin,
                                            float startYTable,
                                            float cellHeight,
                                            PDFont customFont,
                                            float fontSize
                                            ) throws IOException{
        // Set the positions and dimensions

        float cellWidth = (rightMargin - leftMargin) / 2;
        float rowHeightTable = cellHeight * 4;

        // Draw the table border
        float tableX = leftMargin;
        float tableY = startYTable;
        float tableWidth = cellWidth * 2;
        float tableHeight = rowHeightTable + cellHeight;
        contentStream.setLineWidth(1);
        contentStream.addRect(tableX, tableY, tableWidth, tableHeight);
        contentStream.stroke();

        // Draw a line for column separation
        float separationX = leftMargin + cellWidth;
        float separationYStart = tableY;
        float separationYEnd = tableY + tableHeight;
        contentStream.moveTo(separationX, separationYStart);
        contentStream.lineTo(separationX, separationYEnd);
        contentStream.stroke();

        // Draw a line for "Score" column
        float scoreX = leftMargin;
        float scoreYStart = tableY + rowHeightTable;
        contentStream.moveTo(scoreX, scoreYStart);
        contentStream.lineTo(scoreX + cellWidth, scoreYStart);
        contentStream.stroke();
        contentStream.beginText();
        contentStream.setFont(customFont, fontSize);
        contentStream.newLineAtOffset(scoreX + 15, scoreYStart + 2);
        contentStream.showText(pointHeader);
        contentStream.endText();

        // Draw a line for "Comments" column
        float commentsX = leftMargin + cellWidth;
        float commentsYStart = tableY + rowHeightTable;
        contentStream.moveTo(commentsX, commentsYStart);
        contentStream.lineTo(commentsX + cellWidth, commentsYStart);
        contentStream.stroke();
        contentStream.beginText();
        contentStream.setFont(customFont, fontSize);
        contentStream.newLineAtOffset(commentsX + 15, commentsYStart + 2);
        contentStream.showText(commentHeader);
        contentStream.endText();
    }

    private void drawContent(PDPageContentStream contentStream,
                             String content,
                             PDFont customFont,
                             Color textColor,
                             float fontSize,
                             float x,
                             float y) throws IOException {
        contentStream.beginText();
        contentStream.setFont(customFont, fontSize);
        contentStream.setNonStrokingColor(textColor);
        contentStream.newLineAtOffset(x, y);
        contentStream.showText(content);
        contentStream.endText();
    }

    private void drawContentInline(PDPageContentStream contentStream,
                             String contentLeft,
                             String contentRight,
                             PDFont customFont,
                             Color textColor,
                             float fontSize,
                             float xRight,
                             float yRight,
                             float xLeft) throws IOException {
        contentStream.beginText();
        contentStream.setFont(customFont, fontSize);
        contentStream.setNonStrokingColor(textColor);
        contentStream.newLineAtOffset(xRight, yRight);
        contentStream.showText(contentLeft);
        contentStream.newLineAtOffset(xLeft - xRight , 0);
        contentStream.showText(contentRight);
        contentStream.endText();
    }

    private void drawPageNumber(PDPageContentStream contentStream,
                                int pageNo,
                                PDFont customFont,
                                float fontSize,
                                float x,
                                float y) throws IOException {
        contentStream.beginText();
        contentStream.setFont(customFont, fontSize);
        contentStream.newLineAtOffset(x, y);
        contentStream.showText(String.valueOf(pageNo));
        contentStream.endText();
    }

    private void drawFooterContent(PDPageContentStream contentStream,
                                   String contentVn,
                                   String contentEn,
                                   PDFont customFont,
                                   Boolean languageFlag,
                                   Color textColor,
                                   Color textColorReset,
                                   float fontSize,
                                   float x,
                                   float y) throws IOException {
        contentStream.beginText();
        contentStream.setFont(customFont, fontSize);
        contentStream.setNonStrokingColor(textColor);
        contentStream.newLineAtOffset(x, y);
        contentStream.showText(languageFlag ? contentVn : contentEn);
        contentStream.setNonStrokingColor(textColorReset);
        contentStream.endText();
    }
}
