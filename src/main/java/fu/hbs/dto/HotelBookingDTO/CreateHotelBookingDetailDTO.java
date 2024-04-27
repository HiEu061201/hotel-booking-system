/*
 * Copyright (C) 2023, FPT University
 * SEP490 - SEP490_G77
 * HBS
 * Hotel Booking System
 *
 * Record of change:
 * DATE          Version    Author           DESCRIPTION
 * 09/11/2023    1.0        HieuDT74          First Deploy
 * 12/11/2023    2.0        HieuDT74          
 * 12/11/2023	 2.1		HieuDT74		
 * 12/11/2023    2.2        HieuDT74          
 */

package fu.hbs.dto.HotelBookingDTO;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class CreateHotelBookingDetailDTO {
    private Long roomCategoryId;
    private Integer roomNumber;
}