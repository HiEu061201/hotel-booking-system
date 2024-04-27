/*
 * Copyright (C) 2023, FPT University
 * SEP490 - SEP490_G77
 * HBS
 * Hotel Booking System
 *
 * Record of change:
 * DATE          Version    Author           DESCRIPTION
 * 18/10/2023    1.0        HieuLBM          First Deploy
 *  *
 */
package fu.hbs.utils;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringDealer {
    public StringDealer() {
    }

    /**
     * Trim excessive whitespace in a string.
     *
     * @param str the input string
     * @return the trimmed string with normalized whitespace
     */
    public String trimMax(String str) {
        if (str == null)
            return "";
        return str.trim().replaceAll("\\s+", " ");
    }

    /**
     * Convert a date string to a SQL Date object with the "yyyy-MM-dd" format.
     *
     * @param str the date string to convert
     * @return the SQL Date object representing the date
     */
    public Date convertToDateAndFormat(String str) {
        Date date;
        try {
            date = new Date((new SimpleDateFormat("yyyy-MM-dd").parse(str)).getTime());
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        return date;
    }

    /**
     * Check if an email string matches a valid email address pattern.
     *
     * @param email the email string to check
     * @return true if the email is valid, false otherwise
     */
    public boolean checkEmailRegex(String email) {
        String regex = "^[a-zA-Z0-9!#$%&'*+/=?^`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?$";
        return email.matches(regex);
    }

    /**
     * Check if a phone number string matches a valid phone number pattern.
     *
     * @param phone the phone number string to check
     * @return true if the phone number is valid, false otherwise
     */
    public boolean checkPhoneRegex(String phone) {
        String regex = "^(0?)(3[2-9]|5[6|8|9]|7[0|6-9]|8[0-6|8|9]|9[0-4|6-9])[0-9]{7}$";
        return phone.matches(regex);
    }

    /**
     * Check if a password string matches a valid password pattern. Passwords should
     * contain at least one lowercase letter, one uppercase letter, one digit, and
     * one special character from the set {!@#$%}. Password length should be between
     * 8 and 20 characters.
     *
     * @param password the password string to check
     * @return true if the password is valid, false otherwise
     */

    public boolean checkPasswordRegex(String password) {
        String regex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#$%]).{8,20}$";
        return password.matches(regex);
    }

    /**
     * Calculate the number of days between two dates.
     *
     * @param startDate the start date
     * @param endDate   the end date
     * @return the number of days between the two dates
     */
    public long dateDiff(Date startDate, Date endDate) {
        long daysDiff = 0L;
        try {
            // calculating the difference b/w startDate and endDate

            long getDiff = endDate.getTime() - startDate.getTime();

            // using TimeUnit class from java.util.concurrent package
            daysDiff = TimeUnit.MILLISECONDS.toDays(getDiff);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return daysDiff;
    }

    /**
     * Convert a Date object to a LocalDate object.
     *
     * @param date The Date object to convert.
     * @return The equivalent LocalDate object.
     */
    public LocalDate convertDateToLocalDate(java.util.Date date) {
        Instant instant = date.toInstant();
        ZoneId zoneId = ZoneId.systemDefault(); // Sử dụng múi giờ hệ thống
        return instant.atZone(zoneId).toLocalDate();
    }

    public static String extractNumberFromString(String original) {
        Pattern pattern = Pattern.compile("\\d+");
        Matcher matcher = pattern.matcher(original);
        if (matcher.find()) {
            String number = matcher.group();
            return number;
        }
        return "";
    }

    public void uploadFile(MultipartFile file, String uploadDirectory) throws IOException {

        // Tạo đường dẫn đến tệp đích
        String destinationPath = uploadDirectory + file.getOriginalFilename();
        System.out.println(destinationPath);
        // Tạo một tệp đích và ghi nội dung của tệp tải lên vào tệp đó
        File destinationFile = new File(destinationPath);

        try (FileOutputStream fileOutputStream = new FileOutputStream(destinationFile)) {
            fileOutputStream.write(file.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
