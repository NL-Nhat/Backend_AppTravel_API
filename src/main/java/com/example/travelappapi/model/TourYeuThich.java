package com.example.travelappapi.model;

import lombok.*;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "TourYeuThich")
@IdClass(TourYeuThichId.class)
public class TourYeuThich {

    @Id
    private Integer maNguoiDung;

    @Id
    private Integer maTour;

    private LocalDateTime ngayLuu;

    @ManyToOne
    @JoinColumn(name = "maNguoiDung", insertable = false, updatable = false)
    private NguoiDung nguoiDung;

    @ManyToOne
    @JoinColumn(name = "maTour", insertable = false, updatable = false)
    private Tour tour;
}
