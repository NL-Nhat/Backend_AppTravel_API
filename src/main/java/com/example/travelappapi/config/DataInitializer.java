package com.example.travelappapi.config;

import com.example.travelappapi.model.NguoiDung;
import com.example.travelappapi.repository.NguoiDungRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;

// Lớp tạo tài khoản mặc định
@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private NguoiDungRepository nguoiDungRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        
        // 1. Tạo tài khoản Admin mặc định
        if (nguoiDungRepository.findByTenDangNhap("admin").isEmpty()) {
            NguoiDung admin = new NguoiDung();
            admin.setTenDangNhap("admin");
            admin.setEmail("admin@gmail.com");
            // Mật khẩu sẽ được mã hóa BCrypt trước khi lưu
            admin.setMatKhau(passwordEncoder.encode("12345")); 
            admin.setHoTen("Nguyễn Long Nhât");
            admin.setSoDienThoai("0999888777");
            admin.setDiaChi("Quảng Trị");
            admin.setVaiTro("Admin"); // Khớp với CHECK (vaiTro IN ('Admin', 'KhachHang', 'HuongDanVien'))
            admin.setTrangThai("HoatDong");
            admin.setThoiGianTao(LocalDateTime.now());
            admin.setNgaySinh(LocalDate.of(2000, 1, 1));
            admin.setAnhDaiDien("anhdaidien.jpg");

            nguoiDungRepository.save(admin);
            System.out.println(">>> [DataInitializer] Đã tạo tài khoản ADMIN mặc định: admin / 12345");
        }

        // 2. Tạo tài khoản Khách hàng mẫu
        if (nguoiDungRepository.findByTenDangNhap("user").isEmpty()) {
            NguoiDung user = new NguoiDung();
            user.setTenDangNhap("user");
            user.setEmail("user@gmail.com");
            user.setMatKhau(passwordEncoder.encode("12345"));
            user.setHoTen("Nguyễn Long Nhật");
            user.setSoDienThoai("0123456789");
            user.setVaiTro("KhachHang");
            user.setTrangThai("HoatDong");
            user.setDiaChi("Quảng Trị");
            user.setGioiTinh("Nam");
            user.setThoiGianTao(LocalDateTime.now());
            user.setNgaySinh(LocalDate.of(2005, 11, 1));
            user.setAnhDaiDien("anhdaidien.jpg");

            nguoiDungRepository.save(user);
            System.out.println(">>> [DataInitializer] Đã tạo tài khoản USER mặc định: user / 12345");
        }
    }
}