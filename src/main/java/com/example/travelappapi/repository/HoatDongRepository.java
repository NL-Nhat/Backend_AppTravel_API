package com.example.travelappapi.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.travelappapi.model.NguoiDung;

@Repository
public interface HoatDongRepository extends JpaRepository<NguoiDung, Integer> {
    @Query(value = "EXEC sp_LayHoatDongGanDay", nativeQuery = true)
    List<Object[]> getRawRecentActivities();
}