package com.example.travelappapi.dto;

import java.util.List;
import lombok.Data;

@Data
public class TourRequest {
    // Thông tin cơ bản của Tour
    private String tenTour;
    private String moTa;
    private Double giaNguoiLon;
    private Double giaTreEm;
    private String trangThai;
    private Integer maDiemDen;

    private List<LichKhoiHanhDTO> lichKhoiHanhs;

    @Data
    public static class LichKhoiHanhDTO {
        private String ngayKhoiHanh; 
        private String ngayKetThuc;
        private Integer soLuongKhachToiDa;
        private Integer maHDV; 
    }
}