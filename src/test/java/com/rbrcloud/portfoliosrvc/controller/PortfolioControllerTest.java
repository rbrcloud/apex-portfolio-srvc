package com.rbrcloud.portfoliosrvc.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rbrcloud.portfoliosrvc.entity.Stock;
import com.rbrcloud.portfoliosrvc.service.PortfolioService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PortfolioController.class)
public class PortfolioControllerTest {

    @Autowired
    private MockMvc mockMvc;            // To simulate HTTP requests

    @Autowired
    private ObjectMapper objectMapper;  // To handle JSON conversion

    @MockitoBean
    private PortfolioService portfolioService;

    @Test
    public void validRequest() throws Exception {
        Stock stock = new Stock(null, "JPM", 25, new BigDecimal("322.50"));

        Stock savedStock = new Stock(1L, "JPM", 25, new BigDecimal("322.50"));
        when(portfolioService.addStockToPortfolio(any(Stock.class))).thenReturn(savedStock);

        mockMvc.perform(
                post("/api/v1/portfolios/1/stocks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(stock))
                        .with(user("testuser").roles("USER"))
                        .with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.symbol").value("JPM"));
    }
}
