package fu.hbs.dto.HotelBookingDTO;

import fu.hbs.dto.CategoryRoomPriceDTO.DateInfoCategoryRoomPriceDTO;
import fu.hbs.entities.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class BookingDetailsDTO {
    private User user;
    private HotelBooking hotelBooking;
    private List<RoomInDetailsDTO> bookingRoomDetails;
    private Map<Long, List<Room>> groupedRooms;
    private UserInBookingDetailsDTO userInBookingDetailsDTO;
    private Map<Long, BigDecimal> totalPriceByCategoryId;
    private List<DateInfoCategoryRoomPriceDTO> dateInfoList;
    private CustomerCancellation customerCancellation;
    private PaymentType paymentType;
    private Transactions transactions;
    private TransactionsType transactionsType;
    private List<Transactions> transactionsList;
    private String checkIn;
    private String checkOut;
    private String checkInActual;
    private String checkOutActual;
    private BigDecimal roomPrice;
    private List<HotelBookingService> hotelBookingServiceList;
    private List<RoomService> roomServiceList;
}
