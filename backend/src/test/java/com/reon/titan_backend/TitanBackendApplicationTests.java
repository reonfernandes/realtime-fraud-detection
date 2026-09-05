package com.reon.titan_backend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

// index creation needs a running mongo, tests do not have one
@SpringBootTest
@TestPropertySource(properties = "spring.data.mongodb.auto-index-creation=false")
class TitanBackendApplicationTests {

	@Test
	void contextLoads() {
	}

}
