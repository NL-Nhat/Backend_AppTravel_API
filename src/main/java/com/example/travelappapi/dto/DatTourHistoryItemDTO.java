package com.example.travelappapi.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DatTourHistoryItemDTO {
    private Integer maDatTour;

    private Integer soNguoiLon;
    private Integer soTreEm;
    private Double tongTien;

    private String trangThaiDatTour;
    private String trangThaiThanhToan;

    private LocalDateTime ngayDat;
    private LocalDateTime ngayHuy;
    private String lyDoHuy;

    private Integer maLichKhoiHanh;
    private LocalDateTime ngayKhoiHanh;
    private LocalDateTime ngayKetThuc;

    private Integer maTour;
    private String tenTour;
    private String diaDiem;
    private String urlHinhAnhChinh;
}
