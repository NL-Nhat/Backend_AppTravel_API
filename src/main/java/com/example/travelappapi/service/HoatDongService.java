package com.example.travelappapi.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.travelappapi.dto.HoatDongDTO;
import com.example.travelappapi.repository.HoatDongRepository;

@Service
public class HoatDongService {

    @Autowired
    private HoatDongRepository hoatDongRepository;

    public List<HoatDongDTO> getLichSuHoatDong() {
        List<Object[]> data = hoatDongRepository.getRawRecentActivities();

        return data.stream().map(row -> {
            java.time.LocalDateTime thoiGian = null;
            if (row[3] != null) {
                // Ép kiểu sang Timestamp của SQL trước, sau đó mới đổi sang LocalDateTime
                thoiGian = ((java.sql.Timestamp) row[3]).toLocalDateTime();
            }

            return new HoatDongDTO(
                row[0] != null ? row[0].toString() : "", 
                row[1] != null ? row[1].toString() : "", 
                row[2] != null ? row[2].toString() : "", 
                thoiGian
            );
        }).collect(Collectors.toList());
    }
}
