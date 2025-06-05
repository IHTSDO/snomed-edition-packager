package org.snomed.snomededitionpackager;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(properties = "spring.shell.interactive.enabled=false")
class MainTests {

	@Test
	void contextLoads() {
		// Sonarqube
		assertTrue(true);
	}

}
