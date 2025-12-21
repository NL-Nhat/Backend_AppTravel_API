package com.example.travelappapi.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.travelappapi.dto.CancelBookingRequest;
import com.example.travelappapi.dto.DatTourHistoryItemDTO;
import com.example.travelappapi.model.NguoiDung;
import com.example.travelappapi.service.DatTourService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/user")
public class DatTourController {

    private final DatTourService datTourService;

    public DatTourController(DatTourService datTourService) {
        this.datTourService = datTourService;
    }

    @GetMapping("/bookings")
    public ResponseEntity<List<DatTourHistoryItemDTO>> getLichSuDatTour(
            @RequestParam(value = "status", required = false) String trangThaiDatTour) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof NguoiDung)) {
            return ResponseEntity.status(401).build();
        }

        NguoiDung user = (NguoiDung) auth.getPrincipal();
        return ResponseEntity.ok(datTourService.getLichSuDatTourCuaNguoiDung(user.getMaNguoiDung(), trangThaiDatTour));
    }

    @PostMapping("/bookings/{maDatTour}/cancel")
    public ResponseEntity<?> cancelBooking(@PathVariable Integer maDatTour, @Valid @RequestBody CancelBookingRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof NguoiDung)) {
            return ResponseEntity.status(401).build();
        }

        NguoiDung user = (NguoiDung) auth.getPrincipal();
        datTourService.cancelBookingAsUser(maDatTour, user.getMaNguoiDung(), request.getLyDoHuy());
        return ResponseEntity.ok().build();
    }
}

