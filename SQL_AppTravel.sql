-- Kiem tra xem database da ton tai hay chua, ton tai thi xoa
IF EXISTS (SELECT * FROM sys.databases WHERE name = N'dbAppTravel')
BEGIN
    -- Dong tat ca cac ket noi den co so du lieu
    EXECUTE sp_MSforeachdb 'IF ''?'' = ''dbAppTravel''
    BEGIN
        DECLARE @sql AS NVARCHAR(MAX) = ''USE [?]; ALTER DATABASE [?] SET SINGLE_USER WITH ROLLBACK IMMEDIATE;''
        EXEC (@sql)
    END'
    -- Xoa tat ca cac ket noi toi co so du lieu (thuc hien qua he thong master)
    USE MASTER
    -- Xoa co so du lieu neu ton tai
    DROP DATABASE dbAppTravel
END

CREATE DATABASE dbAppTravel
COLLATE Vietnamese_CS_AS;
GO

USE dbAppTravel;
GO
ALTER DATABASE dbAppTravel SET MULTI_USER;
GO

CREATE TABLE NguoiDung (
    maNguoiDung INT IDENTITY(1,1) PRIMARY KEY,
    tenDangNhap NVARCHAR(50) NOT NULL UNIQUE,
    email NVARCHAR(100) NOT NULL UNIQUE,
    matKhau VARCHAR(255) NOT NULL,
    hoTen NVARCHAR(100) NOT NULL,
    soDienThoai NVARCHAR(15),
    diaChi NVARCHAR(255),
    ngaySinh DATE,
    gioiTinh NVARCHAR(10) CHECK (gioiTinh IN ('Nam', 'Nu')),
    anhDaiDien NVARCHAR(255) DEFAULT 'https://i.pravatar.cc/150?img=5', -- Ảnh đại diện mặc định
    vaiTro NVARCHAR(20) NOT NULL DEFAULT 'KhachHang' CHECK (vaiTro IN ('Admin', 'KhachHang', 'HuongDanVien')),
    trangThai NVARCHAR(20) NOT NULL DEFAULT 'HoatDong' CHECK (trangThai IN ('HoatDong', 'Khoa')),
    thoiGianTao DATETIME DEFAULT GETDATE()
);

CREATE TABLE DiemDen (
    maDiemDen INT IDENTITY(1,1) PRIMARY KEY,
    tenDiemDen NVARCHAR(100) NOT NULL,
    thanhPho NVARCHAR(50) NOT NULL,
    moTa NVARCHAR(1000),
    urlHinhAnh NVARCHAR(255)
);

CREATE TABLE Tour (
    maTour INT IDENTITY(1,1) PRIMARY KEY,
    tenTour NVARCHAR(200) NOT NULL,
    maDiemDen INT NOT NULL,
    moTa NVARCHAR(2000),
    giaNguoiLon DECIMAL(18, 2) NOT NULL check (giaNguoiLon > 0), 
    giaTreEm DECIMAL(18, 2) check (giaTreEm >= 0),
    urlHinhAnhChinh NVARCHAR(255),
    diemDanhGiaTrungBinh DECIMAL(3,1) DEFAULT 0,
    soLuongDanhGia INT DEFAULT 0,
    trangThai NVARCHAR(20) DEFAULT 'DangMo' CHECK (trangThai IN ('DangMo', 'TamDung')),
    
    CONSTRAINT FK_Tour_DiemDen FOREIGN KEY (maDiemDen) REFERENCES DiemDen(maDiemDen)
);

CREATE TABLE LichKhoiHanh (
    maLichKhoiHanh INT IDENTITY(1,1) PRIMARY KEY,
    maTour INT NOT NULL,
    ngayKhoiHanh DATETIME NOT NULL,
    ngayKetThuc DATETIME NOT NULL,
    huongDanVien INT, --ID
    soLuongKhachToiDa INT NOT NULL CHECK (soLuongKhachToiDa > 0),
    soLuongKhachDaDat INT not null DEFAULT 0 CHECK (soLuongKhachDaDat >= 0)
    
    CONSTRAINT FK_LichKhoiHanh_Tour FOREIGN KEY (maTour) REFERENCES Tour(maTour),
    CONSTRAINT FK_LichKhoiHanh_HDV FOREIGN KEY (huongDanVien) REFERENCES NguoiDung(maNguoiDung),
    CONSTRAINT CK_Ngay CHECK (ngayKetThuc >= ngayKhoiHanh)
);

CREATE TABLE HinhAnhTour (
    maHinhAnh INT IDENTITY(1,1) PRIMARY KEY,
    maTour INT NOT NULL,
    urlHinhAnh NVARCHAR(255) NOT NULL,
    tieuDeHinhAnh NVARCHAR(100),
    laChinh BIT DEFAULT 0
    
    CONSTRAINT FK_HinhAnhTour_Tour FOREIGN KEY (maTour) REFERENCES Tour(maTour) ON DELETE CASCADE
);

