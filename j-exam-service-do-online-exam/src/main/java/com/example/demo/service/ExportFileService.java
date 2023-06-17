package com.example.demo.service;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.springframework.stereotype.Service;

@Service
public class ExportFileService {

	public void drawTableOfPointAndComment(
			PDPageContentStream contentStream,
			String pointHeader,
			String commentHeader,
			float leftMargin,
			float rightMargin,
			float startYTable,
			float cellHeight,
			PDFont customFont,
			float fontSize)
			throws IOException {
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

	public void drawContent(
			PDPageContentStream contentStream,
			String content,
			PDFont customFont,
			Color textColor,
			float fontSize,
			float x,
			float y)
			throws IOException {
		contentStream.beginText();
		contentStream.setFont(customFont, fontSize);
		contentStream.setNonStrokingColor(textColor);
		contentStream.newLineAtOffset(x, y);
		contentStream.showText(content);
		contentStream.endText();
	}

	public void drawContentInline(
			PDPageContentStream contentStream,
			String contentLeft,
			String contentRight,
			PDFont customFont,
			Color textColor,
			float fontSize,
			float xRight,
			float yRight,
			float xLeft)
			throws IOException {
		contentStream.beginText();
		contentStream.setFont(customFont, fontSize);
		contentStream.setNonStrokingColor(textColor);
		contentStream.newLineAtOffset(xRight, yRight);
		contentStream.showText(contentLeft);
		contentStream.newLineAtOffset(xLeft - xRight, 0);
		contentStream.showText(contentRight);
		contentStream.endText();
	}

	public void drawPageNumber(
			PDPageContentStream contentStream,
			int pageNo,
			PDFont customFont,
			float fontSize,
			float x,
			float y)
			throws IOException {
		contentStream.beginText();
		contentStream.setFont(customFont, fontSize);
		contentStream.newLineAtOffset(x, y);
		contentStream.showText(String.valueOf(pageNo));
		contentStream.endText();
	}

	public void drawFooterContent(
			PDPageContentStream contentStream,
			String contentVn,
			String contentEn,
			PDFont customFont,
			Boolean languageFlag,
			Color textColor,
			Color textColorReset,
			float fontSize,
			float x,
			float y)
			throws IOException {
		contentStream.beginText();
		contentStream.setFont(customFont, fontSize);
		contentStream.setNonStrokingColor(textColor);
		contentStream.newLineAtOffset(x, y);
		contentStream.showText(languageFlag ? contentVn : contentEn);
		contentStream.setNonStrokingColor(textColorReset);
		contentStream.endText();
	}

	public java.util.List<String> splitTextIntoLines(
			String text, PDFont font, int fontSize, int maxWidth) throws IOException {
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

	public boolean containsVietnameseChar(String text) {
		for (int i = 0; i < text.length(); i++) {
			char c = text.charAt(i);
			if (Character.UnicodeBlock.of(c) == Character.UnicodeBlock.LATIN_EXTENDED_ADDITIONAL) {
				return true;
			}
		}
		return false;
	}
}
