package com.example.travelappapi.model;

import java.time.LocalDateTime;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

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
    @JsonBackReference // Ngăn vòng lặp ngược lại Tour
    private Tour tour;

    @ManyToOne
    @JoinColumn(name = "huongDanVien")
    private NguoiDung huongDanVien;

    @OneToMany(mappedBy = "lichKhoiHanh")
    @JsonIgnore
    @ToString.Exclude
    private List<DatTour> datTours;
}