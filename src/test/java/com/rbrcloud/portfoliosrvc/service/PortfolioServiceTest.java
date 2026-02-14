package com.rbrcloud.portfoliosrvc.service;

import com.rbrcloud.portfoliosrvc.entity.Portfolio;
import com.rbrcloud.portfoliosrvc.repository.PortfolioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PortfolioServiceTest {

    @Mock
    private PortfolioRepository portfolioRepository;

    @Mock
    private KafkaProducerService kafkaProducerService;

    @InjectMocks
    private PortfolioService portfolioService;

    @Test
    public void addPortfolio() {
        // Arrange
        Portfolio portfolio = new Portfolio(null, 1001L, "Alex");
        when(portfolioRepository.save(any(Portfolio.class))).thenReturn(portfolio);

        // Act
        Portfolio savedPortfolio = portfolioService.createPortfolio(portfolio);

        // Assert
        assertNotNull(savedPortfolio);
        assertEquals(portfolio.getName(), savedPortfolio.getName());
        verify(portfolioRepository, times(1)).save(portfolio);
    }
}
