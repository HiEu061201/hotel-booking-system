/*
 * Copyright (C) 2023, FPT University
 * SEP490 - SEP490_G77
 * HBS
 * Hotel Booking System
 *
 * Record of change:
 * DATE          Version    Author           DESCRIPTION
 * 04/10/2023    1.0        HieuLBM          First Deploy
 * 10/10/2023    2.0        HieuLBM          Fix notation, id filed
 * 22/10/2023	 2.1		HieuLBM			 change name
 * 2/11/2023     2.2        HieuLBM          Add attribute
 */

package fu.hbs.entities;

import java.math.BigDecimal;
import java.sql.Date;
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
@Table(name = "hotel_booking")
public class HotelBooking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "hotel_booking_id")
    private Long hotelBookingId;
    private Long userId;
    private Long statusId;
    private Long serviceId;
    private int totalRoom;
    private String name;
    private String email;
    private String address;
    private String phone;
    private BigDecimal totalPrice = BigDecimal.ZERO;
    private BigDecimal depositPrice = BigDecimal.ZERO;
    private BigDecimal refundPrice = BigDecimal.ZERO;
    private Instant checkIn;
    private Instant checkOut;
    private Instant checkInActual;
    private Instant checkOutActual;
    private Boolean validBooking = false;
    private String note;
}
