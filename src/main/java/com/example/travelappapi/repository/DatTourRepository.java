package com.example.travelappapi.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.travelappapi.model.DatTour;

@Repository
public interface DatTourRepository extends JpaRepository<DatTour, Integer> {

    List<DatTour> findByNguoiDung_MaNguoiDungOrderByNgayDatDesc(Integer maNguoiDung);

    List<DatTour> findByNguoiDung_MaNguoiDungAndTrangThaiDatTourOrderByNgayDatDesc(Integer maNguoiDung, String trangThaiDatTour);

    // Admin queries
    @org.springframework.data.jpa.repository.Query("""
            select dt from DatTour dt
            join dt.nguoiDung nd
            left join dt.lichKhoiHanh lkh
            left join lkh.tour t
            where (:status is null or :status = '' or lower(dt.trangThaiDatTour) = lower(:status))
              and (
                :q is null or :q = ''
                or lower(nd.hoTen) like lower(concat('%', :q, '%'))
                or lower(nd.email) like lower(concat('%', :q, '%'))
                or lower(nd.soDienThoai) like lower(concat('%', :q, '%'))
                or lower(t.tenTour) like lower(concat('%', :q, '%'))
                or concat('', dt.maDatTour) like concat('%', :q, '%')
              )
            order by dt.ngayDat desc
            """)
    List<DatTour> searchBookingsForAdmin(@org.springframework.data.repository.query.Param("status") String status,
                                        @org.springframework.data.repository.query.Param("q") String q);

    // Kiểm tra xem có đơn đặt nào thuộc về mã lịch trình này không, nếu có thì không xóa lịch được
    boolean existsByLichKhoiHanh_MaLichKhoiHanh(Integer maLich);

}
