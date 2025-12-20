package com.example.travelappapi.service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.travelappapi.dto.DatTourHistoryItemDTO;
import com.example.travelappapi.model.DatTour;
import com.example.travelappapi.model.LichKhoiHanh;
import com.example.travelappapi.model.Tour;
import com.example.travelappapi.repository.DatTourRepository;

@Service
public class DatTourService {

    private final DatTourRepository datTourRepository;

    public DatTourService(DatTourRepository datTourRepository) {
        this.datTourRepository = datTourRepository;
    }

    public List<DatTourHistoryItemDTO> getLichSuDatTourCuaNguoiDung(Integer maNguoiDung, String trangThaiDatTour) {
        List<DatTour> datTours;
        if (trangThaiDatTour == null || trangThaiDatTour.isBlank()) {
            datTours = datTourRepository.findByNguoiDung_MaNguoiDungOrderByNgayDatDesc(maNguoiDung);
        } else {
            datTours = datTourRepository.findByNguoiDung_MaNguoiDungAndTrangThaiDatTourOrderByNgayDatDesc(maNguoiDung, trangThaiDatTour);
        }

        return datTours.stream().map(this::toDto).collect(Collectors.toList());
    }

    private DatTourHistoryItemDTO toDto(DatTour dt) {
        LichKhoiHanh lkh = dt.getLichKhoiHanh();
        Tour tour = (lkh != null ? lkh.getTour() : null);

        String diaDiem = null;
        if (tour != null && tour.getDiemDen() != null) {
            // ưu tiên thanhPho, fallback tenDiemDen
            diaDiem = Objects.toString(tour.getDiemDen().getThanhPho(), null);
            if (diaDiem == null || diaDiem.isBlank()) {
                diaDiem = Objects.toString(tour.getDiemDen().getTenDiemDen(), null);
            }
        }

        return new DatTourHistoryItemDTO(
                dt.getMaDatTour(),
                dt.getSoNguoiLon(),
                dt.getSoTreEm(),
                dt.getTongTien(),
                dt.getTrangThaiDatTour(),
                dt.getTrangThaiThanhToan(),
                dt.getNgayDat(),
                dt.getNgayHuy(),
                dt.getLyDoHuy(),
                lkh != null ? lkh.getMaLichKhoiHanh() : null,
                lkh != null ? lkh.getNgayKhoiHanh() : null,
                lkh != null ? lkh.getNgayKetThuc() : null,
                tour != null ? tour.getMaTour() : null,
                tour != null ? tour.getTenTour() : null,
                diaDiem,
                tour != null ? tour.getUrlHinhAnhChinh() : null
        );
    }
}
