package com.example.travelappapi.controller;

import com.example.travelappapi.dto.TourRequest;
import com.example.travelappapi.model.*;
import com.example.travelappapi.repository.*;
import com.example.travelappapi.service.CloudinaryService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/tours")
public class AdminTourController {

    @Autowired private TourRepository tourRepository;
    @Autowired private DiemDenRepository diemDenRepository;
    @Autowired private LichKhoiHanhRepository lichKhoiHanhRepository;
    @Autowired private NguoiDungRepository nguoiDungRepository;
    @Autowired private CloudinaryService cloudinaryService;
    @Autowired private DatTourRepository datTourRepository;

    // 1. Lấy danh sách tất cả Tour
    @GetMapping("all")
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


    // Lưu Tour và Lịch trình cùng lúc
    @PostMapping(value = "/add-full", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Transactional
    public ResponseEntity<?> addFullTour(@RequestPart("tour") TourRequest request, @RequestParam("file") MultipartFile file) {
        try {
            Tour tour = new Tour();
            tour.setTenTour(request.getTenTour());
            tour.setMoTa(request.getMoTa());
            tour.setGiaNguoiLon(request.getGiaNguoiLon());
            tour.setGiaTreEm(request.getGiaTreEm());
            tour.setTrangThai(request.getTrangThai());

            String fixedFileName = "tour_" + System.currentTimeMillis() + ".jpg";
            String secureUrl = cloudinaryService.uploadImage(file, fixedFileName);

            tour.setUrlHinhAnhChinh(fixedFileName);

            DiemDen dd = diemDenRepository.findById(request.getMaDiemDen()).orElseThrow(() -> new RuntimeException("Không tìm thấy điểm đến"));
            tour.setDiemDen(dd);
            
            Tour savedTour = tourRepository.save(tour);

            if (request.getLichKhoiHanhs() != null) {
                for (TourRequest.LichKhoiHanhDTO dto : request.getLichKhoiHanhs()) {

                    LichKhoiHanh lkh = new LichKhoiHanh();
                    DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

                    lkh.setNgayKhoiHanh(LocalDateTime.parse(dto.getNgayKhoiHanh(), fmt));
                    lkh.setNgayKetThuc(LocalDateTime.parse(dto.getNgayKetThuc(), fmt));
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

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Transactional
    public ResponseEntity<?> updateTour(@PathVariable Integer id, 
                                        @RequestPart("tour") TourRequest request, 
                                        @RequestPart(value = "file", required = false) MultipartFile file) {
        
        Tour tour = tourRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy Tour"));

        // Xử lý ảnh
        if (file != null && !file.isEmpty()) {
            try {
                // Xóa ảnh cũ (nếu có)
                if (tour.getUrlHinhAnhChinh() != null) {
                    cloudinaryService.deleteImage(tour.getUrlHinhAnhChinh());
                }
                
                String newFileName = "tour_" + System.currentTimeMillis()  + ".jpg";
                String uploadedUrl = cloudinaryService.uploadImage(file, newFileName); 

                tour.setUrlHinhAnhChinh(newFileName);
                
            } catch (IOException e) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Lỗi upload/xóa ảnh", e);
            }
        }


        //Cập nhật Tour 
        tour.setTenTour(request.getTenTour());
        tour.setMoTa(request.getMoTa());
        tour.setGiaNguoiLon(request.getGiaNguoiLon());
        tour.setGiaTreEm(request.getGiaTreEm());
        tour.setTrangThai(request.getTrangThai());
        tour.setDiemDen(diemDenRepository.findById(request.getMaDiemDen()).orElse(null));

        //Xử lý Lịch khởi hành
        if (request.getLichKhoiHanhs() != null) {
            //Lấy danh sách ID từ request để biết cái nào cần giữ/cập nhật
            Set<Integer> requestIds = request.getLichKhoiHanhs().stream()
                    .map(TourRequest.LichKhoiHanhDTO::getMaLich)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());

            // Lọc ra các lịch trình bị xóa (có trong DB nhưng không có trong request gửi lên)
            List<LichKhoiHanh> lichBiXoa = tour.getLichKhoiHanhs().stream()
                    .filter(lkh -> lkh.getMaLichKhoiHanh() != null && !requestIds.contains(lkh.getMaLichKhoiHanh()))
                    .collect(Collectors.toList());

            //Kiểm tra xem các lịch bị xóa này đã có khách đặt chưa
            for (LichKhoiHanh lkh : lichBiXoa) {
                if (datTourRepository.existsByLichKhoiHanh_MaLichKhoiHanh(lkh.getMaLichKhoiHanh())) {
                    throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, 
                        "Không thể xóa lịch khởi hành ngày " + lkh.getNgayKhoiHanh() + " vì đã có khách đặt tour!"
                    );
                }
            }

            //Nếu không có lịch nào có đơn đặt thì xóa
            tour.getLichKhoiHanhs().removeIf(lkh -> 
                lkh.getMaLichKhoiHanh() != null && !requestIds.contains(lkh.getMaLichKhoiHanh())
            );

            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

            for (TourRequest.LichKhoiHanhDTO dto : request.getLichKhoiHanhs()) {
                LichKhoiHanh lkh;
                
                if (dto.getMaLich() == null) {
                    // Thêm mới
                    lkh = new LichKhoiHanh();
                    lkh.setTour(tour);
                    tour.getLichKhoiHanhs().add(lkh); // Thêm vào list của cha
                } else {
                    // Cập nhật cái cũ
                    lkh = tour.getLichKhoiHanhs().stream()
                            .filter(item -> item.getMaLichKhoiHanh().equals(dto.getMaLich()))
                            .findFirst()
                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Lịch không tồn tại"));
                }
                
                lkh.setHuongDanVien(nguoiDungRepository.findById(dto.getMaHDV())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "HDV không tồn tại")));
                
                lkh.setNgayKhoiHanh(LocalDateTime.parse(dto.getNgayKhoiHanh(), fmt));
                lkh.setNgayKetThuc(LocalDateTime.parse(dto.getNgayKetThuc(), fmt));
                lkh.setSoLuongKhachToiDa(dto.getSoLuongKhachToiDa());
            }
        }

        tourRepository.save(tour);
        return ResponseEntity.ok("Cập nhật thành công");
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