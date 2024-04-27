package fu.hbs.utils;

import fu.hbs.dto.CategoryRoomPriceDTO.DateInfoCategoryRoomPriceDTO;
import fu.hbs.entities.CategoryRoomPrice;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


@Component
public class BookingDayUtils {

    private final String tetDuongLichConfig;
    private final String ngayThongNhatDatNuocConfig;
    private final String ngayQuocTeLaoDongConfig;
    private final String ngayQuocKhanhConfig;

    public BookingDayUtils(
            @Value("${app.holidays.tetDuongLich}") String tetDuongLichConfig,
            @Value("${app.holidays.ngayThongNhatDatNuoc}") String ngayThongNhatDatNuocConfig,
            @Value("${app.holidays.ngayQuocTeLaoDong}") String ngayQuocTeLaoDongConfig,
            @Value("${app.holidays.ngayQuocKhanh}") String ngayQuocKhanhConfig
    ) {
        this.tetDuongLichConfig = tetDuongLichConfig;
        this.ngayThongNhatDatNuocConfig = ngayThongNhatDatNuocConfig;
        this.ngayQuocTeLaoDongConfig = ngayQuocTeLaoDongConfig;
        this.ngayQuocKhanhConfig = ngayQuocKhanhConfig;
    }


    // Hàm tính tổng giá cho một CategoryRoomPrice dựa trên dateInfoList
    public static BigDecimal calculateTotalForCategory(CategoryRoomPrice
                                                               cpr, List<DateInfoCategoryRoomPriceDTO> dateInfoList) {
        BigDecimal totalForCategory = BigDecimal.ZERO;

        int daysBetween = dateInfoList.size(); //

        for (int i = 0; i < daysBetween - 1; i++) {
            BigDecimal multiplier = BigDecimal.ONE; // Mặc định là 1

            switch (dateInfoList.get(i).getDayType()) {
                case 2:
                    multiplier = new BigDecimal("1.5");
                    break;
                case 3:
                    multiplier = new BigDecimal("3");
                    break;
                default:
                    // Mặc định không thay đổi giá
                    break;
            }
            BigDecimal price = cpr.getPrice().multiply(multiplier); // Tính giá tiền cho cpr cụ thể
            totalForCategory = totalForCategory.add(price);
        }

        return totalForCategory;
    }


    public List<DateInfoCategoryRoomPriceDTO> processDateInfo(LocalDate startDate, LocalDate endDate) {
        List<DateInfoCategoryRoomPriceDTO> dateInfoList = new ArrayList<>();

        int dayType = getDayType(startDate);

        if (startDate.isEqual(endDate)) {
            // Trường hợp startDate và endDate cùng một ngày, thêm hai phần tử
            dateInfoList.add(new DateInfoCategoryRoomPriceDTO(startDate, dayType));
            startDate = startDate.plusDays(1);
            dateInfoList.add(new DateInfoCategoryRoomPriceDTO(startDate, dayType));
        } else {
            // Thêm các phần tử trong khoảng từ startDate đến endDate
            while (!startDate.isAfter(endDate)) {
                dateInfoList.add(new DateInfoCategoryRoomPriceDTO(startDate, dayType));
                startDate = startDate.plusDays(1);
            }
        }


        return dateInfoList;
    }


    /**
     * Get a list of holidays for a specific year.
     *
     * @param year The year for which holidays are requested.
     * @return A list of LocalDate objects representing holidays.
     */
    public List<LocalDate> getHolidays(int year) {
        List<LocalDate> holidays = new ArrayList<>();

        // Chuyển đổi các ngày lễ từ cấu hình thành LocalDate
        LocalDate tetDuongLich = LocalDate.parse(year + "-" + tetDuongLichConfig);
        LocalDate ngayThongNhatDatNuoc = LocalDate.parse(year + "-" + ngayThongNhatDatNuocConfig);
        LocalDate ngayQuocTeLaoDong = LocalDate.parse(year + "-" + ngayQuocTeLaoDongConfig);
        LocalDate ngayQuocKhanh = LocalDate.parse(year + "-" + ngayQuocKhanhConfig);

        holidays.add(tetDuongLich);
        holidays.add(ngayThongNhatDatNuoc);
        holidays.add(ngayQuocTeLaoDong);
        holidays.add(ngayQuocKhanh);

        return holidays;
    }

    /**
     * Determine the day type (weekday, weekend, or holiday) for a given date.
     *
     * @param startDate The date to determine the day type.
     * @return An integer representing the day type (1: weekday, 2: weekend, 3:
     * holiday).
     */
    public int getDayType(LocalDate startDate) {
        if (getHolidays(startDate.getYear()).contains(startDate)) {
            return 3; // holidays
        }
        DayOfWeek dayOfWeek = startDate.getDayOfWeek();

        if (!(dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY)) {
            return 1; // weekday
        } else {
            return 2; // weekend
        }
    }


}
