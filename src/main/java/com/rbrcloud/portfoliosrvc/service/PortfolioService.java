package com.rbrcloud.portfoliosrvc.service;

import com.rbrcloud.portfoliosrvc.entity.Portfolio;
import com.rbrcloud.portfoliosrvc.repository.PortfolioRepository;
import jakarta.annotation.Nonnull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PortfolioService {

    private final PortfolioRepository portfolioRepository;

    @Autowired
    public PortfolioService(PortfolioRepository portfolioRepository) {
        this.portfolioRepository = portfolioRepository;
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
}
