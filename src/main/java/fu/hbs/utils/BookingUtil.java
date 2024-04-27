package fu.hbs.utils;

import java.math.BigDecimal;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import java.util.function.Function;
import java.util.stream.Collectors;

import fu.hbs.entities.BookingRoomDetails;
import fu.hbs.entities.CategoryRoomPrice;
import fu.hbs.entities.HotelBookingService;
import fu.hbs.entities.Room;
import fu.hbs.entities.RoomCategories;
import fu.hbs.entities.RoomService;
import fu.hbs.service.interfaces.CategoryRoomPriceService;
import fu.hbs.service.interfaces.HotelBookingServiceService;
import fu.hbs.service.interfaces.RoomCategoryService;
import fu.hbs.service.interfaces.ServiceService;

@Component
public class BookingUtil {

    private static CategoryRoomPriceService staticCategoryRoomPriceService;
    private static HotelBookingServiceService statichHotelBookingServiceService;

    private static ServiceService staticRoomServiceService;

    private static RoomCategoryService staticRoomCategoryService;

    private static fu.hbs.service.interfaces.HotelBookingService staticHotelBookingService;

    private static fu.hbs.service.interfaces.RoomService staticRoomService;

    public BookingUtil(CategoryRoomPriceService categoryRoomPriceService,
                       HotelBookingServiceService hotelBookingServiceService, ServiceService roomServiceService,
                       RoomCategoryService roomCategoryService, fu.hbs.service.interfaces.HotelBookingService hotelBookingService,
                       fu.hbs.service.interfaces.RoomService roomService) {
        BookingUtil.staticCategoryRoomPriceService = categoryRoomPriceService;
        BookingUtil.statichHotelBookingServiceService = hotelBookingServiceService;
        BookingUtil.staticRoomServiceService = roomServiceService;
        BookingUtil.staticRoomCategoryService = roomCategoryService;
        BookingUtil.staticHotelBookingService = hotelBookingService;
        BookingUtil.staticRoomService = roomService;
    }

    public static long calculateRoomNumber(RoomCategories roomCategories, List<BookingRoomDetails> bookingRoomDetails) {
        return bookingRoomDetails.stream().filter(
                        bookingRoomDetail -> bookingRoomDetail.getRoomCategoryId().equals(roomCategories.getRoomCategoryId()))
                .count();
    }

    public static BigDecimal calculatePriceBetweenDate(Instant checkIn, Instant checkout, Long categoryId,
                                                       Boolean isCalculateForCheckout) {
        Duration durationOfStay = Duration.between(checkIn, checkout);

        BigDecimal pricePer = getPriceOfRoom(categoryId);
        // If the user checked out earlier than 24 hours, deduct 1 day's price as a
        // refund
        long durationOfStayDays = durationOfStay.toDays();
        float multipler = 0L;
        checkIn = checkIn.truncatedTo(ChronoUnit.DAYS);
        checkout = checkout.truncatedTo(ChronoUnit.DAYS);
        // If checkin same day multipler equal 1 => mean 1 day
        if (checkIn.equals(checkout)) {
            multipler = 1;
        } else {
            while (checkIn.isBefore(checkout)) {
                int dayType = staticHotelBookingService
                        .getDayType(LocalDateTime.ofInstant(checkIn, ZoneId.systemDefault()).toLocalDate());
                if (dayType == 3) {
                    multipler += 3;
                } else if (dayType == 2) {
                    multipler += 1.5F;
                } else {
                    multipler += 1;
                }
                checkIn = checkIn.plus(1, ChronoUnit.DAYS);
            }
        }

        BigDecimal totalPrice = BigDecimal.ZERO;
        if (isCalculateForCheckout) {
            Duration durationOfCheckoutSooner = Duration.between(Instant.now(), checkout);
            long soonerDays = durationOfCheckoutSooner.toDays();
            totalPrice = pricePer.multiply(BigDecimal.valueOf(multipler));
            if (soonerDays > 1) {
                BigDecimal refund = pricePer.multiply(BigDecimal.valueOf(soonerDays));
                return totalPrice.subtract(refund);
            }
        } else {
            totalPrice = pricePer.multiply(BigDecimal.valueOf(multipler));
        }
        return totalPrice;
    }

