package fu.hbs.dto.HotelBookingDTO;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.Instant;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SearchingRoomDTO {
    private Long categoryId;
    @NotNull
    private LocalDate checkIn;
    @NotNull
    private LocalDate checkOut;
    private int numberPeople;
}