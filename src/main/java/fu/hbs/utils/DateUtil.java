package fu.hbs.utils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;

public class DateUtil {
    public static String formatInstantToPattern(Instant instant) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, dd-MM-yyyy HH:mm:ss", new Locale("vi", "VN"));
        // Format the Instant with the specified formatter and locale
        return formatter.format(instant.atZone(ZoneId.of("Asia/Ho_Chi_Minh")));
    }


    public static LocalDateTime convertStringToDateTime(String inputString) {
        int year = Integer.parseInt(inputString.substring(0, 4));
        int month = Integer.parseInt(inputString.substring(4, 6));
        int day = Integer.parseInt(inputString.substring(6, 8));
        int hours = Integer.parseInt(inputString.substring(8, 10));
        int minutes = Integer.parseInt(inputString.substring(10, 12));
        int seconds = Integer.parseInt(inputString.substring(12, 14));

        return LocalDateTime.of(year, month, day, hours, minutes, seconds);
    }

}