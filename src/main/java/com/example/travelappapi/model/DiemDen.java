package com.example.travelappapi.model;

import java.util.List;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "DiemDen")
public class DiemDen {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer maDiemDen;

    private String tenDiemDen;
    private String thanhPho;
    private String moTa;
    private String urlHinhAnh;

    @OneToMany(mappedBy = "diemDen")
    private List<Tour> tours;
}
