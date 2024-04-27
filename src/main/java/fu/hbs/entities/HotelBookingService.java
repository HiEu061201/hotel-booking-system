
/*
 * Copyright (C) 2023, FPT University
 * SEP490 - SEP490_G77
 * HBS
 * Hotel Booking System
 *
 * Record of change:
 * DATE          Version    Author           DESCRIPTION
 * 11/11/2023    1.0        HieuLBM          First Deploy
 *
 *
 */
package fu.hbs.entities;


import java.math.BigDecimal;
import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "hotel_booking_service")
public class HotelBookingService {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "hotel_booking_service_id")
    private Long hotelBookingServiceId;
    private Long hotelBookingId;
    private Long roomId;
    private Long serviceId;
    private Instant createDate;
    @Column(name = "quantity")
    private int quantity;
    @Column(name = "total_price")
    private BigDecimal totalPrice;

}
