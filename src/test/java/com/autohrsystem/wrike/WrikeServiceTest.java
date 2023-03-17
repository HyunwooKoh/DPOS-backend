package com.autohrsystem.wrike;

import java.util.Map;
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
	public void createTask() {
		Map<String, Object> response = wrikeService.createTask("test", "this is test");
		Assertions.assertThat(response).isNotEmpty();
	}
}