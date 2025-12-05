package com.example.travelappapi.model;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "DatTour")
public class DatTour {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer maDatTour;

    private Integer soNguoiLon;
    private Integer soTreEm;

    private Double tongTien;

    private String trangThaiDatTour;
    private String trangThaiThanhToan;

    private LocalDateTime ngayDat;
    private LocalDateTime ngayHuy;
    private String lyDoHuy;

    @ManyToOne
    @JoinColumn(name = "maNguoiDung")
    private NguoiDung nguoiDung;

    @ManyToOne
    @JoinColumn(name = "maLichKhoiHanh")
    private LichKhoiHanh lichKhoiHanh;

    @OneToMany(mappedBy = "datTour")
    @JsonIgnore
    @ToString.Exclude
    private List<ThanhToan> thanhToans;

    @OneToMany(mappedBy = "datTour")
    @JsonIgnore
    @ToString.Exclude
    private List<KhachHangThamGia> khachHangThamGias;
}
