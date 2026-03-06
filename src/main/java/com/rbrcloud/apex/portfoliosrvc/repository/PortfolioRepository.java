package com.rbrcloud.apex.portfoliosrvc.repository;

import com.rbrcloud.apex.portfoliosrvc.entity.Portfolio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PortfolioRepository extends JpaRepository<Portfolio, Long> {

    List<Portfolio> findByUserId(Long userId);

    @Query("SELECT p FROM Portfolio p WHERE p.userId = :userId AND p.isDefault = true")
    Portfolio findDefaultByUserId(Long userId);
}
