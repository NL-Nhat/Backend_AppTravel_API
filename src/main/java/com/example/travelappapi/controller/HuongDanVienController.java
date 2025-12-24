package com.example.travelappapi.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.travelappapi.dto.HuongDanVienResponse;
import com.example.travelappapi.model.NguoiDung;
import com.example.travelappapi.repository.NguoiDungRepository;

@RestController
@RequestMapping("/api/admin/huong-dan-vien")
public class HuongDanVienController {

    @Autowired
    private NguoiDungRepository nguoiDungRepository;

    @GetMapping
    public ResponseEntity<List<HuongDanVienResponse>> getHuongDanViens() {
        List<NguoiDung> users = nguoiDungRepository.findByVaiTro("HuongDanVien");
        
        List<HuongDanVienResponse> response = users.stream().map(u -> {
            HuongDanVienResponse res = new HuongDanVienResponse();
            res.setMaHuongDanVien(u.getMaNguoiDung());
            res.setTenHuongDanVien(u.getHoTen());
            return res;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

}
