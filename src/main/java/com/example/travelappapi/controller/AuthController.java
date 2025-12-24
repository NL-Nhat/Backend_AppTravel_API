package com.example.travelappapi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.travelappapi.config.JwtTokenProvider;
import com.example.travelappapi.dto.ChangePasswordRequest;
import com.example.travelappapi.dto.LoginRequest;
import com.example.travelappapi.dto.LoginResponse;
import com.example.travelappapi.dto.RegisterRequest;
import com.example.travelappapi.model.NguoiDung;
import com.example.travelappapi.repository.NguoiDungRepository;
import com.example.travelappapi.service.CloudinaryService;

import java.nio.file.*;
import java.util.UUID;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.List;


@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private NguoiDungRepository nguoiDungRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtTokenProvider tokenProvider;
    @Autowired
    private CloudinaryService cloudinaryService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
    
        // 1. Kiểm tra trùng tên đăng nhập
        if (nguoiDungRepository.existsByTenDangNhap(request.getTenDangNhap())) {
            return ResponseEntity.status(400).body("Tên đăng nhập đã tồn tại!");
        }

        // 2. Tạo đối tượng người dùng mới
        NguoiDung user = new NguoiDung();
        user.setTenDangNhap(request.getTenDangNhap());
        user.setMatKhau(passwordEncoder.encode(request.getMatKhau())); // Mã hóa mật khẩu bảo mật
        user.setHoTen(request.getHoTen());
        user.setEmail(request.getEmail());
        user.setVaiTro("KhachHang"); // Mặc định tài khoản mới là người dùng
        user.setTrangThai("HoatDong"); // Mặc định tài khoản mới là hoạt động

        // 3. Lưu vào DB
        nguoiDungRepository.save(user);

        return ResponseEntity.ok("Đăng ký thành công!");
    }

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

    @GetMapping("/user/{id}")
    public ResponseEntity<?> getNguoiDung(@PathVariable Integer id) {
        return nguoiDungRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

   @PutMapping("/user/{id}")
    public ResponseEntity<?> updateNguoiDung(@PathVariable Integer id, @RequestBody NguoiDung updateData) {
        return nguoiDungRepository.findById(id).map(user -> {
            if (updateData.getHoTen() != null) user.setHoTen(updateData.getHoTen());
            if (updateData.getSoDienThoai() != null) user.setSoDienThoai(updateData.getSoDienThoai());
            if (updateData.getNgaySinh() != null) user.setNgaySinh(updateData.getNgaySinh());
            if (updateData.getDiaChi() != null) user.setDiaChi(updateData.getDiaChi());
            if (updateData.getGioiTinh() != null) user.setGioiTinh(updateData.getGioiTinh());
            if (updateData.getAnhDaiDien() != null) user.setAnhDaiDien(updateData.getAnhDaiDien());
            
            // ĐỂ KHÓA TÀI KHOẢN
            if (updateData.getTrangThai() != null) user.setTrangThai(updateData.getTrangThai());
            
            NguoiDung updatedUser = nguoiDungRepository.save(user);
            return ResponseEntity.ok(updatedUser);

        }).orElse(ResponseEntity.notFound().build());
    }

   @PostMapping("/uploadAnhDaiDien")
    public ResponseEntity<?> uploadAvatar(@RequestParam("file") MultipartFile file, @RequestParam("userId") int userId) {
        try {
            // Đặt tên file
            String fileName = "avatar_" + userId; // đặt tên theo ID để tránh trùng
            
            // Upload lên Cloudinary
            String url = cloudinaryService.uploadImage(file, fileName);
            
            // Cập nhật Database
            NguoiDung user = nguoiDungRepository.findById(userId)
                                                .orElseThrow(() -> new RuntimeException("Không tìm thấy user"));
            user.setAnhDaiDien(fileName + ".jpg"); // Lưu tên vào SQL
            nguoiDungRepository.save(user);

            Map<String, String> response = new HashMap<>();
            response.put("fileName", fileName);
            response.put("url", url);

            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Lỗi upload");
        }
    }

    @PutMapping("/user/{id}/doiMatKhau")
    public ResponseEntity<?> doiMatKhau(@PathVariable Integer id, @RequestBody ChangePasswordRequest request) {
        return nguoiDungRepository.findById(id).map(user -> {

            if (!passwordEncoder.matches(request.getOldPassword(), user.getMatKhau())) {
                return ResponseEntity.status(400).body("Mật khẩu hiện tại không đúng");
            }

            if (request.getOldPassword().equals(request.getNewPassword())) {
                return ResponseEntity.status(400).body("Mật khẩu mới không được trùng mật khẩu cũ");
            }

            user.setMatKhau(passwordEncoder.encode(request.getNewPassword()));
            nguoiDungRepository.save(user);

            return ResponseEntity.ok("Đổi mật khẩu thành công!");
        }).orElse(ResponseEntity.status(404).body("Không tìm thấy người dùng"));
    }
    
   @GetMapping("/users")
    public ResponseEntity<List<NguoiDung>> getAllUsers() {
        return ResponseEntity.ok(nguoiDungRepository.findAll());
    }
}
