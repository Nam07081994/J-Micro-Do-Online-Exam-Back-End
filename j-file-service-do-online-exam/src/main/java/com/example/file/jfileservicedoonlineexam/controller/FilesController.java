package com.example.file.jfileservicedoonlineexam.controller;

import com.example.file.jfileservicedoonlineexam.command.UpdateImageRequest;
import com.example.file.jfileservicedoonlineexam.command.UploadRequest;
import com.example.file.jfileservicedoonlineexam.service.FileService;
import com.example.file.jfileservicedoonlineexam.constants.AllConstants;
import jakarta.validation.Valid;
import java.io.IOException;
import lombok.AllArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("api/v1/files")
public class FilesController {
	private final FileService fileService;

	@GetMapping("/images/{domain}/{imageName}")
	public ResponseEntity<?> getImage(@PathVariable String domain, @PathVariable String imageName)
			throws IOException {
		Resource file =
				new FileSystemResource(
						AllConstants.STATIC_FOLDER_PATH
								+ AllConstants.IMAGE_FOLDER_TYPE
								+ domain
								+ AllConstants.STROKE_CHARACTER
								+ imageName);

		HttpHeaders headers = new HttpHeaders();
		String mediaType = fileService.getMultipleFileContentType(imageName);
		headers.setContentType(MediaType.valueOf(mediaType));
		headers.setContentLength(file.contentLength());

		return new ResponseEntity<>(file, headers, HttpStatus.OK);
	}

	@PostMapping("/image/updateImage")
	public ResponseEntity<?> updateImage(@ModelAttribute @Valid UpdateImageRequest req)
			throws IOException {
		return new ResponseEntity<>(fileService.updateImage(req), HttpStatus.OK);
	}

	@PostMapping
	public ResponseEntity<?> saveFile(@ModelAttribute @Valid UploadRequest req) throws IOException {
		return new ResponseEntity<>(fileService.saveFile(req), HttpStatus.OK);
	}
}
