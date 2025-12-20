package com.example.travelappapi.model;

import java.util.List;

import org.hibernate.annotations.DynamicInsert;

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
@DynamicInsert
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
    @JsonIgnore
    @ToString.Exclude
    private List<LichKhoiHanh> lichKhoiHanhs;

    @OneToMany(mappedBy = "tour")
    @JsonIgnore
    @ToString.Exclude
    private List<HinhAnhTour> hinhAnhTours;

    @OneToMany(mappedBy = "tour")
    @JsonIgnore
    @ToString.Exclude
    private List<DanhGia> danhGias;

    @OneToMany(mappedBy = "tour")
    @JsonIgnore
    @ToString.Exclude
    private List<TourYeuThich> tourYeuThichs;
}
