package com.rbrcloud.portfoliosrvc.repository;

import com.rbrcloud.portfoliosrvc.entity.WatchedStock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WatchedStockRepository extends JpaRepository<WatchedStock, Long> {

    List<WatchedStock> findByPortfolioId(Long portfolioId);
}
