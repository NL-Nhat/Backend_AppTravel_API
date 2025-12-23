package com.example.travelappapi.model;

import java.util.List;
import org.hibernate.annotations.DynamicInsert;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

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
    private String trangThai; // "DangMo" hoặc "TamDung"

    @ManyToOne
    @JoinColumn(name = "maDiemDen")
    private DiemDen diemDen;

    @OneToMany(mappedBy = "tour", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JsonManagedReference // Cho phép gửi danh sách lịch trình về App
    @ToString.Exclude
    private List<LichKhoiHanh> lichKhoiHanhs;

    @OneToMany(mappedBy = "tour", cascade = CascadeType.ALL)
    @com.fasterxml.jackson.annotation.JsonIgnore
    private List<HinhAnhTour> hinhAnhTours;
}