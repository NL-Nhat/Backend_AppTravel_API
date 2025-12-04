package com.example.travelappapi.model;

import java.time.LocalDateTime;
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
@Table(name = "LichKhoiHanh")
public class LichKhoiHanh {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer maLichKhoiHanh;

    private LocalDateTime ngayKhoiHanh;
    private LocalDateTime ngayKetThuc;

    private Integer soLuongKhachToiDa;
    private Integer soLuongKhachDaDat;

    @ManyToOne
    @JoinColumn(name = "maTour")
    private Tour tour;

    @ManyToOne
    @JoinColumn(name = "huongDanVien")
    private NguoiDung huongDanVien;

    @OneToMany(mappedBy = "lichKhoiHanh")
    private List<DatTour> datTours;
}
