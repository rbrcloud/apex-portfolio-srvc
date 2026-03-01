package com.rbrcloud.portfoliosrvc.repository;

import com.rbrcloud.portfoliosrvc.entity.OwnedStock;
import com.rbrcloud.portfoliosrvc.entity.Portfolio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OwnedStockRepository extends JpaRepository<OwnedStock, Long> {

    List<Portfolio> findByUserId(Long userId);

    @Query("SELECT os FROM OwnedStock os WHERE os.userId = :userId AND os.ticker = :ticker")
    List<OwnedStock> findByUserIdAndTicker(Long userId, String ticker);
}
