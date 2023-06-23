package com.example.file.jfileservicedoonlineexam.service;

import com.example.file.jfileservicedoonlineexam.command.UpdateImageRequest;
import com.example.file.jfileservicedoonlineexam.command.UploadRequest;
import com.example.file.jfileservicedoonlineexam.common.UUIDHandler;
import com.example.file.jfileservicedoonlineexam.constants.AllConstants;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class FileService {

	private static final Path CURRENT_FOLDER = Paths.get(System.getProperty("user.dir"));

	@Value("${application.resource-url}")
	private String RESOURCE_URL_ROOT;

	public String saveFile(UploadRequest req) throws IOException {
		Path staticPath = Paths.get(AllConstants.STATIC_FOLDER_PATH);
		Path imagePath = Paths.get(req.getFileType() + req.getDomain());
		if (!Files.exists(CURRENT_FOLDER.resolve(staticPath).resolve(imagePath))) {
			Files.createDirectories(CURRENT_FOLDER.resolve(staticPath).resolve(imagePath));
		}

		Path file =
				CURRENT_FOLDER
						.resolve(staticPath)
						.resolve(imagePath)
						.resolve(
								UUIDHandler.generateTypeUUID()
										+ AllConstants.DOT_CHARACTER
										+ getExtensionFile(req.getFile().getOriginalFilename()));
		try (OutputStream os = Files.newOutputStream(file)) {
			os.write(req.getFile().getBytes());
		}

		return RESOURCE_URL_ROOT
				+ AllConstants.STROKE_CHARACTER
				+ req.getFileType()
				+ req.getDomain()
				+ AllConstants.STROKE_CHARACTER
				+ file.getFileName();
	}

	public String getMultipleFileContentType(String fileName) throws FileNotFoundException {
		return switch (getExtensionFile(fileName)) {
			case AllConstants.PNG_KEY -> AllConstants.IMAGE_PNG_VALUE;
			case AllConstants.JPEG_KEY -> AllConstants.IMAGE_JPEG_VALUE;
			case AllConstants.GIF_KEY -> AllConstants.IMAGE_GIF_VALUE;
			case AllConstants.JPG_KEY -> AllConstants.IMAGE_JPG_VALUE;
			default -> AllConstants.EMPTY_STRING;
		};
	}

	public String getExtensionFile(String fileName) throws FileNotFoundException {
		if (Objects.equals(fileName, AllConstants.EMPTY_STRING)) {
			throw new FileNotFoundException("Not found file");
		}

		return fileName.substring(fileName.lastIndexOf(AllConstants.DOT_CHARACTER) + 1);
	}

	public String updateImage(UpdateImageRequest req) throws IOException {
		removeFile(req.getOldImagePath(), AllConstants.IMAGE_FOLDER_TYPE);
		UploadRequest uploadRequest =
				new UploadRequest(req.getDomain(), AllConstants.IMAGE_FOLDER_TYPE, req.getFile());
		return saveFile(uploadRequest);
	}

	private void removeFile(String filename, String fileType)
			throws IOException, InvalidPathException {
		int count = 0;
		String domain = null, img = null;
		String[] arr = filename.split(AllConstants.STROKE_CHARACTER);
		for (var i = arr.length - 1; count < 2; i--) {
			if (count == 0) {
				img = arr[i];
			} else {
				domain = arr[i];
			}
			count++;
		}

		Path imagesPath =
				Paths.get(AllConstants.STATIC_FOLDER_PATH + fileType + AllConstants.STROKE_CHARACTER);
		Path file = imagesPath.resolve(domain + AllConstants.STROKE_CHARACTER + img);
		Files.deleteIfExists(file);
	}
}
