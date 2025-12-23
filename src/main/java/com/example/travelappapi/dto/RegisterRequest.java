package com.example.travelappapi.dto;

import java.time.LocalDate;

import lombok.Data;

@Data
public class RegisterRequest {
    private String tenDangNhap;
    private String matKhau;
    private String hoTen;
    private String email;
    private String soDienThoai;
    private String diaChi;
    private LocalDate ngaySinh;
    private String gioiTinh;
    private String anhDaiDien;
    private String vaiTro;
    private String trangThai;
}