package com.example.travelappapi.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.travelappapi.dto.HoatDongDTO;
import com.example.travelappapi.service.HoatDongService;

@RestController
@RequestMapping("/api/admin")
public class HoatDongController {
    @Autowired
    private HoatDongService service;

    @GetMapping("/hoatdong")
    public ResponseEntity<List<HoatDongDTO>> getHoatDong() {
        return ResponseEntity.ok(service.getLichSuHoatDong());
    }
}