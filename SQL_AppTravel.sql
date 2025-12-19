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
    soDienThoai NVARCHAR(15) unique,
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
    soLuongKhachDaDat INT DEFAULT 0 CHECK (soLuongKhachDaDat >= 0)
    
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
    maDatTour INT NOT NULL,
    phuongThucThanhToan NVARCHAR(20) NOT NULL CHECK (phuongThucThanhToan IN ('ChuyenKhoan', 'TheTinDung', 'ViDienTu')),
    soTien DECIMAL(18,2) NOT NULL CHECK (soTien > 0),
    ngayThanhToan DATETIME DEFAULT GETDATE(),
    
    CONSTRAINT FK_ThanhToan_DatTour FOREIGN KEY (maDatTour) REFERENCES DatTour(maDatTour)
);

CREATE TABLE DanhGia (
    maDanhGia INT IDENTITY(1,1) PRIMARY KEY,
    maTour INT NOT NULL,
    maNguoiDung INT NOT NULL,
    diemSo DECIMAL(3,1) NOT NULL CHECK (diemSo BETWEEN 0 AND 5),
    binhLuan NVARCHAR(max),
    thoiGianTao DATETIME DEFAULT GETDATE(),
    
    CONSTRAINT FK_DanhGia_Tour FOREIGN KEY (maTour) REFERENCES Tour(maTour),
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
(N'admin2', N'admin1@travel.com', N'123', N'Quản Trị Hệ Thống', N'0909001001', N'Hà Nội', '1990-01-01', N'Nam', N'admin1.jpg', N'Admin', N'HoatDong', '2023-01-01'),
(N'admin3', N'admin2@travel.com', N'123', N'Trần Quản Lý', N'0909001002', N'HCM', '1992-05-05', N'Nu', N'admin2.jpg', N'Admin', N'HoatDong', '2023-01-02'),
-- HDV (ID 3,4,5,6,7)
(N'hdv_hung', N'hung@travel.com', N'123', N'Lê Văn Hùng', N'0909002001', N'Đà Nẵng', '1995-02-02', N'Nam', N'hdv1.jpg', N'HuongDanVien', N'HoatDong', '2023-02-01'),
(N'hdv_lan', N'lan@travel.com', N'123', N'Nguyễn Thị Lan', N'0909002002', N'Hà Nội', '1996-03-03', N'Nu', N'hdv2.jpg', N'HuongDanVien', N'HoatDong', '2023-02-05'),
(N'hdv_tuan', N'tuan@travel.com', N'123', N'Phạm Anh Tuấn', N'0909002003', N'HCM', '1994-04-04', N'Nam', N'hdv3.jpg', N'HuongDanVien', N'HoatDong', '2023-02-10'),
(N'hdv_hoa', N'hoa@travel.com', N'123', N'Trần Thị Hoa', N'0909002004', N'Cần Thơ', '1998-05-05', N'Nu', N'hdv4.jpg', N'HuongDanVien', N'HoatDong', '2023-02-15'),
(N'hdv_dung', N'dung@travel.com', N'123', N'Ngô Tiến Dũng', N'0909002005', N'Đà Lạt', '1993-06-06', N'Nam', N'hdv5.jpg', N'HuongDanVien', N'HoatDong', '2023-02-20'),
-- Khách Hàng (ID 8-20)
(N'nhat', N'nhat@gmail.com', N'123', N'Nguyễn Long Nhật', N'0909003001', N'Quảng Trị', '1999-01-01', N'Nam', N'anhdaidien.jpg', N'KhachHang', N'HoatDong', '2024-01-01'),
(N'kh_binh', N'binh@gmail.com', N'123', N'Trần Thanh Bình', N'0909003002', N'Hải Phòng', '2000-02-02', N'Nu', N'kh2.jpg', N'KhachHang', N'HoatDong', '2024-01-02'),
(N'kh_cuong', N'cuong@gmail.com', N'123', N'Lê Mạnh Cường', N'0909003003', N'Đà Nẵng', '1995-03-03', N'Nam', N'kh3.jpg', N'KhachHang', N'HoatDong', '2024-01-03'),
(N'kh_dung_k', N'dungk@gmail.com', N'123', N'Hoàng Thùy Dung', N'0909003004', N'Nha Trang', '1998-04-04', N'Nu', N'kh4.jpg', N'KhachHang', N'HoatDong', '2024-01-04'),
(N'kh_em', N'em@gmail.com', N'123', N'Phạm Thị Em', N'0909003005', N'Cần Thơ', '2001-05-05', N'Nu', N'kh5.jpg', N'KhachHang', N'HoatDong', '2024-01-05'),
(N'kh_phuc', N'phuc@gmail.com', N'123', N'Đỗ Thiên Phúc', N'0909003006', N'HCM', '1990-06-06', N'Nam', N'kh6.jpg', N'KhachHang', N'HoatDong', '2024-01-06'),
(N'kh_giang', N'giang@gmail.com', N'123', N'Vũ Hương Giang', N'0909003007', N'Hà Nội', '1997-07-07', N'Nu', N'kh7.jpg', N'KhachHang', N'HoatDong', '2024-01-07'),
(N'kh_huy', N'huy@gmail.com', N'123', N'Ngô Quang Huy', N'0909003008', N'HCM', '1992-08-08', N'Nam', N'kh8.jpg', N'KhachHang', N'HoatDong', '2024-01-08'),
(N'kh_yen', N'yen@gmail.com', N'123', N'Trịnh Mỹ Yên', N'0909003009', N'Đà Lạt', '2002-09-09', N'Nu', N'kh9.jpg', N'KhachHang', N'HoatDong', '2024-01-09'),
(N'kh_khanh', N'khanh@gmail.com', N'123', N'Mai Quốc Khánh', N'0909003010', N'Vũng Tàu', '1989-10-10', N'Nam', N'kh10.jpg', N'KhachHang', N'HoatDong', '2024-01-10'),
(N'kh_lam', N'lam@gmail.com', N'123', N'Nguyễn Tùng Lâm', N'0909003011', N'Hà Nội', '1996-11-11', N'Nam', N'kh11.jpg', N'KhachHang', N'Khoa', '2024-01-11'),
(N'kh_mai', N'mai@gmail.com', N'123', N'Lê Tuyết Mai', N'0909003012', N'Đà Nẵng', '1995-12-12', N'Nu', N'kh12.jpg', N'KhachHang', N'HoatDong', '2024-01-12'),
(N'kh_nam', N'nam@gmail.com', N'123', N'Trần Phương Nam', N'0909003013', N'HCM', '1994-01-15', N'Nam', N'kh13.jpg', N'KhachHang', N'HoatDong', '2024-01-13');
GO

-- BẢNG 2: DIEMDEN (20 Địa điểm)
INSERT INTO DiemDen (tenDiemDen, thanhPho, moTa, urlHinhAnh) VALUES
(N'Bà Nà Hills', N'Đà Nẵng', N'Khu du lịch SunWorld, Cầu Vàng', N'bana.jpg'),
(N'Phố Cổ Hội An', N'Quảng Nam', N'Di sản văn hóa thế giới, đèn lồng', N'hoian.jpg'),
(N'Vịnh Hạ Long', N'Quảng Ninh', N'Kỳ quan thiên nhiên, du thuyền', N'halong.jpg'),
(N'VinWonders Phú Quốc', N'Kiên Giang', N'Công viên chủ đề lớn nhất VN', N'vinphuquoc.jpg'),
(N'Thung Lũng Tình Yêu', N'Đà Lạt', N'Điểm đến lãng mạn', N'dalat.jpg'),
(N'Quần thể Tràng An', N'Ninh Bình', N'Non nước hữu tình, Bái Đính', N'trangan.jpg'),
(N'Đại Nội Huế', N'Thừa Thiên Huế', N'Kinh thành triều Nguyễn', N'hue.jpg'),
(N'Động Phong Nha', N'Quảng Bình', N'Hang động nước dài nhất', N'phongnha.jpg'),
(N'Chợ Nổi Cái Răng', N'Cần Thơ', N'Văn hóa sông nước miền Tây', N'cairang.jpg'),
(N'Ruộng Bậc Thang', N'Yên Bái', N'Mù Cang Chải mùa lúa chín', N'mucangchai.jpg'),
(N'Đỉnh Fansipan', N'Lào Cai', N'Nóc nhà Đông Dương', N'sapa.jpg'),
(N'Côn Đảo', N'Bà Rịa - Vũng Tàu', N'Du lịch tâm linh và biển đảo', N'condao.jpg'),
(N'Mũi Né', N'Bình Thuận', N'Đồi cát bay, resort', N'muine.jpg'),
(N'Hồ Ba Bể', N'Bắc Kạn', N'Hồ nước ngọt tự nhiên', N'babe.jpg'),
(N'Thác Bản Giốc', N'Cao Bằng', N'Thác nước hùng vĩ biên giới', N'bangioc.jpg'),
(N'Tam Đảo', N'Vĩnh Phúc', N'Thị trấn mờ sương', N'tamdao.jpg'),
(N'Vườn QG Cát Tiên', N'Đồng Nai', N'Khám phá thiên nhiên hoang dã', N'cattien.jpg'),
(N'Biển Mỹ Khê', N'Đà Nẵng', N'Bãi biển quyến rũ nhất hành tinh', N'mykhe.jpg'),
(N'Eo Gió', N'Bình Định', N'Con đường đi bộ ven biển', N'eogio.jpg'),
(N'Thánh Địa Mỹ Sơn', N'Quảng Nam', N'Di tích Chăm Pa cổ', N'myson.jpg');
GO

-- BẢNG 3: TOUR (20 Tours)
INSERT INTO Tour (tenTour, maDiemDen, moTa, giaNguoiLon, giaTreEm, urlHinhAnhChinh, diemDanhGiaTrungBinh, soLuongDanhGia, trangThai) VALUES
(N'Khám Phá Bà Nà Hills 1 Ngày', 1, N'Ăn buffet trưa, cáp treo khứ hồi', 1250000, 950000, N'tour_bana.jpg', 0, 0, N'DangMo'),
(N'Hội An Ký Ức Đêm', 2, N'Show diễn thực cảnh, thả đèn hoa đăng', 850000, 500000, N'tour_hoian.jpg', 0, 0, N'DangMo'),
(N'Hạ Long 2N1Đ Du Thuyền 5 Sao', 3, N'Ngủ đêm trên vịnh, chèo Kayak', 3500000, 2500000, N'tour_halong.jpg', 0, 0, N'DangMo'),
(N'Combo Phú Quốc 3N2Đ', 4, N'Vé máy bay + Khách sạn + VinWonders', 4500000, 3000000, N'tour_pq.jpg', 0, 0, N'DangMo'),
(N'Đà Lạt Săn Mây', 5, N'Đón bình minh đồi chè Cầu Đất', 700000, 400000, N'tour_dalat.jpg', 0, 0, N'DangMo'),
(N'Ninh Bình Non Nước 1 Ngày', 6, N'Tràng An - Bái Đính - Hang Múa', 950000, 600000, N'tour_trangan.jpg', 0, 0, N'DangMo'),
(N'Huế City Tour Trọn Gói', 7, N'Tham quan lăng tẩm, nghe ca Huế', 750000, 450000, N'tour_hue.jpg', 0, 0, N'DangMo'),
(N'Thám Hiểm Phong Nha Kẻ Bàng', 8, N'Trekking hang động 2 ngày', 2500000, 1800000, N'tour_phongnha.jpg', 0, 0, N'TamDung'),
(N'Miền Tây Sông Nước 2N1Đ', 9, N'Mỹ Tho - Cần Thơ - Chợ Nổi', 1800000, 1200000, N'tour_mientay.jpg', 0, 0, N'DangMo'),
(N'Mù Cang Chải Mùa Lúa Chín', 10, N'Săn ảnh ruộng bậc thang', 3200000, 2800000, N'tour_taybac.jpg', 0, 0, N'DangMo'),
(N'Chinh Phục Fansipan', 11, N'Leo núi hoặc cáp treo', 1500000, 1000000, N'tour_sapa.jpg', 0, 0, N'DangMo'),
(N'Côn Đảo Tâm Linh', 12, N'Viếng mộ cô Sáu, tắm biển', 5000000, 4000000, N'tour_condao.jpg', 0, 0, N'DangMo'),
(N'Mũi Né Jeep Tour', 13, N'Ngắm bình minh đồi cát trắng', 600000, 300000, N'tour_muine.jpg', 0, 0, N'DangMo'),
(N'Hồ Ba Bể - Thác Bản Giốc', 14, N'Tour Đông Bắc 3N2Đ', 2800000, 2000000, N'tour_babe.jpg', 0, 0, N'DangMo'),
(N'Tam Đảo Nghỉ Dưỡng Cuối Tuần', 16, N'Xe đưa đón Hà Nội - Tam Đảo', 500000, 300000, N'tour_tamdao.jpg', 0, 0, N'DangMo'),
(N'Nam Cát Tiên Trekking', 17, N'Xem thú đêm, Bàu Sấu', 1200000, 800000, N'tour_cattien.jpg', 0, 0, N'DangMo'),
(N'Đà Nẵng - Hội An 4N3Đ', 18, N'Tour liên tuyến Miền Trung', 5500000, 4000000, N'tour_mientrung.jpg', 0, 0, N'DangMo'),
(N'Kỳ Co - Eo Gió', 19, N'Maldives của Việt Nam', 850000, 500000, N'tour_quynhon.jpg', 0, 0, N'DangMo'),
(N'Thánh Địa Mỹ Sơn Nửa Ngày', 20, N'Tìm hiểu văn hóa Chăm', 600000, 300000, N'tour_myson.jpg', 0, 0, N'DangMo'),
(N'Tour Ẩm Thực Hà Nội', 1, N'Walking food tour phố cổ', 500000, 300000, N'tour_hanoi.jpg', 0, 0, N'DangMo');
GO

-- BẢNG 4: LICHKHOIHANH (20 Lịch trình)
-- Lưu ý: soLuongKhachDaDat để mặc định là 0, trigger sẽ tự tính khi insert DatTour
INSERT INTO LichKhoiHanh (maTour, ngayKhoiHanh, ngayKetThuc, huongDanVien, soLuongKhachToiDa) VALUES
(1, '2025-12-01 07:00:00', '2025-12-01 17:00:00', 3, 30), -- Bà Nà
(1, '2025-12-05 07:00:00', '2025-12-05 17:00:00', 3, 30),
(2, '2025-12-02 15:00:00', '2025-12-02 21:00:00', 3, 20), -- Hội An
(3, '2025-12-10 08:00:00', '2025-12-11 12:00:00', 4, 15), -- Hạ Long
(3, '2025-12-20 08:00:00', '2025-12-21 12:00:00', 4, 15),
(4, '2025-12-15 09:00:00', '2025-12-17 18:00:00', 5, 25), -- Phú Quốc
(5, '2025-12-03 04:00:00', '2025-12-03 14:00:00', 7, 10), -- Đà Lạt
(6, '2025-12-12 07:00:00', '2025-12-12 18:00:00', 4, 40), -- Ninh Bình
(7, '2025-12-06 08:00:00', '2025-12-06 17:00:00', 3, 25), -- Huế
(9, '2025-12-25 05:00:00', '2025-12-26 18:00:00', 6, 20), -- Miền Tây
(10, '2025-09-15 06:00:00', '2025-09-17 18:00:00', 4, 15), -- Mù Cang Chải (Mùa lúa)
(11, '2025-12-30 20:00:00', '2026-01-01 10:00:00', 4, 20), -- Sapa
(12, '2025-11-20 06:00:00', '2025-11-22 14:00:00', 5, 15), -- Côn Đảo
(14, '2025-12-18 05:00:00', '2025-12-20 20:00:00', 4, 25), -- Ba Bể
(16, '2025-12-15 06:00:00', '2025-12-16 17:00:00', 6, 12), -- Nam Cát Tiên
(17, '2026-01-01 08:00:00', '2026-01-04 18:00:00', 3, 30), -- Liên tuyến miền Trung
(18, '2025-12-24 07:00:00', '2025-12-24 17:00:00', 3, 20), -- Quy Nhơn
(19, '2025-12-25 08:00:00', '2025-12-25 12:00:00', 3, 30), -- Mỹ Sơn
(20, '2025-11-28 17:00:00', '2025-11-28 21:00:00', 4, 10), -- Food tour
(4, '2026-01-10 09:00:00', '2026-01-12 18:00:00', 5, 25); -- Phú Quốc đợt 2
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
(8, 1, 2, 1, 3450000, N'DaXacNhan', N'DaThanhToan', '2025-11-20 08:00:00', NULL, NULL), -- An đặt Bà Nà
(9, 1, 2, 0, 2500000, N'ChoXacNhan', N'ChuaThanhToan', '2025-11-25 09:00:00', NULL, NULL), -- Bình đặt Bà Nà
(10, 3, 2, 0, 7000000, N'DaXacNhan', N'DaThanhToan', '2025-11-22 10:00:00', NULL, NULL), -- Cường đặt Hạ Long
(11, 4, 2, 2, 15000000, N'DaXacNhan', N'DaThanhToan', '2025-11-15 11:00:00', NULL, NULL), -- Dung đặt Phú Quốc
(12, 4, 4, 0, 18000000, N'DaHuy', N'ChuaThanhToan', '2025-11-10 12:00:00', '2025-11-11 08:00:00', N'Bận việc đột xuất'), -- Em hủy Phú Quốc
(13, 6, 2, 0, 1900000, N'DaXacNhan', N'ChuaThanhToan', '2025-11-18 13:00:00', NULL, NULL), -- Phúc đặt Ninh Bình
(14, 5, 2, 0, 1400000, N'DaXacNhan', N'DaThanhToan', '2025-11-19 14:00:00', NULL, NULL), -- Giang đặt Đà Lạt
(15, 6, 2, 0, 1900000, N'DaXacNhan', N'DaThanhToan', '2025-11-21 15:00:00', NULL, NULL), -- Huy đặt Ninh Bình
(16, 17, 4, 2, 22000000, N'ChoXacNhan', N'ChuaThanhToan', '2025-11-26 16:00:00', NULL, NULL), -- Yên đặt Liên tuyến
(17, 7, 5, 0, 3750000, N'DaXacNhan', N'DaThanhToan', '2025-11-10 17:00:00', NULL, NULL), -- Khánh đặt Huế
(8, 7, 2, 1, 1950000, N'DaXacNhan', N'DaThanhToan', '2025-11-11 18:00:00', NULL, NULL), -- An đặt Huế
(9, 10, 2, 1, 5400000, N'DaXacNhan', N'DaThanhToan', '2025-11-23 19:00:00', NULL, NULL), -- Bình đặt Miền Tây
(10, 11, 2, 0, 6400000, N'ChoXacNhan', N'ChuaThanhToan', '2025-08-01 20:00:00', NULL, NULL), -- Cường đặt Mù Cang Chải
(11, 13, 2, 0, 10000000, N'DaXacNhan', N'DaThanhToan', '2025-11-01 21:00:00', NULL, NULL), -- Dung đặt Côn Đảo
(12, 19, 1, 0, 500000, N'DaXacNhan', N'ChuaThanhToan', '2025-11-27 08:00:00', NULL, NULL), -- Em đặt Food tour
(13, 16, 2, 0, 2400000, N'DaXacNhan', N'DaThanhToan', '2025-11-28 09:00:00', NULL, NULL), -- Phúc đặt Nam Cát Tiên
(14, 2, 2, 0, 1700000, N'DaXacNhan', N'ChuaThanhToan', '2025-11-29 10:00:00', NULL, NULL), -- Giang đặt Hội An
(15, 12, 2, 0, 3000000, N'ChoXacNhan', N'ChuaThanhToan', '2025-11-29 11:00:00', NULL, NULL), -- Huy đặt Sapa
(8, 14, 2, 1, 7600000, N'DaXacNhan', N'DaThanhToan', '2025-11-15 12:00:00', NULL, NULL), -- An đặt Ba Bể
(9, 20, 4, 0, 18000000, N'DaXacNhan', N'ChuaThanhToan', '2025-11-30 08:00:00', NULL, NULL); -- Bình đặt Phú Quốc đợt 2
GO

-- BẢNG 7: THANHTOAN (~15 Giao dịch)
INSERT INTO ThanhToan (maDatTour, phuongThucThanhToan, soTien, ngayThanhToan) VALUES
(1, N'ViDienTu', 3450000, '2025-11-20 08:05:00'),
(3, N'ChuyenKhoan', 7000000, '2025-11-22 10:15:00'),
(4, N'TheTinDung', 15000000, '2025-11-15 11:30:00'),
(7, N'ViDienTu', 1400000, '2025-11-19 14:10:00'),
(8, N'ChuyenKhoan', 1900000, '2025-11-21 15:05:00'),
(10, N'ChuyenKhoan', 3750000, '2025-11-10 17:10:00'),
(11, N'TheTinDung', 1950000, '2025-11-11 18:15:00'),
(12, N'ViDienTu', 5400000, '2025-11-23 19:10:00'),
(14, N'ChuyenKhoan', 10000000, '2025-11-01 21:05:00'),
(15, N'ChuyenKhoan', 500000, '2025-11-27 08:15:00'),
(16, N'ViDienTu', 2400000, '2025-11-28 09:10:00'),
(17, N'ViDienTu', 1700000, '2025-11-29 10:05:00'),
(19, N'ChuyenKhoan', 7600000, '2025-11-15 12:15:00'),
(20, N'ChuyenKhoan', 18000000, '2025-11-30 08:10:00'),
(1, N'ChuyenKhoan', 100000, '2025-11-20 09:00:00');
GO

-- BẢNG 8: DANHGIA (20 Reviews)
-- Trigger sẽ update vào bảng Tour
INSERT INTO DanhGia (maTour, maNguoiDung, diemSo, binhLuan, thoiGianTao) VALUES
(1, 8, 5.0, N'Cầu Vàng quá đẹp, đồ ăn ngon', '2025-11-21 08:00:00'),
(1, 9, 4.0, N'Đông khách quá phải chờ cáp treo', '2025-11-26 09:00:00'),
(3, 10, 5.0, N'Du thuyền 5 sao xịn sò, nhân viên thân thiện', '2025-11-23 10:00:00'),
(4, 11, 4.5, N'VinWonders chơi cả ngày không chán', '2025-11-16 11:00:00'),
(6, 13, 5.0, N'Cảnh Tràng An như trong phim', '2025-11-19 13:00:00'),
(5, 14, 4.0, N'Săn mây thành công, trời hơi lạnh', '2025-11-20 14:00:00'),
(7, 17, 5.0, N'Huế mộng mơ, hướng dẫn viên nhiệt tình', '2025-11-12 15:00:00'),
(9, 9, 4.5, N'Trái cây miệt vườn tươi ngon', '2025-11-24 19:00:00'),
(12, 11, 5.0, N'Côn Đảo linh thiêng và đẹp', '2025-11-05 21:00:00'),
(1, 10, 3.0, N'Giá hơi cao so với dịch vụ', '2025-11-27 08:00:00'),
(2, 14, 5.0, N'Hội An về đêm lung linh tuyệt vời', '2025-11-29 11:00:00'),
(10, 8, 4.0, N'Đường đi hơi vất vả nhưng cảnh đẹp', '2025-09-20 10:00:00'),
(14, 8, 5.0, N'Thác Bản Giốc hùng vĩ', '2025-12-21 09:00:00'),
(20, 12, 4.5, N'Phở ngon, cafe trứng tuyệt', '2025-11-28 10:00:00'),
(13, 9, 4.0, N'Xe Jeep đi đồi cát rất vui', '2025-11-25 15:00:00'),
(16, 13, 5.0, N'Rừng Cát Tiên bảo tồn tốt', '2025-11-29 16:00:00'),
(18, 8, 4.5, N'Quy Nhơn biển xanh ngắt', '2025-12-25 18:00:00'),
(19, 12, 4.0, N'Mỹ Sơn cổ kính', '2025-12-26 10:00:00'),
(3, 15, 5.0, N'Sẽ quay lại Hạ Long', '2025-12-22 10:00:00'),
(5, 11, 4.0, N'Đà Lạt đẹp nhưng kẹt xe', '2025-12-04 10:00:00');
GO

-- BẢNG 9: KHACHHANGTHAMGIA (Chi tiết người đi - ~20 người)
INSERT INTO KhachHangThamGia (maDatTour, hoTen, ngaySinh, soDienThoai, gioiTinh, diaChi) VALUES
(1, N'Nguyễn Văn An', '1999-01-01', N'0909003001', N'Nam', N'Hà Nội'),
(1, N'Bạn An', '1999-05-05', NULL, N'Nu', N'Hà Nội'),
(3, N'Lê Mạnh Cường', '1995-03-03', N'0909003003', N'Nam', N'Đà Nẵng'),
(3, N'Vợ Cường', '1996-06-06', NULL, N'Nu', N'Đà Nẵng'),
(4, N'Hoàng Thùy Dung', '1998-04-04', N'0909003004', N'Nu', N'Nha Trang'),
(4, N'Chồng Dung', '1995-01-01', NULL, N'Nam', N'Nha Trang'),
(4, N'Con Dung 1', '2018-01-01', NULL, N'Nam', N'Nha Trang'),
(4, N'Con Dung 2', '2020-01-01', NULL, N'Nu', N'Nha Trang'),
(6, N'Đỗ Thiên Phúc', '1990-06-06', N'0909003006', N'Nam', N'HCM'),
(10, N'Trần Thanh Bình', '2000-02-02', N'0909003002', N'Nu', N'Hải Phòng'),
(10, N'Mẹ Bình', '1975-01-01', NULL, N'Nu', N'Hải Phòng'),
(11, N'Nguyễn Tùng Lâm', '1996-11-11', N'0909003011', N'Nam', N'Hà Nội'),
(11, N'Bạn Lâm', '1996-12-12', NULL, N'Nam', N'Hà Nội'),
(14, N'Vũ Hương Giang', '1997-07-07', N'0909003007', N'Nu', N'Hà Nội'),
(14, N'Bạn Giang', '1997-08-08', NULL, N'Nu', N'Hà Nội'),
(15, N'Ngô Quang Huy', '1992-08-08', N'0909003008', N'Nam', N'HCM'),
(15, N'Bạn Huy', '1993-09-09', NULL, N'Nu', N'HCM'),
(16, N'Trịnh Mỹ Yên', '2002-09-09', N'0909003009', N'Nu', N'Đà Lạt'),
(17, N'Mai Quốc Khánh', '1989-10-10', N'0909003010', N'Nam', N'Vũng Tàu'),
(20, N'Trần Thanh Bình', '2000-02-02', N'0909003002', N'Nu', N'Hải Phòng');
GO

-- BẢNG 10: TOURYEUTHICH (Sở thích)
INSERT INTO TourYeuThich (maNguoiDung, maTour, ngayLuu) VALUES
(8, 1, '2025-11-01 08:00:00'), (8, 4, '2025-11-01 08:05:00'),
(9, 2, '2025-11-02 09:00:00'), (9, 9, '2025-11-02 09:10:00'),
(10, 3, '2025-11-03 10:00:00'), (10, 11, '2025-11-03 10:30:00'),
(11, 4, '2025-11-04 11:00:00'), (11, 12, '2025-11-04 11:15:00'),
(12, 1, '2025-11-05 12:00:00'), (12, 20, '2025-11-05 12:05:00'),
(13, 5, '2025-11-06 13:00:00'), (13, 6, '2025-11-06 13:10:00'),
(14, 2, '2025-11-07 14:00:00'), (14, 5, '2025-11-07 14:10:00'),
(15, 3, '2025-11-08 15:00:00'), (15, 11, '2025-11-08 15:10:00'),
(16, 17, '2025-11-09 16:00:00'),
(17, 7, '2025-11-10 17:00:00');
GO

-- BẢNG 11: YEUCAUTOURAI (Prompt)
INSERT INTO YeuCauTourAI (maNguoiDung, noiDungYeuCau, ngayYeuCau) VALUES
(8, N'Tìm tour đi biển cho gia đình 4 người', '2025-11-01 08:00:00'),
(9, N'Muốn đi leo núi ở Tây Bắc', '2025-11-02 09:00:00'),
(10, N'Tour trăng mật lãng mạn tại Đà Lạt', '2025-11-03 10:00:00'),
(11, N'Du lịch tâm linh miền Bắc', '2025-11-04 11:00:00'),
(12, N'Đi food tour trong ngày', '2025-11-05 12:00:00');
GO

-- BẢNG 12: LICHTRINHDEXUAT (Kết quả AI trả về)
-- CHÚ Ý: Bảng này theo cấu trúc bạn gửi KHÔNG CÓ cột maTour, chỉ có tieuDeTour
INSERT INTO LichTrinhDeXuat (maYeuCau, tieuDeTour, noiDungDeXuat, tongTienDuKien) VALUES
(1, N'Combo Phú Quốc 3N2Đ', N'Gợi ý bạn đi Phú Quốc, tham quan VinWonders và Safari. Nghỉ tại resort 4 sao.', 15000000),
(1, N'Du lịch Côn Đảo', N'Côn Đảo biển rất đẹp và hoang sơ, thích hợp nghỉ dưỡng gia đình.', 18000000),
(2, N'Chinh Phục Fansipan', N'Tour leo núi Fansipan 2 ngày 1 đêm, hoặc đi cáp treo.', 3000000),
(2, N'Mù Cang Chải', N'Ngắm ruộng bậc thang mùa lúa chín rất đẹp.', 3500000),
(3, N'Đà Lạt Săn Mây', N'Không khí lãng mạn, đi dạo hồ Xuân Hương, săn mây đồi chè.', 4000000),
(3, N'Sapa mờ sương', N'Sapa cũng rất lãng mạn với khí hậu lạnh.', 5000000),
(4, N'Ninh Bình Non Nước', N'Thăm chùa Bái Đính, ngôi chùa lớn nhất ĐNA.', 2000000),
(4, N'Yên Tử Quảng Ninh', N'Hành hương về đất phật Yên Tử.', 1500000),
(5, N'Hà Nội Street Food', N'Khám phá ẩm thực phố cổ: Phở, Bún chả, Cafe trứng.', 500000),
(5, N'Hải Phòng Food Tour', N'Đi tàu hỏa xuống Hải Phòng ăn bánh đa cua, dừa dầm.', 800000);
GO

-- BẢNG 13: GUIYEUCAUTOUR (Trạng thái gửi yêu cầu xử lý)
INSERT INTO GuiYeuCauTour (maLichTrinh, ngayGui, trangThai) VALUES
(1, '2025-11-01 08:30:00', N'DaDuyet'),
(2, '2025-11-01 08:35:00', N'ChoDuyet'),
(3, '2025-11-02 09:30:00', N'DaDuyet'),
(4, '2025-11-02 09:40:00', N'DaHuy'),
(5, '2025-11-03 10:30:00', N'DaDuyet'),
(7, '2025-11-04 11:30:00', N'DaDuyet'),
(9, '2025-11-05 12:30:00', N'DaDuyet'),
(10, '2025-11-05 12:40:00', N'ChoDuyet'),
(6, '2025-11-03 10:40:00', N'ChoDuyet'),
(8, '2025-11-04 11:40:00', N'ChoDuyet');
GO

UPDATE NguoiDung
SET matKhau = '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.TVuHOn2'
WHERE matKhau = '123';

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
