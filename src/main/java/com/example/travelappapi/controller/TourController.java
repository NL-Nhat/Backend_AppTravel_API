package com.example.travelappapi.controller;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.travelappapi.model.Tour;
import com.example.travelappapi.service.TourService;


@RestController
@RequestMapping("api/tour")
public class TourController {

    private final TourService tourService;

    public TourController(TourService tourService) {
        this.tourService = tourService;
    }

    @GetMapping("/all")
    // Kiểu trả về ResponseEntity<?> trả về được cả List hoặc Lỗi
    public ResponseEntity<?> getAllTours() {
        try {
            List<Tour> tours = tourService.getAllTours();
            //Trả về HTTP 200 (OK) kèm dữ liệu
            return ResponseEntity.ok(tours); 
        } catch (Exception e) {
            // Khai báo biến error map để chứa thông báo
            Map<String, String> error = new HashMap<>();
            error.put("message", "Không thể lấy danh sách tour.");
            error.put("detail", e.getMessage()); // Ghi thêm chi tiết lỗi thực tế

            //Trả về HTTP 500 (Internal Server Error) kèm thông báo lỗi
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @GetMapping("/{id}/lich-khoi-hanh")
    public ResponseEntity<?> getLichKhoiHanh(@PathVariable int id) {
        return ResponseEntity.ok(tourService.getLichKhoiHanhByTourId(id));
    }

    @GetMapping("/{id}/danh-gia")
    public ResponseEntity<?> getDanhGia(@PathVariable int id) {
        return ResponseEntity.ok(tourService.getDanhGiaByTourId(id));
    }

    //Check đường dẫn ảnh
    @GetMapping("/check-avatar")
    public Map<String, Object> check() {
        String root = System.getProperty("user.dir");
        File file = new File(root + "/uploads/avatar/anhdaidien.jpg");

        Map<String, Object> rs = new HashMap<>();
        rs.put("root", root);
        rs.put("exists", file.exists());
        rs.put("path", file.getAbsolutePath());
        return rs;
    }
    
}
