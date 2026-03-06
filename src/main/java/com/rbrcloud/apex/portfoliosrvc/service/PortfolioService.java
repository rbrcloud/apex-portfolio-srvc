package com.rbrcloud.apex.portfoliosrvc.service;

import com.rbrcloud.apex.orderexecution.dto.OrderExecutedEvent;
import com.rbrcloud.apex.portfoliosrvc.entity.OwnedStock;
import com.rbrcloud.apex.portfoliosrvc.entity.Portfolio;
import com.rbrcloud.apex.portfoliosrvc.repository.OwnedStockRepository;
import com.rbrcloud.apex.portfoliosrvc.repository.PortfolioRepository;
import jakarta.annotation.Nonnull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.rbrcloud.apex.ordersrvc.enums.OrderSide.BUY;

@Service
@Slf4j
public class PortfolioService {

    private final PortfolioRepository portfolioRepository;

    private final OwnedStockRepository ownedStockRepository;

    private static final String ORDER_EXECUTED_TOPIC = "order.executed.event";

    @Autowired
    public PortfolioService(PortfolioRepository portfolioRepository, OwnedStockRepository ownedStockRepository) {
        this.portfolioRepository = portfolioRepository;
        this.ownedStockRepository = ownedStockRepository;
    }

    @Transactional
    public Portfolio createPortfolio(@Nonnull Portfolio portfolio) {
        // Make this the default portfolio if it's the first one for the user
        List<Portfolio> existingPortfolios = portfolioRepository.findByUserId(portfolio.getUserId());
        if (existingPortfolios.isEmpty()) {
            portfolio.setIsDefault(true);
        }
        return portfolioRepository.save(portfolio);
    }

    public List<Portfolio> getAllPortfolios() {
        return portfolioRepository.findAll();
    }

    public List<Portfolio> getPortfoliosByUserId(@Nonnull Long userId) {
        return portfolioRepository.findByUserId(userId);
    }

    @Transactional
    @KafkaListener(topics = ORDER_EXECUTED_TOPIC, groupId = "execution-group")
    public void consumeOrderExecutedEvent(OrderExecutedEvent orderExecutedEvent) {
        log.info("Received OrderExecutedEvent: Order Id: {}, Ticker: {}, Price: {}, User Id: {}, Quantity: {}, Order Side: {}",
                orderExecutedEvent.getOrderId(), orderExecutedEvent.getTicker(), orderExecutedEvent.getPrice(),
                orderExecutedEvent.getUserId(), orderExecutedEvent.getQuantity(), orderExecutedEvent.getOrderSide());

        // Get the default portfolio for the user
        Long defaultPortfolioId = portfolioRepository.findDefaultByUserId(orderExecutedEvent.getUserId()).getUserId();
        log.info("Default portfolio id for user {} is {}.", orderExecutedEvent.getUserId(), defaultPortfolioId);

        // Check if the user already owns the stock, if so update quantity and price, otherwise add new entry
        OwnedStock ownedStock = ownedStockRepository
                .findByUserIdAndTicker(orderExecutedEvent.getUserId(), orderExecutedEvent.getTicker())
                .stream()
                .findFirst()
                .orElse(null);
        if (ownedStock == null) {
            ownedStock = OwnedStock.builder()
                    .ticker(orderExecutedEvent.getTicker())
                    .quantity(orderExecutedEvent.getQuantity())
                    .price(orderExecutedEvent.getPrice())
                    .portfolioId(defaultPortfolioId)
                    .userId(orderExecutedEvent.getUserId())
                    .build();
        } else {
            int newQuantity = orderExecutedEvent.getOrderSide() == BUY
                    ? ownedStock.getQuantity() + orderExecutedEvent.getQuantity()
                    : ownedStock.getQuantity() - orderExecutedEvent.getQuantity();
            ownedStock.setQuantity(newQuantity);
        }

        ownedStockRepository.save(ownedStock);
        log.info("Added stock {} to portfolio {} for user {}.", ownedStock.getTicker(), defaultPortfolioId, ownedStock.getUserId());
    }
}
