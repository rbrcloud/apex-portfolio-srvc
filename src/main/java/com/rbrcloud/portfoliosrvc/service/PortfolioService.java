package com.rbrcloud.portfoliosrvc.service;

import com.rbrcloud.portfoliosrvc.entity.Portfolio;
import com.rbrcloud.portfoliosrvc.repository.PortfolioRepository;
import jakarta.annotation.Nonnull;
import org.springframework.beans.factory.annotation.Autowired;
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
        return portfolioRepository.save(portfolio);
    }

    public List<Portfolio> getPortfoliosByUserId(@Nonnull Long userId) {
        return portfolioRepository.findByUserId(userId);
    }
}