CREATE TABLE DatTour (
    maDatTour INT IDENTITY(1,1) PRIMARY KEY,
    maNguoiDung INT NOT NULL,
    maLichKhoiHanh int not null,
    soNguoiLon INT NOT NULL DEFAULT 1 CHECK (soNguoiLon > 0),
    soTreEm INT DEFAULT 0 CHECK (soTreEm >= 0),
    tongTien DECIMAL(18,2) NOT NULL CHECK (tongTien >= 0),
    trangThaiDatTour NVARCHAR(20) NOT NULL DEFAULT 'ChoXacNhan' CHECK (trangThaiDatTour IN ('ChoXacNhan', 'DaXacNhan', 'DaHuy')),
    trangThaiThanhToan NVARCHAR(20) NOT NULL DEFAULT 'ChuaThanhToan' CHECK (trangThaiThanhToan IN ('ChuaThanhToan', 'DaThanhToan')),
    ngayDat DATETIME DEFAULT GETDATE(),
    ngayHuy DATETIME,
    lyDoHuy NVARCHAR(500),
    
    CONSTRAINT FK_DatTour_NguoiDung FOREIGN KEY (maNguoiDung) REFERENCES NguoiDung(maNguoiDung),
    CONSTRAINT FK_DatTour_Lich FOREIGN KEY (maLichKhoiHanh) REFERENCES LichKhoiHanh(maLichKhoiHanh)
);

CREATE TABLE ThanhToan (
    maThanhToan INT IDENTITY(1,1) PRIMARY KEY,
    maDatTour INT NOT NULL unique,
    phuongThucThanhToan NVARCHAR(20) NOT NULL CHECK (phuongThucThanhToan IN ('ChuyenKhoan', 'TheTinDung', 'ViDienTu')),
    soTien DECIMAL(18,2) NOT NULL CHECK (soTien > 0),
    ngayThanhToan DATETIME not null DEFAULT GETDATE(),
    
    CONSTRAINT FK_ThanhToan_DatTour FOREIGN KEY (maDatTour) REFERENCES DatTour(maDatTour)
);

CREATE TABLE DanhGia (
    maDanhGia INT IDENTITY(1,1) PRIMARY KEY,
    maTour INT NOT NULL,
    maNguoiDung INT NOT NULL,
    diemSo DECIMAL(3,1) NOT NULL CHECK (diemSo BETWEEN 0 AND 5),
    binhLuan NVARCHAR(max),
    thoiGianTao DATETIME DEFAULT GETDATE(),
    
    CONSTRAINT FK_DanhGia_Tour FOREIGN KEY (maTour) REFERENCES Tour(maTour) on delete CASCADE,
    CONSTRAINT FK_DanhGia_NguoiDung FOREIGN KEY (maNguoiDung) REFERENCES NguoiDung(maNguoiDung)
);

CREATE TABLE KhachHangThamGia (
    maKhachHang INT IDENTITY(1,1) PRIMARY KEY,
    maDatTour INT NOT NULL,
    hoTen NVARCHAR(100) NOT NULL,
    ngaySinh DATE,
    soDienThoai NVARCHAR(15),
    gioiTinh NVARCHAR(10) CHECK (gioiTinh IN ('Nam', 'Nu')) default 'Nam',
    diaChi NVARCHAR(255)
    
    CONSTRAINT FK_KhachHangThamGia_DatTour FOREIGN KEY (maDatTour) REFERENCES DatTour(maDatTour) ON DELETE CASCADE
);

CREATE TABLE TourYeuThich (
    maNguoiDung INT,
    maTour INT,
    ngayLuu DATETIME DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT PK_TourYeuThich PRIMARY KEY (maNguoiDung, maTour),
    CONSTRAINT FK_TourYeuThich_NguoiDung FOREIGN KEY (maNguoiDung) REFERENCES NguoiDung(maNguoiDung),
    CONSTRAINT FK_TourYeuThich_Tour FOREIGN KEY (maTour) REFERENCES Tour(maTour)
);

-- 7. Bảng Yêu Cầu Tour (Prompt của người dùng)
CREATE TABLE YeuCauTourAI (
    maYeuCau INT IDENTITY(1,1) PRIMARY KEY,
    maNguoiDung INT,
    noiDungYeuCau nvarchar(max),
    ngayYeuCau DATETIME DEFAULT Getdate(), 
    CONSTRAINT FK_YeuCauTourAI_NguoiDung FOREIGN KEY (maNguoiDung) REFERENCES NguoiDung(maNguoiDung)
);

CREATE TABLE LichTrinhDeXuat (
    maLichTrinh INT Identity(1,1) PRIMARY KEY,
    maYeuCau INT not null,
    tieuDeTour NVARCHAR(200),
    noiDungDeXuat NVARCHAR(MAX),
    tongTienDuKien DECIMAL(18,2) check(tongTienDuKien >= 0),
    trangThai nvarchar(20) default N'DaLuu' check (trangThai in (N'DaLuu', N'ChuaLuu')),
    CONSTRAINT FK_LichTrinhDeXuat_YeuCauTourAI FOREIGN KEY (maYeuCau) REFERENCES YeuCauTourAI(maYeuCau) ON DELETE CASCADE
);
go

CREATE TABLE GuiYeuCauTour (
    maYeuCauTour int IDENTITY(1,1) primary key,
    maLichTrinh int,
    ngayGui datetime default getdate(),
    trangThai nvarchar(20) default N'ChoDuyet' check(trangThai in (N'ChoDuyet', N'DaDuyet', N'DaHuy')),
    CONSTRAINT FK_GuiYeuCauTour_LichTrinhDeXuat FOREIGN KEY (maLichTrinh) REFERENCES LichTrinhDeXuat(maLichTrinh) ON DELETE CASCADE
);
go

