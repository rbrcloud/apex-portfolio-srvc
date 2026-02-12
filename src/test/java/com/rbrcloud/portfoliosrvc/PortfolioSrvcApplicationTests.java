package com.rbrcloud.portfoliosrvc;

import com.rbrcloud.portfoliosrvc.service.PortfolioService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class PortfolioSrvcApplicationTests {

	@InjectMocks
	private PortfolioService portfolioService;

	@Test
	void contextLoads() {
	}

}
