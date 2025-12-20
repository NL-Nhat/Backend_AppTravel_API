package com.example.travelappapi.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.travelappapi.dto.BookingRequestDTO;
import com.example.travelappapi.dto.BookingResponseDTO;
import com.example.travelappapi.service.BookingService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping("/create")
    public ResponseEntity<?> createBooking(@RequestBody BookingRequestDTO request) {
        try {
            int maDatTour = bookingService.createBooking(request);
            
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new BookingResponseDTO(maDatTour, "Đặt tour thành công"));
                    
        } catch (RuntimeException e) {
            // Bắt các lỗi nghiệp vụ (ví dụ: hết chỗ, sai ID người dùng...) 
            // Trả về mã 400 Bad Request để Android biết lỗi gì mà hiện Toast
            return ResponseEntity.badRequest()
                    .body(new BookingResponseDTO(0, e.getMessage()));
                    
        } catch (Exception e) {
            //Bắt các lỗi hệ thống không lường trước (Lỗi SQL, NullPointer...)
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BookingResponseDTO(0, "Lỗi hệ thống: " + e.getMessage()));
        }
    }
}
