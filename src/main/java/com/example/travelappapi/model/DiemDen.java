package com.example.travelappapi.model;

import java.util.List;

import org.hibernate.annotations.DynamicInsert;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@DynamicInsert
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
    @JsonIgnore  //Ngắt vòng lặp JSON khi lấy DiemDen
    @ToString.Exclude  //Ngắt vòng lặp toString của Lombok
    private List<Tour> tours;
}
