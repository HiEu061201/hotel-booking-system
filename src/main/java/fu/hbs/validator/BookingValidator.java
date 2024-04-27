package fu.hbs.validator;

import fu.hbs.entities.HotelBooking;
import fu.hbs.entities.Room;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class BookingValidator {
	public static boolean isValidToCheckIn(Instant checkIn) {
		Instant currentDay = Instant.now().truncatedTo(ChronoUnit.MINUTES);
        Instant checkInTruncated = checkIn.truncatedTo(ChronoUnit.MINUTES);
        return !currentDay.isBefore(checkInTruncated);
    }

    public static boolean isExistNotReadyRoom(List<Room> allBookedRooms) {
    	return allBookedRooms.stream().anyMatch(room -> room.getRoomStatusId() != 2L);
    }

    public static boolean haveCheckedInOrNot(HotelBooking hotelBooking) {
        return hotelBooking.getStatusId() == 2;
    }

    public static boolean haveCheckedOutOrNot(HotelBooking hotelBooking) {
        return hotelBooking.getStatusId() == 3;
    }

	public static boolean isValidTimeForBooking(Instant checkIn, Instant checkOut) {
		Instant currentDay = Instant.now().truncatedTo(ChronoUnit.DAYS);
		Instant checkInTruncated = checkIn.truncatedTo(ChronoUnit.DAYS);
		Instant checkOutTruncated = checkOut.truncatedTo(ChronoUnit.DAYS);
		if (currentDay.isAfter(checkInTruncated)) {
			return false;
		}
		if (!checkOutTruncated.isAfter(checkInTruncated)) {
			return false;
		}
		return true;
	}
    
    

    public static boolean isValidToCheckOut(HotelBooking hotelBooking) {
        boolean isCheckIn = haveCheckedInOrNot(hotelBooking);
        if (!isCheckIn) {
            return false;
        }
        return true;
}
}