package com.example.travelappapi.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.travelappapi.dto.AdminBookingItemDTO;
import com.example.travelappapi.dto.DatTourHistoryItemDTO;
import com.example.travelappapi.model.DatTour;
import com.example.travelappapi.model.LichKhoiHanh;
import com.example.travelappapi.model.NguoiDung;
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

    public void cancelBookingAsUser(Integer maDatTour, Integer maNguoiDung, String lyDoHuy) {
        DatTour dt = datTourRepository.findById(maDatTour)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy đơn đặt tour"));

        NguoiDung nd = dt.getNguoiDung();
        if (nd == null || !Objects.equals(nd.getMaNguoiDung(), maNguoiDung)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Bạn không có quyền hủy đơn này");
        }

        String currentStatus = Objects.toString(dt.getTrangThaiDatTour(), "");

        // Rule: chỉ cho phép hủy khi đang "Chờ xác nhận"
        if (!"ChoXacNhan".equalsIgnoreCase(currentStatus)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Chỉ được hủy đơn khi đang ở trạng thái Chờ xác nhận");
        }

        dt.setTrangThaiDatTour("DaHuy");
        dt.setNgayHuy(LocalDateTime.now());
        dt.setLyDoHuy(lyDoHuy);
        datTourRepository.save(dt);
    }

    public List<AdminBookingItemDTO> getBookingsForAdmin(String status, String q) {
        return datTourRepository.searchBookingsForAdmin(status, q)
                .stream()
                .map(this::toAdminDto)
                .collect(Collectors.toList());
    }

    public AdminBookingItemDTO getBookingDetailForAdmin(Integer maDatTour) {
        DatTour dt = datTourRepository.findById(maDatTour)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy đơn đặt tour"));
        return toAdminDto(dt);
    }

    public void confirmBookingAsAdmin(Integer maDatTour) {
        DatTour dt = datTourRepository.findById(maDatTour)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy đơn đặt tour"));

        String currentStatus = Objects.toString(dt.getTrangThaiDatTour(), "");
        if (!"ChoXacNhan".equalsIgnoreCase(currentStatus)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Chỉ được xác nhận đơn khi đang ở trạng thái Chờ xác nhận");
        }

        dt.setTrangThaiDatTour("DaXacNhan");
        datTourRepository.save(dt);
    }

    public void cancelBookingAsAdmin(Integer maDatTour, String lyDoHuy) {
        DatTour dt = datTourRepository.findById(maDatTour)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy đơn đặt tour"));

        String currentStatus = Objects.toString(dt.getTrangThaiDatTour(), "");
        if ("DaHuy".equalsIgnoreCase(currentStatus)) {
            // idempotent-ish
            return;
        }

        dt.setTrangThaiDatTour("DaHuy");
        dt.setNgayHuy(LocalDateTime.now());
        dt.setLyDoHuy(lyDoHuy);
        datTourRepository.save(dt);
    }

    private DatTourHistoryItemDTO toDto(DatTour dt) {
        LichKhoiHanh lkh = dt.getLichKhoiHanh();
        Tour tour = (lkh != null ? lkh.getTour() : null);

        String diaDiem = resolveDiaDiem(tour);

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

    private AdminBookingItemDTO toAdminDto(DatTour dt) {
        LichKhoiHanh lkh = dt.getLichKhoiHanh();
        Tour tour = (lkh != null ? lkh.getTour() : null);
        NguoiDung nd = dt.getNguoiDung();

        String diaDiem = resolveDiaDiem(tour);

        return new AdminBookingItemDTO(
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
                tour != null ? tour.getUrlHinhAnhChinh() : null,
                nd != null ? nd.getMaNguoiDung() : null,
                nd != null ? nd.getHoTen() : null,
                nd != null ? nd.getEmail() : null,
                nd != null ? nd.getSoDienThoai() : null,
                nd != null ? nd.getAnhDaiDien() : null
        );
    }

    private String resolveDiaDiem(Tour tour) {
        if (tour == null || tour.getDiemDen() == null) {
            return null;
        }

        String diaDiem = Objects.toString(tour.getDiemDen().getThanhPho(), null);
        if (diaDiem == null || diaDiem.isBlank()) {
            diaDiem = Objects.toString(tour.getDiemDen().getTenDiemDen(), null);
        }
        return diaDiem;
    }
}

