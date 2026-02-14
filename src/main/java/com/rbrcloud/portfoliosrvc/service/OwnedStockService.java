package com.rbrcloud.portfoliosrvc.service;

import com.rbrcloud.portfoliosrvc.entity.OwnedStock;
import com.rbrcloud.portfoliosrvc.repository.OwnedStockRepository;
import com.rbrcloud.shared.constants.KafkaTopics;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class OwnedStockService {

    private final KafkaProducerService kafkaProducerService;

    private final OwnedStockRepository portfolioStockRepository;

    @Autowired
    public OwnedStockService(KafkaProducerService kafkaProducerService,
                             OwnedStockRepository portfolioStockRepository) {
        this.kafkaProducerService = kafkaProducerService;
        this.portfolioStockRepository = portfolioStockRepository;
    }

    /**
     * Get all stocks from the portfolio
     */
    public List<OwnedStock> getStocksFromPortfolio() {
        return portfolioStockRepository.findAll();
    }

    @Transactional
    public OwnedStock addStockToPortfolio(OwnedStock stock) {
        if (stock.getQuantity() < 1) {
            throw new IllegalArgumentException("Stock quantity must be at least 1");
        }
        OwnedStock savedStock = portfolioStockRepository.save(stock);

        // Publish this save event to kafka
        String message = "Stock added to portfolio: " + savedStock.getTicker();
        kafkaProducerService.sendMessage(KafkaTopics.PORTFOLIO_UPDATE, message);

        return savedStock;
    }

    @Transactional
    public void updatePortfolio(Long stockId, int quantity) {
        OwnedStock stock = portfolioStockRepository.findById(stockId)
                .orElseThrow(() -> new ResourceNotFoundException("Portfolio doesn't have the stock with id: " + stockId));

        int newQuantity = stock.getQuantity() + quantity;
        if (newQuantity == 0) {                 // Sold all the stocks
            deleteStockFromPortFolio(stock);
        } else if (newQuantity < 0) {           // Selling invalid amount of stocks
            throw new IllegalArgumentException(
                    String.format("Insufficient quantity. Owned: %s, Requested: %s", stock.getQuantity(), quantity));
        } else {                                // Valid quantity. Update the portfolio
            stock.setQuantity(newQuantity);
            portfolioStockRepository.save(stock);
        }

    }

    public void deleteStockFromPortFolio(OwnedStock stock) {
        portfolioStockRepository.delete(stock);
    }
}
