package com.example.travelappapi.controller;

import com.example.travelappapi.model.DiemDen;
import com.example.travelappapi.repository.DiemDenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/diem-den")
public class DiemDenController {

    @Autowired
    private DiemDenRepository diemDenRepository;

    @GetMapping
    public List<DiemDen> getAllDiemDen() {
        return diemDenRepository.findAll();
    }
}