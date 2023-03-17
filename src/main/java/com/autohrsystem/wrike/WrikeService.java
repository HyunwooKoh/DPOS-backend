package com.autohrsystem.wrike;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class WrikeService {

	private final String token;

	@Autowired
	public WrikeService(@Value("${wrike.token}") String tokenPath) {
		this.token = readToken(Path.of(tokenPath));
	}

	public Map<String, Object> createTask(String title, String contents) {
		String folderId = "IEAAOKQMI46CCVUM";
		String url = String.format("https://www.wrike.com/api/v4/folders/%s/tasks", folderId);

		UriComponents build = UriComponentsBuilder.fromUriString(url)
			.queryParam("title", title)
			.queryParam( "description", contents)
			.build();

		RequestEntity<Void> requestEntity = RequestEntity
			.method(HttpMethod.POST, build.toUriString())
			.header("Authorization", String.format("bearer %s", token))
			.build();
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<Map<String, Object>> response =
			restTemplate.exchange(requestEntity, new ParameterizedTypeReference<>() {});
		return response.getBody();
	}

	@SneakyThrows
	private String readToken(Path tokenPath) {
		return Files.readAllLines(tokenPath)
			.stream().collect(Collectors.joining());
	}
}
