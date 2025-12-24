package com.example.travelappapi.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import org.hibernate.annotations.DynamicInsert;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Data
@DynamicInsert
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "NguoiDung")
public class NguoiDung implements UserDetails{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer maNguoiDung;

    private String tenDangNhap;
    private String email;
    private String matKhau;
    private String hoTen;
    private String soDienThoai;
    private String diaChi;

    @JsonFormat(pattern = "dd/MM/yyyy")
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

    @Override
    public String getUsername() {
        return tenDangNhap;
    }

    @Override
    public String getPassword() {
        return matKhau;
    }
    
    @Override
    public boolean isAccountNonExpired() {
        return true; // Tài khoản không bao giờ hết hạn
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // Tài khoản không bị khóa
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Mật khẩu không bao giờ hết hạn
    }

    @Override
    public boolean isEnabled() {
        return trangThai != null && trangThai.trim().equalsIgnoreCase("HoatDong");
    }


    @Override
    public Collection <? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + vaiTro));
    }
}
