package com.example.travelappapi.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.travelappapi.dto.ThanhToanRequest;
import com.example.travelappapi.service.ThanhToanService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/api/thanhtoan")
public class ThanhToanController {

    private final ThanhToanService thanhToanService;

    public ThanhToanController(ThanhToanService thanhToanService) {
        this.thanhToanService = thanhToanService;
    }

    @PostMapping("/create")
    public ResponseEntity<?> createThanhToan(@RequestBody ThanhToanRequest thanhToanRequest) {
        
        thanhToanService.createThanhToan(thanhToanRequest);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
    
}
