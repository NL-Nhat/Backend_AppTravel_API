package com.example.travelappapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.travelappapi.model.DatTour;

public interface DatTourRepository extends JpaRepository<DatTour, Integer>{
    
}
