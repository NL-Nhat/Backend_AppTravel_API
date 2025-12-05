package com.example.travelappapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.travelappapi.model.Tour;

@Repository
public interface TourRepository extends JpaRepository<Tour, Integer> {


}
