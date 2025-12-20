package com.example.travelappapi.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.travelappapi.model.DatTour;

@Repository
public interface DatTourRepository extends JpaRepository<DatTour, Integer> {
    List<DatTour> findByNguoiDung_MaNguoiDungOrderByNgayDatDesc(Integer maNguoiDung);

    List<DatTour> findByNguoiDung_MaNguoiDungAndTrangThaiDatTourOrderByNgayDatDesc(Integer maNguoiDung, String trangThaiDatTour);
}
