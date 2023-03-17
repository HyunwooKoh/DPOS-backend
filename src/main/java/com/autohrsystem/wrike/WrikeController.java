package com.autohrsystem.wrike;

import com.autohrsystem.common.CommonApi;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

import com.autohrsystem.controller.Dto.JobDto;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class WrikeController {

	@Autowired
	WrikeService wrikeService;

	@RequestMapping(value = "/issue/help", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
	public ResponseEntity<String> issue(JobDto.JobRequestForm dto) {
		List<File> images = dto.getFiles().stream()
			.map(this::transferTo)
			.collect(Collectors.toList());

		return ResponseEntity.ok(wrikeService.issue(images, dto.getReqType()).toASCIIString());
//		String uuid = CommonApi.generateUuid();
//		String inputFilePath = CommonApi.getTempDir(uuid) + "origin" + ext;
//		String outputFilePath = CommonApi.getTempDir(uuid) + "result.json";
//		//FileHandler fileHandler = new FileHandler(uuid, ext, inputFilePath, outputFilePath);
//		OcrParams param = new OcrParams(inputFilePath, outputFilePath, env.getProperty("OCR_SERVER_URL"))
	}

	@SneakyThrows
	private File transferTo(MultipartFile multipartFile) {
		String uuid = CommonApi.generateUuid();
		String fileName = multipartFile.getOriginalFilename();
		String ext = fileName.substring(fileName.lastIndexOf('.'));
		File input = new File(CommonApi.getAndCreateTempDir(uuid) + "origin" + ext);
		multipartFile.transferTo(input);
		return input;
	}

}
