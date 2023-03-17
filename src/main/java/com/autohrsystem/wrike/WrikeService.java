package com.autohrsystem.wrike;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class WrikeService {

	private final String token;

	@Setter
	@Getter
	public static class WrikeResponse {
		String kind;
		List<Map<String, Object>> data;

		public String getTaskId() {
			return (String) data.get(0).get("id");
		}
	}

	@Autowired
	public WrikeService(@Value("${wrike.token}") String tokenPath) {
		this.token = readToken(Path.of(tokenPath));
	}

	public WrikeResponse createTask(String title, String contents) {
		String folderId = "IEAAOKQMI46CCVUM";
		String url = String.format("https://www.wrike.com/api/v4/folders/%s/tasks", folderId);

		UriComponents build = UriComponentsBuilder.fromUriString(url)
			.queryParam("title", title)
			.queryParam("description", contents)
			.build();

		RequestEntity<Void> requestEntity = RequestEntity
			.method(HttpMethod.POST, build.toUriString())
			.header("Authorization", String.format("bearer %s", token))
			.build();
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<WrikeResponse> response =
			restTemplate.exchange(requestEntity, WrikeResponse.class);
		return response.getBody();
	}

	public WrikeResponse uploadAttachment(String taskId, List<File> images) {
		UriComponents build = UriComponentsBuilder.fromUriString(
				"https://www.wrike.com/api/v4//tasks/{taskId}/attachments")
			.buildAndExpand(Map.of("taskId", taskId));

		MultiValueMap<String, Object> requestBody = new LinkedMultiValueMap<>();
		images.forEach(image -> requestBody.add("files", new FileSystemResource(image)));

		RequestEntity<MultiValueMap<String, Object>> entity =
			RequestEntity.post(build.toUriString())
				.header("Authorization", String.format("bearer %s", token))
				.contentType(MediaType.MULTIPART_FORM_DATA)
				.body(requestBody);

		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<WrikeResponse> response = restTemplate.exchange(entity, WrikeResponse.class);
		return response.getBody();
	}

	@SneakyThrows
	private String readToken(Path tokenPath) {
		return Files.readAllLines(tokenPath)
			.stream().collect(Collectors.joining());
	}
}
