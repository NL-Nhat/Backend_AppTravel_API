package com.example.travelappapi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.travelappapi.dto.LoginRequest;
import com.example.travelappapi.model.NguoiDung;
import com.example.travelappapi.repository.NguoiDungRepository;

@Service
public class AuthService {

    private final NguoiDungRepository nguoiDungRepository;

    @Autowired
    public AuthService(NguoiDungRepository nguoiDungRepository) {
        this.nguoiDungRepository = nguoiDungRepository;
    }

    public NguoiDung authenticate(LoginRequest loginRequest) {
        
        // 1. Tìm người dùng
        NguoiDung nguoiDung = nguoiDungRepository.findByTenDangNhap(loginRequest.getTenDangNhap())
                .orElseThrow(() -> new RuntimeException("Tên đăng nhập không tồn tại."));

        // 2. SO SÁNH MẬT KHẨU 
        if (!loginRequest.getMatKhau().equals(nguoiDung.getMatKhau())) {
            throw new RuntimeException("Mật khẩu không đúng.");
        }
        
        // 3. Kiểm tra trạng thái 
        if (!nguoiDung.getTrangThai().equals("HoatDong")) {
            throw new RuntimeException("Tài khoản đang bị " + nguoiDung.getTrangThai());
        }

        // Đăng nhập thành công
        return nguoiDung;
    }

}
