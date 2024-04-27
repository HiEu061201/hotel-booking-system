package fu.hbs.dto.HotelBookingDTO;

import fu.hbs.dto.RoomServiceDTO.RoomBookingServiceDTO;
import fu.hbs.entities.*;
import fu.hbs.utils.BookingUtil;
import fu.hbs.utils.DateUtil;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Data
public class ViewCheckInDTO {
    private Long hotelBookingId;
    private Long userId;
    private Long statusId;
    private Long serviceId;
    private int totalRoom;
    private String name;
    private String email;
    private String address;
    private String phone;
    private BigDecimal totalRoomPrice = BigDecimal.ZERO;
    private BigDecimal depositPrice = BigDecimal.ZERO;
    private String checkIn;
    private String checkOut;
    private String checkInActual;
    private String checkOutActual;
    private Long paymentTypeId = 1L;
    private String paymentTypeName;
    private BigDecimal totalServicePrice = BigDecimal.ZERO;
    private List<CheckInBookingDetailsDTO> bookingDetails = new ArrayList<>(); // Correspond each of booking room
    // detail of this booking
    private BigDecimal prepay = BigDecimal.ZERO;
    private BigDecimal taxPrice = BigDecimal.ZERO;
    private BigDecimal totalPriceOfBooking = BigDecimal.ZERO;
    private String note = "";
    public static ViewCheckInDTO valueOf(HotelBooking hotelBooking,
                                         List<BookingRoomDetails> bookingRoomDetails,
                                         PaymentType paymentType,
                                         Map<Long, RoomCategories> roomCategoriesMap) {

        ViewCheckInDTO viewCheckoutDto = new ViewCheckInDTO();
        viewCheckoutDto.setHotelBookingId(hotelBooking.getHotelBookingId());
        viewCheckoutDto.setUserId(hotelBooking.getUserId());
        viewCheckoutDto.setStatusId(hotelBooking.getStatusId());
        viewCheckoutDto.setServiceId(hotelBooking.getServiceId());
        viewCheckoutDto.setTotalRoom(hotelBooking.getTotalRoom());
        viewCheckoutDto.setName(hotelBooking.getName());
        viewCheckoutDto.setEmail(hotelBooking.getEmail());
        viewCheckoutDto.setAddress(hotelBooking.getAddress());
        viewCheckoutDto.setPhone(hotelBooking.getPhone());
        viewCheckoutDto.setDepositPrice(hotelBooking.getDepositPrice());
        viewCheckoutDto.setCheckOut(DateUtil.formatInstantToPattern(hotelBooking.getCheckOut()));
   
            viewCheckoutDto.setCheckInActual(DateUtil.formatInstantToPattern(Instant.now()));
            viewCheckoutDto.setCheckIn(DateUtil.formatInstantToPattern(hotelBooking.getCheckIn()));
            viewCheckoutDto.setCheckOut(DateUtil.formatInstantToPattern(hotelBooking.getCheckOut()));
            viewCheckoutDto.setCheckOutActual(DateUtil.formatInstantToPattern(hotelBooking.getCheckOut()));
    
        viewCheckoutDto.setPaymentTypeId(paymentType.getPaymentId());
        viewCheckoutDto.setPaymentTypeName(paymentType.getPaymentName());



        for (BookingRoomDetails bookingRoomDetail : bookingRoomDetails) {
            CheckInBookingDetailsDTO checkInBookingDetailDTO = CheckInBookingDetailsDTO.valueOf(hotelBooking,
                    roomCategoriesMap.get(bookingRoomDetail.getRoomCategoryId()),
                    bookingRoomDetail, hotelBooking.getCheckIn(),
                    hotelBooking.getCheckOut());
            viewCheckoutDto.getBookingDetails().add(checkInBookingDetailDTO);
        }


        BigDecimal totalRoomPrice = viewCheckoutDto.getBookingDetails().stream()
                .reduce(BigDecimal.ZERO, (subTotal, bookingDetail) -> subTotal.add(bookingDetail.getTotalPrice()), BigDecimal::add);
        

        BigDecimal taxPrice = totalRoomPrice.multiply(BigDecimal.valueOf(0.1));
        BigDecimal totalOfBooking = totalRoomPrice.add(taxPrice);

        viewCheckoutDto.setTaxPrice(taxPrice);
        viewCheckoutDto.setTotalPriceOfBooking(totalOfBooking);
        viewCheckoutDto.setTotalRoomPrice(totalRoomPrice);
        viewCheckoutDto.setPrepay(hotelBooking.getDepositPrice());
        viewCheckoutDto.setNote(hotelBooking.getNote());
        return viewCheckoutDto;
    }
}