package com.rbrcloud.portfoliosrvc.repository;

import com.rbrcloud.portfoliosrvc.entity.Portfolio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PortfolioRepository extends JpaRepository<Portfolio, Long> {
}
