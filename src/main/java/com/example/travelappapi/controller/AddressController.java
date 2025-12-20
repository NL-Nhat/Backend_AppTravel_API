package com.example.travelappapi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/api/address")
public class AddressController {

    @Autowired
    private RestTemplate restTemplate;

    private final String BASE_URL = "https://provinces.open-api.vn/api";

    // 1. Lấy toàn bộ Tỉnh/Thành
    @GetMapping("/provinces")
    public ResponseEntity<?> getAllProvinces() {
        try {
            String url = BASE_URL + "/p/";
            Object data = restTemplate.getForObject(url, Object.class);
            return ResponseEntity.ok(data);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Lỗi khi lấy danh sách tỉnh thành: " + e.getMessage());
        }
    }

    // 2. Lấy Quận/Huyện theo mã Tỉnh
    @GetMapping("/districts/{provinceCode}")
    public ResponseEntity<?> getDistrictsByProvince(@PathVariable String provinceCode) {
        try {
            // Lưu ý: Kết quả trả về là Object Tỉnh, bên trong có mảng "districts"
            String url = BASE_URL + "/p/" + provinceCode + "?depth=2";
            Object data = restTemplate.getForObject(url, Object.class);
            return ResponseEntity.ok(data);
        } catch (Exception e) {
            return ResponseEntity.status(404).body("Không tìm thấy quận huyện cho mã tỉnh: " + provinceCode);
        }
    }

    // 3. Lấy Phường/Xã theo mã Quận
    @GetMapping("/wards/{districtCode}")
    public ResponseEntity<?> getWardsByDistrict(@PathVariable String districtCode) {
        try {
            // Lưu ý: Kết quả trả về là Object Quận, bên trong có mảng "wards"
            String url = BASE_URL + "/d/" + districtCode + "?depth=2";
            Object data = restTemplate.getForObject(url, Object.class);
            return ResponseEntity.ok(data);
        } catch (Exception e) {
            return ResponseEntity.status(404).body("Không tìm thấy phường xã cho mã quận: " + districtCode);
        }
    }
}