CREATE TRIGGER trg_CapNhatDanhGia_Tour
ON DanhGia
AFTER INSERT, UPDATE, DELETE
AS
BEGIN
    SET NOCOUNT ON;

    -- 1. Tìm danh sách các maTour bị thay đổi (Bao gồm cả trường hợp Thêm, Sửa, Xóa)
    -- Sử dụng bảng ảo 'inserted' (dữ liệu mới) và 'deleted' (dữ liệu cũ)
    DECLARE @AffectedTours TABLE (maTour INT);

    INSERT INTO @AffectedTours (maTour)
    SELECT maTour FROM inserted
    UNION -- Dùng UNION để loại bỏ trùng lặp nếu vừa có insert vừa có delete cùng 1 tour
    SELECT maTour FROM deleted;

    -- 2. Cập nhật lại bảng Tour dựa trên dữ liệu tổng hợp mới nhất từ bảng DanhGia
    UPDATE T
    SET 
        T.soLuongDanhGia = ISNULL(Agg.SoLuong, 0),
        T.diemDanhGiaTrungBinh = ISNULL(Agg.DiemTrungBinh, 0)
    FROM Tour T
    INNER JOIN @AffectedTours AF ON T.maTour = AF.maTour -- Chỉ update những tour bị ảnh hưởng
    OUTER APPLY (
        SELECT 
            COUNT(*) AS SoLuong, 
            AVG(CAST(diemSo AS DECIMAL(10, 2))) AS DiemTrungBinh -- CAST để tính toán chính xác hơn
        FROM DanhGia DG
        WHERE DG.maTour = T.maTour
    ) Agg;
END;
GO


CREATE TRIGGER trg_CapNhatSoLuongKhachDat
ON DatTour
AFTER INSERT, UPDATE, DELETE
AS
BEGIN
    SET NOCOUNT ON;

    -- BƯỚC 1: Xác định các Lịch Khởi Hành bị ảnh hưởng
    -- (Dùng UNION để lấy ID từ cả bảng inserted và deleted để xử lý trường hợp Thêm, Sửa, Xóa)
    DECLARE @LichBiAnhHuong TABLE (maLichKhoiHanh INT);

    INSERT INTO @LichBiAnhHuong (maLichKhoiHanh)
    SELECT maLichKhoiHanh FROM inserted WHERE maLichKhoiHanh IS NOT NULL
    UNION
    SELECT maLichKhoiHanh FROM deleted WHERE maLichKhoiHanh IS NOT NULL;

    -- BƯỚC 2: Tính toán lại và Cập nhật số lượng
    UPDATE L
    SET L.soLuongKhachDaDat = ISNULL(Cal.TongSoKhach, 0)
    FROM LichKhoiHanh L
    INNER JOIN @LichBiAnhHuong A ON L.maLichKhoiHanh = A.maLichKhoiHanh
    OUTER APPLY (
        SELECT SUM(soNguoiLon + soTreEm) AS TongSoKhach
        FROM DatTour D
        WHERE D.maLichKhoiHanh = L.maLichKhoiHanh
          AND D.trangThaiDatTour <> 'DaHuy' -- Quan trọng: Không đếm đơn đã hủy
    ) Cal;

    -- BƯỚC 3: KIỂM TRA OVERBOOKING (Quá số lượng cho phép)
    -- Nếu sau khi update mà số khách đã đặt > số khách tối đa -> Báo lỗi và Hoàn tác
    IF EXISTS (
        SELECT 1 
        FROM LichKhoiHanh L
        JOIN @LichBiAnhHuong A ON L.maLichKhoiHanh = A.maLichKhoiHanh
        WHERE L.soLuongKhachDaDat > L.soLuongKhachToiDa
    )
    BEGIN
        RAISERROR (N'Lỗi: Số lượng khách đặt vượt quá số chỗ còn nhận của Tour này!', 16, 1);
        ROLLBACK TRANSACTION; -- Hủy thao tác vừa làm
        RETURN;
    END
END;
GO


-- BẢNG 1: NGUOIDUNG (20 Users: 2 Admin, 5 HDV, 13 Khách)
INSERT INTO NguoiDung (tenDangNhap, email, matKhau, hoTen, soDienThoai, diaChi, ngaySinh, gioiTinh, anhDaiDien, vaiTro, trangThai, thoiGianTao) VALUES
(N'admin2', N'admin@travel.com', N'$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.TVuHOn2', N'Quản Trị Viên', N'0901234567', N'Hà Nội', '1985-05-10', N'Nam', N'admin.jpg', N'Admin', N'HoatDong', GETDATE()),
(N'hdv_tuan', N'tuanhdv@travel.com', N'$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.TVuHOn2', N'Trần Anh Tuấn', N'0907654321', N'Đà Nẵng', '1992-08-20', N'Nam', N'hdv_tuan.jpg', N'HuongDanVien', N'HoatDong', GETDATE()),
(N'nhat', N'nhat@gmail.com', N'$2a$10$X7kmatqMdm9KT..Jgk9OCO/4LirhL5Zul/lsMuI7gMlkIn0MVYK/.', N'Nguyễn Long Nhật', N'0988123456', N'Quảng Trị', '1999-01-01', N'Nam', N'luffy.jpg', N'KhachHang', N'HoatDong', GETDATE()),
(N'kh_an', N'annguyen@gmail.com', N'$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.TVuHOn2', N'Nguyễn Thúy An', N'0911223344', N'HCM', '2000-02-15', N'Nu', N'an_avatar.jpg', N'KhachHang', N'HoatDong', GETDATE());
GO

