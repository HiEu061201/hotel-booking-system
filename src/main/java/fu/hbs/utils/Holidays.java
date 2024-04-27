package fu.hbs.utils;

import java.time.LocalDate;
import java.util.*;

public class Holidays {
    public Map<String, LocalDate> holidayDates;

    public Holidays() {
        initializeHolidayDates();
    }

    private void initializeHolidayDates() {
        holidayDates = new HashMap<>();
        // Đặt ngày tháng tương ứng cho các ngày lễ
        holidayDates.put("Tết Dương Lịch", LocalDate.of(1, 1, 1));
        holidayDates.put("Ngày thống nhất đất nước", LocalDate.of(1, 4, 30));
        holidayDates.put("Ngày quốc tế lao động", LocalDate.of(1, 5, 1));
        holidayDates.put("Ngày quốc khánh", LocalDate.of(1, 9, 2));
    }


}
