package com.example.travelappapi.model;

import java.time.LocalDate;

import org.hibernate.annotations.DynamicInsert;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@DynamicInsert
@AllArgsConstructor
@Table(name = "KhachHangThamGia")
public class KhachHangThamGia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer maKhachHang;

    private String hoTen;
    private LocalDate ngaySinh;
    private String soDienThoai;
    private String gioiTinh;
    private String diaChi;

    @ManyToOne
    @JoinColumn(name = "maDatTour")
    private DatTour datTour;
}
