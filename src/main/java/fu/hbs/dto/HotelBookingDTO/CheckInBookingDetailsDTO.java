package fu.hbs.dto.HotelBookingDTO;

import fu.hbs.entities.BookingRoomDetails;
import fu.hbs.entities.HotelBooking;
import fu.hbs.entities.RoomCategories;
import fu.hbs.utils.BookingUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

import fu.hbs.entities.Room;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Locale;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class CheckInBookingDetailsDTO {
    private Long categoryId;
    private String categoryName;
    private long roomId;
    private String roomName;
    private BigDecimal price = BigDecimal.ZERO;
    private BigDecimal totalPrice = BigDecimal.ZERO;
    private List<Room> optionRooms = new ArrayList<>();
    private Long bookingDetailId;

    public static CheckInBookingDetailsDTO valueOf(HotelBooking hotelBooking, RoomCategories roomCategory, BookingRoomDetails bookingRoomDetails, Instant checkIn, Instant checkOut) {
        CheckInBookingDetailsDTO checkInBookingDetailsDTO = new CheckInBookingDetailsDTO();
        checkInBookingDetailsDTO.setCategoryId(roomCategory.getRoomCategoryId());
        checkInBookingDetailsDTO.setCategoryName(roomCategory.getRoomCategoryName());
        checkInBookingDetailsDTO.setRoomId(bookingRoomDetails.getRoomId());
        checkInBookingDetailsDTO.setTotalPrice(BookingUtil.calculatePriceBetweenDate(checkIn, checkOut, roomCategory.getRoomCategoryId(), false));
        checkInBookingDetailsDTO.setPrice(BookingUtil.getPriceOfRoom(roomCategory.getRoomCategoryId()));
        LocalDate checkinLocalDate = LocalDateTime.ofInstant(checkIn, ZoneId.systemDefault()).toLocalDate();
        LocalDate checkoutLocalDate = LocalDateTime.ofInstant(checkOut, ZoneId.systemDefault()).toLocalDate();
        List<Room> availableRoom = BookingUtil.findAvailableRoom(roomCategory.getRoomCategoryId(), checkinLocalDate, checkoutLocalDate);
        Room bookedRoom = BookingUtil.findRoomById(bookingRoomDetails.getRoomId());
        availableRoom.add(bookedRoom);
        checkInBookingDetailsDTO.setOptionRooms(availableRoom);
        checkInBookingDetailsDTO.setRoomId(bookedRoom.getRoomId());
        checkInBookingDetailsDTO.setRoomName(bookedRoom.getRoomName());
        checkInBookingDetailsDTO.setBookingDetailId(bookingRoomDetails.getBookingRoomId());
        return checkInBookingDetailsDTO;
    }
}