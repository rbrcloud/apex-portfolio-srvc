package com.rbrcloud.portfoliosrvc.controller;

import com.rbrcloud.portfoliosrvc.entity.Stock;
import com.rbrcloud.portfoliosrvc.service.PortfolioService;
import com.rbrcloud.shared.dto.StockPriceDTO;
import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/portfolios")
public class PortfolioController {

    private final PortfolioService portfolioService;

    @Autowired
    public PortfolioController(PortfolioService portfolioService) {
        this.portfolioService = portfolioService;
    }

    @PostMapping("{portfolio_id}/stocks")
    public ResponseEntity<Stock> addStock(@RequestBody Stock stock) {
        Stock newStock = portfolioService.addStockToPortfolio(stock);
        return new ResponseEntity<>(newStock, HttpStatus.CREATED);
    }

    @GetMapping("{portfolio_id}/stocks")
    public ResponseEntity<List<Stock>> getAllStocks() {
        return ResponseEntity.ok(portfolioService.getStocksFromPortfolio());
    }
}
