package com.example.travelappapi.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "NguoiDung")
public class NguoiDung {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer maNguoiDung;

    private String tenDangNhap;
    private String email;
    private String matKhau;
    private String hoTen;
    private String soDienThoai;
    private String diaChi;

    private LocalDate ngaySinh;

    private String gioiTinh;

    private String anhDaiDien;

    private String vaiTro;
    private String trangThai;

    private LocalDateTime thoiGianTao;

    // Lich khởi hành hướng dẫn viên
    @OneToMany(mappedBy = "huongDanVien")
    @JsonIgnore  //Ngắt vòng lặp JSON khi lấy NguoiDung
    @ToString.Exclude  //Ngắt vòng lặp toString của Lombok
    private List<LichKhoiHanh> lichKhoiHanhs;

    // Đặt tour
    @OneToMany(mappedBy = "nguoiDung")
    @JsonIgnore
    @ToString.Exclude
    private List<DatTour> datTours;

    // Đánh giá
    @OneToMany(mappedBy = "nguoiDung")
    @JsonIgnore
    @ToString.Exclude
    private List<DanhGia> danhGias;

    // Tour yêu thích
    @OneToMany(mappedBy = "nguoiDung")
    @JsonIgnore
    @ToString.Exclude
    private List<TourYeuThich> tourYeuThichs;

    // Yêu cầu tour AI
    @OneToMany(mappedBy = "nguoiDung")
    @JsonIgnore
    @ToString.Exclude
    private List<YeuCauTourAI> yeuCauTourAIList;
}