-- BẢNG 2: DIEMDEN (20 Địa điểm)
INSERT INTO DiemDen (tenDiemDen, thanhPho, moTa, urlHinhAnh) VALUES
(N'Bà Nà Hills', N'Đà Nẵng', N'Khu du lịch SunWorld, Cầu Vàng nổi tiếng.', N'bana.jpg'),
(N'Phố Cổ Hội An', N'Quảng Nam', N'Di sản văn hóa thế giới với đèn lồng rực rỡ.', N'hoian.jpg'),
(N'Vịnh Hạ Long', N'Quảng Ninh', N'Kỳ quan thiên nhiên thế giới với hàng nghìn đảo đá.', N'halong.jpg'),
(N'VinWonders Phú Quốc', N'Kiên Giang', N'Tổ hợp vui chơi giải trí hàng đầu.', N'vinpq.jpg'),
(N'Thung Lũng Tình Yêu', N'Đà Lạt', N'Không gian thơ mộng cho các cặp đôi.', N'dalat.jpg'),
(N'Tràng An', N'Ninh Bình', N'Quần thể danh thắng di sản kép.', N'trangan.jpg'),
(N'Cố Đô Huế', N'Thừa Thiên Huế', N'Di tích lịch sử triều Nguyễn.', N'hue.jpg'),
(N'Phong Nha Kẻ Bàng', N'Quảng Bình', N'Hệ thống hang động kỳ vĩ.', N'phongnha.jpg'),
(N'Chợ Nổi Cái Răng', N'Cần Thơ', N'Đặc trưng văn hóa sông nước miền Tây.', N'cairang.jpg'),
(N'Mù Cang Chải', N'Yên Bái', N'Ruộng bậc thang đẹp nhất vùng Tây Bắc.', N'mucangchai.jpg'),
(N'Đỉnh Fansipan', N'Lào Cai', N'Nóc nhà Đông Dương.', N'fansipan.jpg'),
(N'Côn Đảo', N'Bà Rịa - Vũng Tàu', N'Vùng đất tâm linh và biển xanh.', N'condao.jpg'),
(N'Mũi Né', N'Bình Thuận', N'Đồi cát trắng và biển xanh.', N'muine.jpg'),
(N'Thác Bản Giốc', N'Cao Bằng', N'Thác nước hùng vĩ biên giới.', N'bangioc.jpg'),
(N'Tam Đảo', N'Vĩnh Phúc', N'Thị trấn mờ sương.', N'tamdao.jpg'),
(N'Biển Mỹ Khê', N'Đà Nẵng', N'Một trong những bãi biển quyến rũ nhất hành tinh.', N'mykhe.jpg'),
(N'Eo Gió', N'Bình Định', N'Con đường ven biển Quy Nhơn đẹp mê hồn.', N'eogio.jpg'),
(N'Thánh Địa Mỹ Sơn', N'Quảng Nam', N'Khu đền tháp Chăm Pa cổ.', N'myson.jpg'),
(N'Vườn Quốc Gia Cát Tiên', N'Đồng Nai', N'Khám phá thiên nhiên hoang dã.', N'cattien.jpg'),
(N'Hồ Hoàn Kiếm', N'Hà Nội', N'Trái tim của thủ đô nghìn năm văn hiến.', N'hoankiem.jpg');
GO

