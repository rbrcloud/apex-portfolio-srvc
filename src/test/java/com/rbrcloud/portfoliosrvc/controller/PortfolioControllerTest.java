package com.rbrcloud.portfoliosrvc.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rbrcloud.portfoliosrvc.entity.Portfolio;
import com.rbrcloud.portfoliosrvc.service.PortfolioService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

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
        Portfolio portfolio = new Portfolio(null, 3001L, "AAPL");

        Portfolio savedPortfolio = new Portfolio(1L, 3001L, "AAPL");
        when(portfolioService.createPortfolio(any(Portfolio.class))).thenReturn(savedPortfolio);

        mockMvc.perform(
                        post("/api/v1/portfolios")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(portfolio))
                                .with(user("testuser").roles("USER"))
                                .with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }
}
