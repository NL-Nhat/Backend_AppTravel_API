package com.example.travelappapi.service;

import java.time.LocalDateTime;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.example.travelappapi.dto.BookingRequestDTO;
import com.example.travelappapi.dto.ViDienTuResponse;
import com.example.travelappapi.model.DatTour;
import com.example.travelappapi.model.KhachHangThamGia;
import com.example.travelappapi.model.LichKhoiHanh;
import com.example.travelappapi.model.NguoiDung;
import com.example.travelappapi.model.Tour;
import com.example.travelappapi.repository.DatTourRepository;
import com.example.travelappapi.repository.KhachHangThamGiaRepository;
import com.example.travelappapi.repository.LichKhoiHanhRepository;
import com.example.travelappapi.repository.NguoiDungRepository;
import com.example.travelappapi.repository.TourRepository;

import jakarta.transaction.Transactional;

@Service
public class BookingService {

    private final KhachHangThamGiaRepository khRepository;
    private final DatTourRepository datTourRepository;
    private final LichKhoiHanhRepository lichRepository;
    private final NguoiDungRepository nguoiDungRepository;
    private final TourRepository tourRepository;

    public BookingService(KhachHangThamGiaRepository khRepo, DatTourRepository datTourRepo, LichKhoiHanhRepository lichRepo, NguoiDungRepository nguoiDungRepo, TourRepository tourRepository) {
        this.datTourRepository = datTourRepo;
        this.khRepository = khRepo;
        this.lichRepository = lichRepo;
        this.nguoiDungRepository = nguoiDungRepo;
        this.tourRepository = tourRepository;
    }

    @Transactional //kích hoạt roolback nếu bi lỗi
    public int createBooking(BookingRequestDTO dto) {
        LichKhoiHanh lich = lichRepository.findById(dto.getMaLichKhoiHanh())
                                            .orElseThrow( () -> new RuntimeException("Không tìm thấy lịch khởi hàng này"));
        Tour tour = lich.getTour();
        double giaNguoiLon = tour.getGiaNguoiLon();
        double giaTreEm = tour.getGiaTreEm();

        double tongTien = giaNguoiLon * dto.getSoNguoiLon() + giaTreEm * dto.getSoTreEm();

        DatTour datTour = new DatTour();
        KhachHangThamGia khachHangThamGia = new KhachHangThamGia();
        NguoiDung nguoiDung = new NguoiDung();
        nguoiDung = nguoiDungRepository.findById(dto.getMaNguoiDung()).orElseThrow(() -> new RuntimeException("không tìm thấy người dùng này"));

        datTour.setTongTien(tongTien);
        datTour.setNguoiDung(nguoiDung);
        datTour.setSoNguoiLon(dto.getSoNguoiLon());
        datTour.setSoTreEm(dto.getSoTreEm());
        datTour.setLichKhoiHanh(lich);

        DatTour saveBooking = datTourRepository.save(datTour);

        khachHangThamGia.setDatTour(saveBooking);
        khachHangThamGia.setHoTen(dto.getHoTen());
        khachHangThamGia.setDiaChi(dto.getDiaChi());
        khachHangThamGia.setGioiTinh(StringUtils.stripAccents(dto.getGioiTinh())); //bỏ dấu tiếng việt
        khachHangThamGia.setSoDienThoai(dto.getSoDienThoai());
        khachHangThamGia.setNgaySinh(dto.getNgaySinh());
        khRepository.save(khachHangThamGia);

        return saveBooking.getMaDatTour();
    }

    public ViDienTuResponse layThongTin(int maDatTour) {
        ViDienTuResponse viDienTuResponse = new ViDienTuResponse();

        DatTour datTour = datTourRepository.findById(maDatTour)
        .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn đặt tour"));

        NguoiDung nguoiDung = new NguoiDung();
        nguoiDung = datTour.getNguoiDung();

        LichKhoiHanh lichKhoiHanh = datTour.getLichKhoiHanh();
        Tour tour = lichKhoiHanh.getTour();
        
        LocalDateTime ngayDi = lichKhoiHanh.getNgayKhoiHanh();
        String tenTour = tour.getTenTour();
        int soNguoiLon = datTour.getSoNguoiLon();
        int soTreEm = datTour.getSoTreEm();

        viDienTuResponse.setNgayDi(ngayDi);
        viDienTuResponse.setSoNguoiLon(soNguoiLon);
        viDienTuResponse.setSoTreEm(soTreEm);
        viDienTuResponse.setTentour(tenTour);
        viDienTuResponse.setTenKhachHang(nguoiDung.getHoTen());
        viDienTuResponse.setTrangThaiThanhToan(datTour.getTrangThaiThanhToan());

        return viDienTuResponse;
    }
}