-- BẢNG 3: TOUR (20 Tours)
INSERT INTO Tour (tenTour, maDiemDen, moTa, giaNguoiLon, giaTreEm, urlHinhAnhChinh, diemDanhGiaTrungBinh, soLuongDanhGia, trangThai) VALUES
(N'Tour Bà Nà Hills 1 Ngày', 1, N'Cầu Vàng, Làng Pháp, buffet trưa.', 1250000, 950000, N'bana.jpg', 0, 0, N'DangMo'),
(N'Hội An Đêm Rực Rỡ', 2, N'Tham quan phố cổ, thả đèn hoa đăng.', 800000, 400000, N'hoian.jpg', 0, 0, N'DangMo'),
(N'Du Thuyền Hạ Long 5 Sao', 3, N'2 ngày 1 đêm trên vịnh.', 3500000, 2500000, N'halong.jpg', 0, 0, N'DangMo'),
(N'Phú Quốc Nghỉ Dưỡng', 4, N'3 ngày 2 đêm tại đảo ngọc.', 4500000, 3000000, N'phuquoc.jpg', 0, 0, N'DangMo'),
(N'Đà Lạt Săn Mây', 5, N'Đón bình minh đồi chè Cầu Đất.', 700000, 350000, N'dalat.jpg', 0, 0, N'DangMo'),
(N'Ninh Bình - Tràng An', 6, N'Thăm Bái Đính, xuôi dòng Tràng An.', 950000, 700000, N'trangan.jpg', 0, 0, N'DangMo'),
(N'Huế Kinh Kỳ', 7, N'Đại Nội, lăng tẩm, chùa Thiên Mụ.', 800000, 500000, N'hue.jpg', 0, 0, N'DangMo'),
(N'Khám Phá Hang Động Quảng Bình', 8, N'Động Thiên Đường, Phong Nha.', 1500000, 1100000, N'phongnha.jpg', 0, 0, N'DangMo'),
(N'Miền Tây Sông Nước', 9, N'Chợ nổi, miệt vườn trái cây.', 1200000, 800000, N'mientay.jpg', 0, 0, N'DangMo'),
(N'Mùa Vàng Mù Cang Chải', 10, N'Ngắm ruộng bậc thang chín vàng.', 2500000, 2000000, N'mu.jpg', 0, 0, N'DangMo'),
(N'Chinh Phục Fansipan', 11, N'Cáp treo Fansipan Legend.', 1600000, 1100000, N'fansipan.jpg', 0, 0, N'DangMo'),
(N'Côn Đảo Tâm Linh', 12, N'Viếng mộ chị Sáu, tham quan nhà tù.', 4800000, 3800000, N'condao.jpg', 0, 0, N'DangMo'),
(N'Mũi Né Jeep Tour', 13, N'Đồi cát trắng, Bàu Trắng.', 600000, 300000, N'muine.jpg', 0, 0, N'DangMo'),
(N'Cao Bằng - Bản Giốc', 14, N'Thác Bản Giốc hùng vĩ.', 2800000, 2100000, N'caobang.jpg', 0, 0, N'DangMo'),
(N'Tam Đảo Cuối Tuần', 15, N'Nghỉ dưỡng không khí trong lành.', 500000, 300000, N'tamdao.jpg', 0, 0, N'DangMo'),
(N'Đà Nẵng City Tour', 16, N'Bán đảo Sơn Trà, Ngũ Hành Sơn.', 750000, 500000, N'danang.jpg', 0, 0, N'DangMo'),
(N'Quy Nhơn - Eo Gió', 17, N'Kỳ Co, Eo Gió, Tượng Phật.', 900000, 600000, N'quynhon.jpg', 0, 0, N'DangMo'),
(N'Mỹ Sơn Cổ Kính', 18, N'Thánh địa Mỹ Sơn di sản.', 650000, 400000, N'myson.jpg', 0, 0, N'DangMo'),
(N'Trekking Cát Tiên', 19, N'Rừng Nam Cát Tiên, Bàu Sấu.', 1400000, 1000000, N'cattien.jpg', 0, 0, N'DangMo'),
(N'Hà Nội 36 Phố Phường', 20, N'Food tour Hà Nội phố cổ.', 450000, 300000, N'hanoi.jpg', 0, 0, N'DangMo');
GO

-- BẢNG 4: LICHKHOIHANH (20 Lịch trình)
--soLuongKhachDaDat để mặc định là 0, trigger sẽ tự tính khi insert DatTour
INSERT INTO LichKhoiHanh (maTour, ngayKhoiHanh, ngayKetThuc, huongDanVien, soLuongKhachToiDa, soLuongKhachDaDat) VALUES
(1, '2026-01-01 08:00:00', '2026-01-01 17:00:00', 2, 40, 0),
(1, '2026-01-15 08:00:00', '2026-01-15 17:00:00', 2, 40, 0),
(2, '2026-01-02 14:00:00', '2026-01-02 21:00:00', 2, 30, 0),
(2, '2026-02-02 14:00:00', '2026-02-02 21:00:00', 2, 30, 0),
(3, '2026-01-05 08:00:00', '2026-01-06 12:00:00', 2, 20, 0),
(4, '2026-01-10 09:00:00', '2026-01-12 18:00:00', 2, 25, 0),
(4, '2026-02-10 09:00:00', '2026-02-12 18:00:00', 2, 25, 0),
(5, '2026-01-03 04:00:00', '2026-01-03 16:00:00', 2, 15, 0),
(6, '2026-01-07 07:00:00', '2026-01-07 18:00:00', 2, 40, 0),
(7, '2026-01-12 08:00:00', '2026-01-12 17:00:00', 2, 25, 0),
(8, '2026-01-20 07:00:00', '2026-01-20 18:00:00', 2, 15, 0),
(9, '2026-01-25 05:00:00', '2026-01-26 18:00:00', 2, 20, 0),
(10, '2026-09-15 06:00:00', '2026-09-17 18:00:00', 2, 12, 0),
(11, '2026-01-30 08:00:00', '2026-01-30 20:00:00', 2, 40, 0),
(12, '2026-01-15 06:00:00', '2026-01-17 14:00:00', 2, 20, 0),
(13, '2026-01-22 05:00:00', '2026-01-22 10:00:00', 2, 15, 0),
(14, '2026-01-28 05:00:00', '2026-01-30 20:00:00', 2, 20, 0),
(15, '2026-01-05 08:00:00', '2026-01-06 17:00:00', 2, 10, 0),
(16, '2026-01-10 08:00:00', '2026-01-10 17:00:00', 2, 35, 0),
(17, '2026-01-18 07:00:00', '2026-01-18 17:00:00', 2, 25, 0),
(18, '2026-01-24 08:00:00', '2026-01-24 12:00:00', 2, 30, 0),
(19, '2026-01-11 06:00:00', '2026-01-12 17:00:00', 2, 12, 0),
(20, '2026-01-03 17:00:00', '2026-01-03 21:00:00', 2, 15, 0),
(3, '2026-02-05 08:00:00', '2026-02-06 12:00:00', 2, 20, 0),
(11, '2026-02-28 08:00:00', '2026-02-28 20:00:00', 2, 40, 0);
GO

