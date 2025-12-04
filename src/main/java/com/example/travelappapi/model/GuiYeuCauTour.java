package com.example.travelappapi.model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "GuiYeuCauTour")
public class GuiYeuCauTour {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer maYeuCauTour;

    private LocalDateTime ngayGui;
    private String trangThai;

    @ManyToOne
    @JoinColumn(name = "maLichTrinh")
    private LichTrinhDeXuat lichTrinhDeXuat;
}
