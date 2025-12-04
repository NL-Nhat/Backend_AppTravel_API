package com.example.travelappapi.model;

import java.util.List;
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

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "Tour")
public class Tour {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer maTour;

    private String tenTour;
    private String moTa;

    private Double giaNguoiLon;
    private Double giaTreEm;

    private String urlHinhAnhChinh;

    private Double diemDanhGiaTrungBinh;
    private Integer soLuongDanhGia;
    private String trangThai;

    @ManyToOne
    @JoinColumn(name = "maDiemDen")
    private DiemDen diemDen;

    @OneToMany(mappedBy = "tour")
    private List<LichKhoiHanh> lichKhoiHanhs;

    @OneToMany(mappedBy = "tour")
    private List<HinhAnhTour> hinhAnhTours;

    @OneToMany(mappedBy = "tour")
    private List<DanhGia> danhGias;

    @OneToMany(mappedBy = "tour")
    private List<TourYeuThich> tourYeuThichs;
}