    public static BigDecimal getPriceOfRoom(Long categoryId) {
        CategoryRoomPrice categoryRoomPrice = staticCategoryRoomPriceService.findByCateRoomPriceId(categoryId);
        return categoryRoomPrice.getPrice();
    }

    public static List<HotelBookingService> getAllHotelBookingService(Long hotelId) {
        return statichHotelBookingServiceService.findAllByHotelBookingId(hotelId);
    }

    public static BigDecimal calculateTotalPriceOfUseService(RoomService roomService, int quantity) {
        BigDecimal pricePer = roomService.getServicePrice();
        return pricePer.multiply(BigDecimal.valueOf(quantity));
    }

    public static Map<Long, RoomService> getAllRoomServiceAsMap() {
        return staticRoomServiceService.getAllServices().stream()
                .collect(Collectors.toMap(RoomService::getServiceId, Function.identity()));
    }

    public static Map<Long, RoomCategories> getAllRoomCategoryAsMap() {
        return staticRoomCategoryService.getAllRoomCategories().stream()
                .collect(Collectors.toMap(RoomCategories::getRoomCategoryId, Function.identity()));
    }

    public static BigDecimal calculateTotalPriceOfBooking(BigDecimal servicePrice, BigDecimal roomPrice, BigDecimal additionalFee) {
        BigDecimal taxPrice = servicePrice.add(additionalFee).add(roomPrice).multiply(BigDecimal.valueOf(0.1));
        return servicePrice.add(roomPrice).add(taxPrice); // Total is not related to prepay
    }

    public static List<Room> findAvailableRoom(Long roomCategoryId, LocalDate checkIn, LocalDate checkOut) {
        return staticRoomService.findAvailableRoom(roomCategoryId, checkIn, checkOut);
    }

    public static Room findRoomById(Long roomId) {
        return staticRoomService.findRoomById(roomId);
    }

    public static BigDecimal calculateRoomCostForCheckOut(LocalDate checkInDate, LocalDate checkOutDate,
                                                          Long categoryId) {
// Set default check-in and check-out times
        LocalTime defaultCheckInTime = LocalTime.of(14, 0);
        LocalTime defaultCheckOutTime = LocalTime.of(12, 0);

// Calculate the total number of days for reservation
        long totalDays = ChronoUnit.DAYS.between(checkInDate, checkOutDate);
        totalDays = totalDays == 0 ? totalDays + 1 : totalDays;
// Initialize total cost
        BigDecimal totalCost = BigDecimal.ZERO;
        BigDecimal totalRefundCost = BigDecimal.ZERO;
        BigDecimal dailyPrice = getPriceOfRoom(categoryId);
// Loop through each day of the reservation
        for (int i = 0; i < totalDays; i++) {
            LocalDate currentDate = checkInDate.plusDays(i);
            double multiplier = getMultiplier(currentDate);

// Calculate cost for the day based on the multiplier
            BigDecimal dailyCost = dailyPrice.multiply(BigDecimal.valueOf(multiplier));

// Check for early check-out and apply refund if applicable
            if (currentDate.equals(checkOutDate.minusDays(1)) && LocalTime.now().isBefore(defaultCheckOutTime)) {
                long hours = defaultCheckOutTime.until(LocalTime.now(), ChronoUnit.HOURS);
                BigDecimal refundAmount = dailyCost.multiply(
                        BigDecimal.valueOf(hours).divide(BigDecimal.valueOf(24), 10, BigDecimal.ROUND_HALF_UP));
                totalRefundCost = totalRefundCost.add(refundAmount);
            } else {
                totalCost = totalCost.add(dailyCost);
            }
        }

// Check for late check-out and apply additional fee if applicable
        LocalTime minus = LocalTime.now().minus(defaultCheckOutTime.getHour(), ChronoUnit.HOURS);
        boolean after = minus.isAfter(LocalTime.of(2, 0));
        if (LocalDate.now().isAfter(checkOutDate) && after) {
            BigDecimal additionalFee = totalCost.multiply(BigDecimal.valueOf(0.15));
            totalCost = totalCost.add(additionalFee);
        }

        return totalCost.subtract(totalRefundCost);
    }

    private static double getMultiplier(LocalDate date) {
        int dayType = staticHotelBookingService.getDayType(date);
        if (dayType == 3) {
            return 3;
        } else if (dayType == 2) {
            return 1.5;
        } else {
            return 1;
        }
    }
}