-- BẢNG 5: HINHANHTOUR (~20 ảnh)
INSERT INTO HinhAnhTour (maTour, urlHinhAnh, tieuDeHinhAnh, laChinh) VALUES
(1, N'bana_cauvang.jpg', N'Cầu Vàng', 1), (1, N'bana_langphap.jpg', N'Làng Pháp', 0),
(2, N'hoian_denlong.jpg', N'Phố đèn lồng', 1), (2, N'hoian_chua.jpg', N'Chùa Cầu', 0),
(3, N'halong_du.jpg', N'Du thuyền 5 sao', 1), (3, N'halong_cave.jpg', N'Hang Sửng Sốt', 0),
(4, N'pq_bien.jpg', N'Bãi Sao', 1),
(5, N'dalat_may.jpg', N'Săn mây', 1),
(6, N'trangan_thuyen.jpg', N'Bến thuyền', 1),
(7, N'hue_dainoi.jpg', N'Ngọ Môn', 1),
(8, N'phongnha_dong.jpg', N'Thạch nhũ', 1),
(9, N'mientay_cho.jpg', N'Chợ nổi Cái Răng', 1),
(10, N'taybac_lua.jpg', N'Ruộng bậc thang', 1),
(11, N'sapa_dinh.jpg', N'Đỉnh Fansipan', 1),
(12, N'condao_bien.jpg', N'Biển Côn Đảo', 1),
(14, N'bangioc.jpg', N'Thác Bản Giốc', 1),
(17, N'danang_rong.jpg', N'Cầu Rồng', 1),
(18, N'eogio.jpg', N'Eo Gió', 1),
(19, N'myson.jpg', N'Tháp Chàm', 1),
(20, N'pho.jpg', N'Phở Hà Nội', 1),
(13, N'muine_cat.jpg', N'Đồi cát', 1);
GO

-- BẢNG 6: DATTOUR (20 Bookings)
-- Chú ý: Trigger sẽ tự update số lượng khách vào bảng LichKhoiHanh
INSERT INTO DatTour (maNguoiDung, maLichKhoiHanh, soNguoiLon, soTreEm, tongTien, trangThaiDatTour, trangThaiThanhToan, ngayDat, ngayHuy, lyDoHuy) VALUES
(3, 1, 2, 0, 2500000, N'DaXacNhan', N'DaThanhToan', '2025-12-01', NULL, NULL),
(3, 3, 1, 1, 1200000, N'ChoXacNhan', N'ChuaThanhToan', '2025-12-02', NULL, NULL),
(3, 5, 2, 0, 7000000, N'DaXacNhan', N'DaThanhToan', '2025-12-03', NULL, NULL),
(3, 6, 2, 1, 12000000, N'DaHuy', N'ChuaThanhToan', '2025-12-04', '2025-12-05', N'Bận đột xuất'),
(3, 8, 2, 0, 1400000, N'DaXacNhan', N'DaThanhToan', '2025-12-05', NULL, NULL),
(3, 9, 3, 0, 2850000, N'ChoXacNhan', N'ChuaThanhToan', '2025-12-06', NULL, NULL),
(3, 10, 2, 0, 1600000, N'DaXacNhan', N'DaThanhToan', '2025-12-07', NULL, NULL),
(3, 11, 2, 0, 3000000, N'DaXacNhan', N'DaThanhToan', '2025-12-08', NULL, NULL),
(3, 12, 1, 0, 1200000, N'ChoXacNhan', N'DaThanhToan', '2025-12-09', NULL, NULL),
(3, 13, 2, 0, 5000000, N'DaHuy', N'DaThanhToan', '2025-12-10', '2025-12-11', N'Nhầm ngày'),
(3, 14, 2, 0, 3200000, N'DaXacNhan', N'ChuaThanhToan', '2025-12-11', NULL, NULL),
(3, 15, 2, 0, 9600000, N'ChoXacNhan', N'ChuaThanhToan', '2025-12-12', NULL, NULL),
(3, 16, 4, 0, 2400000, N'DaXacNhan', N'DaThanhToan', '2025-12-13', NULL, NULL),
(3, 17, 2, 1, 7700000, N'DaXacNhan', N'DaThanhToan', '2025-12-14', NULL, NULL),
(3, 18, 2, 0, 1000000, N'ChoXacNhan', N'ChuaThanhToan', '2025-12-15', NULL, NULL),
(3, 19, 2, 0, 1500000, N'DaXacNhan', N'DaThanhToan', '2025-12-16', NULL, NULL),
(3, 20, 2, 0, 1800000, N'DaXacNhan', N'DaThanhToan', '2025-12-17', NULL, NULL),
(3, 21, 2, 0, 1300000, N'DaXacNhan', N'DaThanhToan', '2025-12-18', NULL, NULL),
(3, 22, 2, 0, 2800000, N'ChoXacNhan', N'ChuaThanhToan', '2025-12-19', NULL, NULL),
(3, 23, 1, 0, 450000, N'DaXacNhan', N'DaThanhToan', '2025-12-20', NULL, NULL);
GO

