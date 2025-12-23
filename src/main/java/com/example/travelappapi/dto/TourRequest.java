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
    private String urlHinhAnhChinh;
    private String trangThai;
    private Integer maDiemDen; // Chỉ nhận ID để map vào Entity sau

    // Danh sách lịch trình đi kèm
    private List<LichKhoiHanhDTO> lichKhoiHanhs;

    @Data
    public static class LichKhoiHanhDTO {
        private String ngayKhoiHanh; // Định dạng "yyyy-MM-dd HH:mm:ss"
        private String ngayKetThuc;
        private Integer soLuongKhachToiDa;
        private Integer maHDV; // ID của Hướng dẫn viên được chọn
    }
}