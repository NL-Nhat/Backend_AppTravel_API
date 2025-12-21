package com.example.travelappapi.dto;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingRequestDTO {

    private int maNguoiDung;
    private int maLichKhoiHanh;
    private int soNguoiLon;
    private int soTreEm;
    private String hoTen;
    private String soDienThoai;
    private String gioiTinh;
    private String diaChi;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate ngaySinh;
}
