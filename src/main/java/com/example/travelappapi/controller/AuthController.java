package com.example.travelappapi.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.travelappapi.dto.LoginRequest;
import com.example.travelappapi.model.NguoiDung;
import com.example.travelappapi.service.AuthService;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            // 1. Gọi Service để xác thực
            NguoiDung nguoiDung = authService.authenticate(loginRequest);

            // 2. Tạo phản hồi thành công (trả về vai trò)
            Map<String, Object> response = new HashMap<>();
            response.put("maNguoiDung", nguoiDung.getMaNguoiDung());
            response.put("hoTen", nguoiDung.getHoTen());
            response.put("email", nguoiDung.getEmail());
            response.put("anhDaiDien", nguoiDung.getAnhDaiDien());
            response.put("vaiTro", nguoiDung.getVaiTro()); 
            response.put("message", "Đăng nhập thành công!");
            
            return ResponseEntity.ok(response); 

        } catch (RuntimeException e) {
            // 3. Xử lý lỗi xác thực từ Service
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            
            return ResponseEntity
                    .status(401) // Trả về 401 Unauthorized
                    .body(error);
        }
    }

}
