package com.example.travelappapi.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.travelappapi.dto.BookingRequestDTO;
import com.example.travelappapi.dto.BookingResponseDTO;
import com.example.travelappapi.dto.ViDienTuResponse;
import com.example.travelappapi.service.BookingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;



@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getMethodName(@PathVariable int id) {
        ViDienTuResponse viDienTuResponse = new ViDienTuResponse();
        viDienTuResponse = bookingService.layThongTin(id);
        return ResponseEntity.ok(viDienTuResponse);
    }
    

    @PostMapping("/create")
    public ResponseEntity<?> createBooking(@RequestBody BookingRequestDTO request) {
        try {
            int maDatTour = bookingService.createBooking(request);
            
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new BookingResponseDTO(maDatTour, "Đặt tour thành công"));
                    
        } catch (RuntimeException e) {
            
            return ResponseEntity.badRequest()
                    .body(new BookingResponseDTO(0, e.getMessage()));
                    
        } catch (Exception e) {
            //Bắt các lỗi hệ thống không lường trước (Lỗi SQL, NullPointer...)
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BookingResponseDTO(0, "Lỗi hệ thống: " + e.getMessage()));
        }
    }
}
