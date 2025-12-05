package com.example.travelappapi.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.travelappapi.model.LichKhoiHanh;

public interface LichKhoiHanhRepository extends JpaRepository<LichKhoiHanh, Integer>{

    List<LichKhoiHanh> findByTour_MaTour(Integer maTour);
}
