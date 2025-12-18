package com.example.travelappapi.config;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class JwtResponse {
    private String token;
    private String type = "Bearer";

    // Constructor nhanh chỉ nhận token
    public JwtResponse(String token) {
        this.token = token;
    }
}