package com.example.travelappapi.model;

import lombok.*;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TourYeuThichId implements Serializable {
    private Integer maNguoiDung;
    private Integer maTour;
}

