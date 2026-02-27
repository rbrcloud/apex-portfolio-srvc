package com.rbrcloud.portfoliosrvc.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/portfolio/dbcheck")
public class DBCheckController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping("/connection-info")
    public Map<String, String> getConnectionInfo() {
        return jdbcTemplate.queryForObject(
                "SELECT current_database() AS database, current_user AS user, current_schema() AS schema, version() AS version, inet_client_addr() AS client_ip",
                (rs, rowNum) -> Map.of(
                        "database", rs.getString("database"),
                        "user", rs.getString("user"),
                        "schema", rs.getString("schema"),
                        "version", rs.getString("version"),
                        "client_ip", rs.getString("client_ip")
                )
        );
    }
}
