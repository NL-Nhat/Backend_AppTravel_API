package com.example.travelappapi.repository;

import java.util.Optional;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.travelappapi.model.NguoiDung;

@Repository
public interface NguoiDungRepository extends JpaRepository<NguoiDung, Integer> {

    // Spring Data JPA tự động tạo truy vấn tìm kiếm theo tên đăng nhập
    Optional<NguoiDung> findByTenDangNhap(String tenDangNhap);
    // Thêm dòng này để kiểm tra trùng lặp cho đăng ký tài khoản
    boolean existsByTenDangNhap(String tenDangNhap);
    List<NguoiDung> findByVaiTro(String vaiTro);
}
