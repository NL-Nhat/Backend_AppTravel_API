package com.example.travelappapi.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.travelappapi.dto.AdminBookingItemDTO;
import com.example.travelappapi.dto.CancelBookingRequest;
import com.example.travelappapi.service.DatTourService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/admin/bookings")
@PreAuthorize("hasRole('Admin')")
public class AdminBookingController {

    private final DatTourService datTourService;

    public AdminBookingController(DatTourService datTourService) {
        this.datTourService = datTourService;
    }

    /**
     * List bookings for admin.
     * Optional filters:
     * - status: ChoXacNhan | DaXacNhan | DaHuy
     * - q: free text (booking id, customer name/phone/email, tour name)
     */
    @GetMapping
    public ResponseEntity<List<AdminBookingItemDTO>> listBookings(
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "q", required = false) String q) {
        return ResponseEntity.ok(datTourService.getBookingsForAdmin(status, q));
    }

    @GetMapping("/{maDatTour}")
    public ResponseEntity<AdminBookingItemDTO> getBookingDetail(@PathVariable Integer maDatTour) {
        return ResponseEntity.ok(datTourService.getBookingDetailForAdmin(maDatTour));
    }

    @PostMapping("/{maDatTour}/confirm")
    public ResponseEntity<?> confirmBooking(@PathVariable Integer maDatTour) {
        datTourService.confirmBookingAsAdmin(maDatTour);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{maDatTour}/cancel")
    public ResponseEntity<?> cancelBooking(@PathVariable Integer maDatTour, @Valid @RequestBody CancelBookingRequest request) {
        datTourService.cancelBookingAsAdmin(maDatTour, request.getLyDoHuy());
        return ResponseEntity.ok().build();
    }
}
