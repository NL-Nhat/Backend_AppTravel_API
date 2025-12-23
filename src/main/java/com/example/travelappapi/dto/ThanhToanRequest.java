package com.example.travelappapi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ThanhToanRequest {

    private int maDatTour;
    private String phuongThuc;
    private Double soTien;
}
