package com.rbrcloud.portfoliosrvc.repository;

import com.rbrcloud.portfoliosrvc.entity.OwnedStock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OwnedStockRepository extends JpaRepository<OwnedStock, Long> {

    List<OwnedStock> findByPortfolioId(Long portfolioID);
}
