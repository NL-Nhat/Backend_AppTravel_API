package com.example.travelappapi.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class ViDienTuResponse {

    private LocalDateTime ngayDi;
    private int soTreEm;
    private int soNguoiLon;
    private String tentour;
    private String trangThaiThanhToan;
    private String tenKhachHang;
}
