package com.rbrcloud.portfoliosrvc.service;

import com.rbrcloud.portfoliosrvc.entity.Stock;
import com.rbrcloud.portfoliosrvc.repository.PortfolioRepository;
import com.rbrcloud.shared.constants.KafkaTopics;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PortfolioService {

    private final KafkaProducerService kafkaProducerService;

    private final PortfolioRepository portfolioRepository;

    @Autowired
    public PortfolioService(KafkaProducerService kafkaProducerService,
                            PortfolioRepository portfolioRepository) {
        this.kafkaProducerService = kafkaProducerService;
        this.portfolioRepository = portfolioRepository;
    }

    public Stock addStockToPortfolio(Stock stock) {
        if (stock.getQuantity() < 1) {
            throw new IllegalArgumentException("Stock quantity must be at least 1");
        }
        Stock savedStock = portfolioRepository.save(stock);

        // Publish this save event to kafka
        String message = "Stock added to portfolio: " + savedStock.getSymbol();
        kafkaProducerService.sendMessage(KafkaTopics.PORTFOLIO_UPDATE, message);

        return savedStock;
    }

    public List<Stock> getStocksFromPortfolio() {
        return portfolioRepository.findAll();
    }
}
