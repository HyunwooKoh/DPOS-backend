package com.autohrsystem.wrike;

import com.autohrsystem.wrike.WrikeService.WrikeResponse;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@Import(WrikeService.class)
@TestPropertySource(properties = "wrike.token=/ePapyrus/tmp/wrike-token.txt")
class WrikeServiceTest {

	@Autowired
	private WrikeService wrikeService;

	@Test
	public void createTask() throws ExecutionException, InterruptedException {
		WrikeResponse finalResponse =
			CompletableFuture.supplyAsync(() -> wrikeService.createTask("test", "this is test"))
			.thenApply(wrikeResponse -> wrikeService.uploadAttachment(wrikeResponse.getTaskId()))
			.get();
		Assertions.assertThat(finalResponse).isNotNull();
	}
}