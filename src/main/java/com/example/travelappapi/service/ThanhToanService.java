package com.example.travelappapi.service;


import org.springframework.stereotype.Service;

import com.example.travelappapi.dto.ThanhToanRequest;
import com.example.travelappapi.model.DatTour;
import com.example.travelappapi.model.ThanhToan;
import com.example.travelappapi.repository.DatTourRepository;
import com.example.travelappapi.repository.ThanhToanRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class ThanhToanService {

    private final DatTourRepository datTourRepository;
    private final ThanhToanRepository thanhToanRepository;

    public ThanhToanService(DatTourRepository datTourRepository, ThanhToanRepository thanhToanRepository) {
        this.datTourRepository = datTourRepository;
        this.thanhToanRepository = thanhToanRepository;
    }

    public void createThanhToan(ThanhToanRequest thanhToanRequest) {

        ThanhToan thanhToan = new ThanhToan();
        DatTour datTour = datTourRepository.findById(thanhToanRequest.getMaDatTour())
        .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn đặt tour"));

        thanhToan.setDatTour(datTour);
        thanhToan.setPhuongThucThanhToan(thanhToanRequest.getPhuongThuc());
        thanhToan.setSoTien(thanhToanRequest.getSoTien());

        datTour.setTrangThaiDatTour("DaXacNhan");
        datTour.setTrangThaiThanhToan("DaThanhToan");

        thanhToanRepository.save(thanhToan);
    }
}
