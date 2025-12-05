package com.example.travelappapi.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.travelappapi.model.DanhGia;

public interface DanhGiaRepository extends JpaRepository<DanhGia, Integer> {

    List<DanhGia> findByTour_MaTourOrderByThoiGianTaoDesc(Integer maTour);
}
