package com.example.travelappapi.model;

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
@Table(name = "HinhAnhTour")
public class HinhAnhTour {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer maHinhAnh;

    private String urlHinhAnh;
    private String tieuDeHinhAnh;
    private Boolean laChinh;

    @ManyToOne
    @JoinColumn(name = "maTour")
    private Tour tour;
}
