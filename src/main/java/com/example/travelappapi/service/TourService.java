package com.example.travelappapi.service;

import java.util.List;
import org.springframework.stereotype.Service;
import com.example.travelappapi.model.DanhGia;
import com.example.travelappapi.model.LichKhoiHanh;
import com.example.travelappapi.model.Tour;
import com.example.travelappapi.repository.DanhGiaRepository;
import com.example.travelappapi.repository.LichKhoiHanhRepository;
import com.example.travelappapi.repository.TourRepository;

@Service
public class TourService {

    private final TourRepository tourRepository;
    private final LichKhoiHanhRepository lichKhoiHanhRepository;
    private final DanhGiaRepository danhGiaRepository;

    public TourService(TourRepository tourRepository, LichKhoiHanhRepository lichKhoiHanhRepository, DanhGiaRepository danhGiaRepository) {
        this.tourRepository = tourRepository;
        this.lichKhoiHanhRepository = lichKhoiHanhRepository;
        this.danhGiaRepository = danhGiaRepository;
    }

    public List<Tour> getAllTours() {
        return tourRepository.findAll();
    }

    public Tour getTourById(int id) {
        return tourRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tour với ID " + id + " không tồn tại."));
    }

    // Hàm lấy Lịch Khởi Hành theo ID Tour
    public List<LichKhoiHanh> getLichKhoiHanhByTourId(int tourId) {
        return lichKhoiHanhRepository.findByTour_MaTour(tourId);
    }

    // Hàm lấy Đánh Giá theo ID Tour
    public List<DanhGia> getDanhGiaByTourId(int tourId) {
        return danhGiaRepository.findByTour_MaTourOrderByThoiGianTaoDesc(tourId);
    }

}
