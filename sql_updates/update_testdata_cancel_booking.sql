-- File này dùng để phục vụ test chức năng HỦY ĐẶT TOUR.
-- Lưu ý: KHÔNG sửa file SQL_AppTravel.sql theo yêu cầu.
-- Tùy DB bạn đang dùng, có thể cần chạy thủ công trong SSMS.

-- Ví dụ: đưa 1 đơn bất kỳ về trạng thái ChoXacNhan để test hủy
-- (Hãy thay @MaDatTour bằng ID tồn tại trong DB của bạn)
DECLARE @MaDatTour INT = 1;

UPDATE DatTour
SET trangThaiDatTour = N'ChoXacNhan',
    ngayHuy = NULL,
    lyDoHuy = NULL
WHERE maDatTour = @MaDatTour;

SELECT maDatTour, trangThaiDatTour, ngayHuy, lyDoHuy
FROM DatTour
WHERE maDatTour = @MaDatTour;
