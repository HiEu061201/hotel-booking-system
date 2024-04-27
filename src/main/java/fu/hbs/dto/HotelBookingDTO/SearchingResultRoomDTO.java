package fu.hbs.dto.HotelBookingDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SearchingResultRoomDTO {
    private int numberAvailable;
    private BigDecimal price;
    private Long roomCategoryId;
    private String roomCategoryName;
    private String description;
    private Double square;
    private int numberPerson;
    private String image;
    private String bed;
    private int bookedNumber = 0;

}