-- BẢNG 7: THANHTOAN (~15 Giao dịch)
INSERT INTO ThanhToan (maDatTour, phuongThucThanhToan, soTien, ngayThanhToan) VALUES
(1, N'TheTinDung', 2500000, '2025-12-01'),
(3, N'ChuyenKhoan', 7000000, '2025-12-03'),
(5, N'ViDienTu', 1400000, '2025-12-05'),
(7, N'ChuyenKhoan', 1600000, '2025-12-07'),
(8, N'ViDienTu', 3000000, '2025-12-08'),
(9, N'ChuyenKhoan', 1200000, '2025-12-09'),
(10, N'TheTinDung', 5000000, '2025-12-10'),
(13, N'ViDienTu', 2400000, '2025-12-13'),
(14, N'ChuyenKhoan', 7700000, '2025-12-14'),
(16, N'TheTinDung', 1500000, '2025-12-16'),
(17, N'ViDienTu', 1800000, '2025-12-17'),
(18, N'ChuyenKhoan', 1300000, '2025-12-18'),
(20, N'ViDienTu', 450000, '2025-12-20');
GO

-- BẢNG 8: DANHGIA (20 Reviews)
-- Trigger sẽ update vào bảng Tour
INSERT INTO DanhGia (maTour, maNguoiDung, diemSo, binhLuan, thoiGianTao) VALUES
(1, 3, 5.0, N'Tuyệt vời!', GETDATE()), (2, 4, 4.0, N'Rất đẹp', GETDATE()),
(3, 3, 4.5, N'Dịch vụ tốt', GETDATE()), (4, 4, 5.0, N'Đáng tiền', GETDATE()),
(5, 3, 4.0, N'Hơi lạnh', GETDATE()), (6, 4, 5.0, N'Cảnh đẹp', GETDATE()),
(7, 3, 4.0, N'Nhiều di tích', GETDATE()), (8, 4, 4.5, N'Thú vị', GETDATE()),
(9, 3, 5.0, N'Vui vẻ', GETDATE()), (10, 4, 4.0, N'Ruộng đẹp', GETDATE()),
(11, 3, 5.0, N'Hùng vĩ', GETDATE()), (12, 4, 4.5, N'Linh thiêng', GETDATE()),
(13, 3, 4.0, N'Cát mịn', GETDATE()), (14, 4, 5.0, N'Nước trong', GETDATE()),
(15, 3, 3.5, N'Hơi đông', GETDATE()), (16, 4, 4.0, N'Biển sạch', GETDATE()),
(17, 3, 5.0, N'Eo gió đẹp', GETDATE()), (18, 4, 4.0, N'Cổ kính', GETDATE()),
(19, 3, 4.5, N'Rừng mát', GETDATE()), (20, 4, 5.0, N'Đồ ăn ngon', GETDATE());
GO

-- BẢNG 9: KHACHHANGTHAMGIA (Chi tiết người đi - ~20 người)
INSERT INTO KhachHangThamGia (maDatTour, hoTen, ngaySinh, soDienThoai, gioiTinh, diaChi) VALUES
(1, N'Nguyễn Long Nhật', '1999-01-01', N'0988123456', N'Nam', N'Quảng Trị'),
(1, N'Khách 0', '1999-05-05', NULL, N'Nam', N'Hà Nội'),
(2, N'Khách 1', '1990-10-10', NULL, N'Nam', N'Quảng Trị'),
(3, N'Khách 2', '1992-12-12', NULL, N'Nam', N'Quảng Trị'),
(3, N'Khách 3', '1995-01-01', NULL, N'Nu', N'Quảng Trị'),
(4, N'Khách 4', '2010-10-10', NULL, N'Nam', N'Quảng Trị'),
(5, N'Khách 5', '1988-08-08', NULL, N'Nam', N'Quảng Trị'),
(6, N'Khách 6', '1985-05-05', NULL, N'Nam', N'Quảng Trị'),
(7, N'Khách 7', '1993-03-03', NULL, N'Nam', N'Quảng Trị'),
(8, N'Khách 8', '1996-06-06', NULL, N'Nam', N'Quảng Trị'),
(9, N'Khách 9', '1998-08-08', NULL, N'Nam', N'Quảng Trị'),
(10, N'Khách 10', '1991-01-01', NULL, N'Nam', N'Quảng Trị'),
(11, N'Khách 11', '1994-04-04', NULL, N'Nam', N'Quảng Trị'),
(12, N'Khách 12', '1997-07-07', NULL, N'Nam', N'Quảng Trị'),
(13, N'Khách 13', '1989-09-09', NULL, N'Nam', N'Quảng Trị'),
(14, N'Khách 14', '1980-08-08', NULL, N'Nam', N'Quảng Trị'),
(15, N'Khách 15', '1982-02-02', NULL, N'Nam', N'Quảng Trị'),
(16, N'Khách 16', '1999-11-11', NULL, N'Nam', N'Quảng Trị'),
(17, N'Khách 17', '1992-12-22', NULL, N'Nam', N'Quảng Trị'),
(20, N'Nguyễn Long Nhật', '1999-01-01', N'0988123456', N'Nam', N'Quảng Trị');
GO

