package com.example.travelappapi.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor // Tự động tạo constructor có đầy đủ tham số
@NoArgsConstructor  // Tự động tạo constructor không đối số
public class HoatDongDTO {
    private String loai;
    private String tieuDe;
    private String moTa;
    private LocalDateTime thoiGian;
}