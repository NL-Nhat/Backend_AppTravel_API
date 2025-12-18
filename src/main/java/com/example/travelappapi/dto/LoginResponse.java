package com.example.travelappapi.dto;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponse {

    private String token;
    private Integer maNguoiDung;
    private String hoTen;
    private String email;
    private String vaiTro;
    private String anhDaiDien;
    private String soDienThoai;
    private String diaChi;
    private String gioiTinh;
    private LocalDate ngaySinh;
}
