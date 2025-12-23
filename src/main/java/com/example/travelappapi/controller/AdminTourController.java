package com.example.travelappapi.controller;

import com.example.travelappapi.dto.TourRequest;
import com.example.travelappapi.model.*;
import com.example.travelappapi.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/admin/tours")
public class AdminTourController {

    @Autowired private TourRepository tourRepository;
    @Autowired private DiemDenRepository diemDenRepository;
    @Autowired private LichKhoiHanhRepository lichKhoiHanhRepository;
    @Autowired private NguoiDungRepository nguoiDungRepository;

    // 1. Lấy danh sách tất cả Tour
    @GetMapping("")
    public ResponseEntity<List<Tour>> getAllTours() {
        return ResponseEntity.ok(tourRepository.findAll());
    }

    // 2. Lấy chi tiết một Tour
    @GetMapping("/{id}")
    public ResponseEntity<Tour> getTourById(@PathVariable Integer id) {
        return tourRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 3. API Lấy danh sách Hướng dẫn viên
    @GetMapping("/huong-dan-vien")
    public ResponseEntity<List<NguoiDung>> getHuongDanViens() {
        List<NguoiDung> tatCa = nguoiDungRepository.findAll();
        List<NguoiDung> danhSachHDV = new ArrayList<>();
        for (NguoiDung nd : tatCa) {
            if ("HuongDanVien".equalsIgnoreCase(nd.getVaiTro())) {
                danhSachHDV.add(nd);
            }
        }
        return ResponseEntity.ok(danhSachHDV);
    }

    // 4. Lưu Tour và Lịch trình cùng lúc
    @PostMapping("/add-full")
    @Transactional
    public ResponseEntity<?> addFullTour(@RequestBody TourRequest request) {
        try {
            Tour tour = new Tour();
            tour.setTenTour(request.getTenTour());
            tour.setMoTa(request.getMoTa());
            tour.setGiaNguoiLon(request.getGiaNguoiLon());
            tour.setGiaTreEm(request.getGiaTreEm());
            tour.setUrlHinhAnhChinh(request.getUrlHinhAnhChinh());
            tour.setTrangThai(request.getTrangThai());
            
            DiemDen dd = diemDenRepository.findById(request.getMaDiemDen()).orElse(null);
            tour.setDiemDen(dd);
            
            Tour savedTour = tourRepository.save(tour);

            if (request.getLichKhoiHanhs() != null) {
                for (TourRequest.LichKhoiHanhDTO dto : request.getLichKhoiHanhs()) {
                    LichKhoiHanh lkh = new LichKhoiHanh();
                    try {
                        lkh.setNgayKhoiHanh(LocalDateTime.parse(dto.getNgayKhoiHanh()));
                        lkh.setNgayKetThuc(LocalDateTime.parse(dto.getNgayKetThuc()));
                    } catch (Exception e) {
                        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                        lkh.setNgayKhoiHanh(LocalDateTime.parse(dto.getNgayKhoiHanh(), fmt));
                        lkh.setNgayKetThuc(LocalDateTime.parse(dto.getNgayKetThuc(), fmt));
                    }
                    lkh.setSoLuongKhachToiDa(dto.getSoLuongKhachToiDa());
                    lkh.setTour(savedTour);
                    if (dto.getMaHDV() != null) {
                        nguoiDungRepository.findById(dto.getMaHDV()).ifPresent(lkh::setHuongDanVien);
                    }
                    lichKhoiHanhRepository.save(lkh);
                }
            }
            return ResponseEntity.ok("Lưu Tour thành công!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi dữ liệu: " + e.getMessage());
        }
    }

    // 5. Cập nhật Tour (SỬA ĐỂ CẬP NHẬT CẢ HDV TRONG LỊCH TRÌNH)
    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<?> updateTour(@PathVariable Integer id, @RequestBody TourRequest request) {
        try {
            Tour tour = tourRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy Tour"));

            // Cập nhật thông tin Tour
            tour.setTenTour(request.getTenTour());
            tour.setMoTa(request.getMoTa());
            tour.setGiaNguoiLon(request.getGiaNguoiLon());
            tour.setGiaTreEm(request.getGiaTreEm());
            tour.setTrangThai(request.getTrangThai());
            tour.setUrlHinhAnhChinh(request.getUrlHinhAnhChinh());

            DiemDen dd = diemDenRepository.findById(request.getMaDiemDen()).orElse(null);
            tour.setDiemDen(dd);

            // Logic cập nhật Hướng dẫn viên cho các lịch trình hiện có
            if (request.getLichKhoiHanhs() != null && !request.getLichKhoiHanhs().isEmpty()) {
                List<LichKhoiHanh> currentSchedules = tour.getLichKhoiHanhs();
                
                for (int i = 0; i < request.getLichKhoiHanhs().size(); i++) {
                    TourRequest.LichKhoiHanhDTO dto = request.getLichKhoiHanhs().get(i);
                    
                    // Nếu index này tồn tại trong DB, thực hiện cập nhật HDV
                    if (i < currentSchedules.size()) {
                        LichKhoiHanh lkh = currentSchedules.get(i);
                        if (dto.getMaHDV() != null) {
                            NguoiDung hdvMoi = nguoiDungRepository.findById(dto.getMaHDV()).orElse(null);
                            lkh.setHuongDanVien(hdvMoi);
                            // Cập nhật thêm số lượng khách nếu cần
                            lkh.setSoLuongKhachToiDa(dto.getSoLuongKhachToiDa());
                            lichKhoiHanhRepository.save(lkh);
                        }
                    }
                }
            }

            tourRepository.save(tour);
            return ResponseEntity.ok("Cập nhật thành công");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi: " + e.getMessage());
        }
    }

    // 6. Xóa Tour
    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<?> deleteTour(@PathVariable Integer id) {
        try {
            if (tourRepository.existsById(id)) {
                tourRepository.deleteById(id);
                return ResponseEntity.ok("Đã xóa tour thành công");
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Không thể xóa Tour vì đã có đơn đặt vé!");
        }
    }
}