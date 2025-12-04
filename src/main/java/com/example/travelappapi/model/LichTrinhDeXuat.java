package com.example.travelappapi.model;

import java.util.List;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "LichTrinhDeXuat")
public class LichTrinhDeXuat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer maLichTrinh;

    private String tieuDeTour;
    private String noiDungDeXuat;
    private Double tongTienDuKien;
    private String trangThai;

    @ManyToOne
    @JoinColumn(name = "maYeuCau")
    private YeuCauTourAI yeuCau;

    @OneToMany(mappedBy = "lichTrinhDeXuat")
    private List<GuiYeuCauTour> guiYeuCauTours;
}
