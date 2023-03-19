package com.autohrsystem.wrike;

import com.autohrsystem.wrike.WrikeService.WrikeResponse;
import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import lombok.SneakyThrows;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@Import(WrikeService.class)
@TestPropertySource(properties = "wrike.token=/ePapyrus/tmp/wrike-token.txt")
class WrikeServiceTest {

	@Autowired
	private WrikeService wrikeService;

	@Test
	@Disabled
	public void createTask() throws ExecutionException, InterruptedException {
		ClassPathResource errorPng = new ClassPathResource("/error.png");
		WrikeResponse finalResponse =
			CompletableFuture.supplyAsync(() -> wrikeService.createTask("test", "this is test"))
			.thenApply(wrikeResponse ->
				wrikeService.uploadAttachment(wrikeResponse.getTaskId(), toFiles(errorPng)))
			.get();
		Assertions.assertThat(finalResponse).isNotNull();
	}

	@SneakyThrows
	private List<File> toFiles(ClassPathResource resource) {
		return  List.of(resource.getFile());
	}
}