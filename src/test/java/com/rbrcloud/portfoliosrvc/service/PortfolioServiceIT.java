package com.rbrcloud.portfoliosrvc.service;

import com.rbrcloud.portfoliosrvc.entity.Portfolio;
import com.rbrcloud.portfoliosrvc.repository.PortfolioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class PortfolioServiceIT {

    @MockitoBean
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    private PortfolioService portfolioService;

    @Autowired
    private PortfolioRepository portfolioRepository;

    @BeforeEach
    public void setup() {
        portfolioRepository.deleteAll();
    }

    @Test
    public void addStockToPortfolio() {
        Portfolio portfolio = Portfolio.builder().userId(1001L).name("AAPL").build();
        Portfolio savedPortfolio = portfolioService.createPortfolio(portfolio);

        assertNotNull(savedPortfolio.getId());
        assertEquals(portfolio.getName(), savedPortfolio.getName());

        // Verify in DB
        List<Portfolio> portfolios = portfolioRepository.findAll();
        assertEquals(1, portfolios.size());
        assertEquals(portfolio.getName(), portfolios.getFirst().getName());
    }
}