-- HinhAnhTour
INSERT INTO HinhAnhTour (maTour, urlHinhAnh, tieuDeHinhAnh, laChinh) VALUES
(1, N'b1.jpg', N'Cầu Vàng', 1), (1, N'b2.jpg', N'Làng Pháp', 0),
(2, N'h1.jpg', N'Phố cổ', 1), (3, N'ha1.jpg', N'Vịnh', 1), (4, N'p1.jpg', N'Bãi biển', 1);
go

-- TourYeuThich
INSERT INTO TourYeuThich (maNguoiDung, maTour, ngayLuu) VALUES
(3, 1, GETDATE()), (3, 2, GETDATE()), (3, 3, GETDATE()), (4, 4, GETDATE()), (4, 5, GETDATE());
go

-- YeuCauTourAI
INSERT INTO YeuCauTourAI (maNguoiDung, noiDungYeuCau, ngayYeuCau) VALUES
(3, N'Tư vấn tour biển', GETDATE()), (3, N'Tour leo núi', GETDATE()),
(4, N'Tour ẩm thực', GETDATE()), (4, N'Tour gia đình', GETDATE()), (3, N'Tour tâm linh', GETDATE());
go

-- LichTrinhDeXuat
INSERT INTO LichTrinhDeXuat (maYeuCau, tieuDeTour, noiDungDeXuat, tongTienDuKien, trangThai) VALUES
(1, N'Phú Quốc 3N2Đ', N'Lịch trình chi tiết...', 15000000, N'DaLuu'),
(2, N'Fansipan 2N1Đ', N'Leo núi...', 5000000, N'DaLuu'),
(3, N'Hải Phòng Food Tour', N'Ăn sập...', 1000000, N'DaLuu'),
(4, N'Đà Nẵng - Hội An', N'Kết hợp...', 8000000, N'DaLuu'),
(5, N'Bái Đính - Tràng An', N'Cúng phật...', 2000000, N'DaLuu');
go

-- GuiYeuCauTour
INSERT INTO GuiYeuCauTour (maLichTrinh, ngayGui, trangThai) VALUES
(1, GETDATE(), N'ChoDuyet'), (2, GETDATE(), N'DaDuyet'), (3, GETDATE(), N'DaHuy'), (4, GETDATE(), N'ChoDuyet'), (5, GETDATE(), N'DaDuyet');
GO


GO
CREATE PROCEDURE sp_LayHoatDongGanDay
AS
BEGIN
    SET NOCOUNT ON;

    -- Lấy 10 hoạt động mới nhất từ 3 nguồn khác nhau
    SELECT TOP 10 * FROM (
        -- 1. Hoạt động Đặt Tour (Từ bảng DatTour)
        SELECT 
            'BOOKING' AS loai, 
            N'Khách hàng đã đặt tour mới' AS tieuDe,
            ND.hoTen + N' đã đặt tour ' + T.tenTour AS moTa,
            DT.ngayDat AS thoiGian
        FROM DatTour DT
        JOIN NguoiDung ND ON DT.maNguoiDung = ND.maNguoiDung
        JOIN LichKhoiHanh LKH ON DT.maLichKhoiHanh = LKH.maLichKhoiHanh
        JOIN Tour T ON LKH.maTour = T.maTour

        UNION ALL

        -- 2. Hoạt động Đánh giá (Từ bảng DanhGia)
        SELECT 
            'REVIEW' AS loai, 
            N'Khách hàng đã đánh giá' AS tieuDe,
            ND.hoTen + N' đã đánh giá ' + CAST(DG.diemSo AS NVARCHAR) + N' sao cho ' + T.tenTour AS moTa,
            DG.thoiGianTao AS thoiGian
        FROM DanhGia DG
        JOIN NguoiDung ND ON DG.maNguoiDung = ND.maNguoiDung
        JOIN Tour T ON DG.maTour = T.maTour

        UNION ALL

        -- 3. Hoạt động Thanh toán (Từ bảng ThanhToan)
        SELECT 
            'PAYMENT' AS loai,
            N'Khách hàng đã thanh toán' AS tieuDe,
            N'Khách hàng' + ND.hoTen + N' đã thanh toán tour ' + T.tenTour AS moTa,
            ngayThanhToan AS thoiGian
        FROM ThanhToan TT
        JOIN DatTour DT on TT.maDatTour = DT.maDatTour
        JOIN NguoiDung ND on DT.maNguoiDung = ND.maNguoiDung
        JOIN LichKhoiHanh LKH on DT.maLichKhoiHanh = LKH.maLichKhoiHanh
        JOIN Tour T on T.maTour = LKH.maTour
    ) AS CombinedActivities
    ORDER BY thoiGian DESC;
END;
GO

Exec sp_LayHoatDongGanDay



select *from NguoiDung
select *from Tour
select *from LichKhoiHanh
select *from DatTour
select *from ThanhToan
