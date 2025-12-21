package com.example.travelappapi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.travelappapi.config.JwtTokenProvider;
import com.example.travelappapi.dto.LoginRequest;
import com.example.travelappapi.dto.LoginResponse;
import com.example.travelappapi.model.NguoiDung;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    loginRequest.getTenDangNhap(),
                    loginRequest.getMatKhau()
                )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Lấy user đã đăng nhập
            NguoiDung user = (NguoiDung) authentication.getPrincipal();

            // Tạo JWT
            String jwt = tokenProvider.generateToken(authentication);

            // TRẢ VỀ TOKEN + THÔNG TIN USER
            return ResponseEntity.ok(
                new LoginResponse(
                    jwt,
                    user.getMaNguoiDung(),
                    user.getHoTen(),
                    user.getEmail(),
                    user.getVaiTro(),
                    user.getAnhDaiDien(),
                    user.getSoDienThoai(),
                    user.getDiaChi(),
                    user.getGioiTinh(),
                    user.getNgaySinh()
                )
            );

        } catch (Exception e) {
            return ResponseEntity.status(401)
                    .body("Tên đăng nhập hoặc mật khẩu không đúng");
        }
    }

    @Autowired
    private com.example.travelappapi.repository.NguoiDungRepository nguoiDungRepository;

    @PutMapping("/user/{id}")
    public ResponseEntity<?> updateNguoiDung(@PathVariable Integer id, @RequestBody NguoiDung updateData) {
        return nguoiDungRepository.findById(id).map(user -> {
            user.setHoTen(updateData.getHoTen());
            user.setSoDienThoai(updateData.getSoDienThoai());
            user.setNgaySinh(updateData.getNgaySinh());
            user.setDiaChi(updateData.getDiaChi());
            user.setGioiTinh(updateData.getGioiTinh());
            
            NguoiDung updatedUser = nguoiDungRepository.save(user);
            
            return ResponseEntity.ok(updatedUser);
        }).orElse(ResponseEntity.notFound().build());
    }
}
