/*
 * Copyright (C) 2023, FPT University
 * SEP490 - SEP490_G77
 * HBS
 * Hotel Booking System
 *
 * Record of change:
 * DATE          Version    Author           DESCRIPTION
 *  2/11/2023    1.0        HieuLBM          First Deploy
 *
 */


package fu.hbs.controller;


import fu.hbs.dto.HotelBookingDTO.ViewHotelBookingDTO;
import fu.hbs.entities.Feedback;
import fu.hbs.entities.User;
import fu.hbs.service.interfaces.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;


@Controller
public class CustomerController {
    @Autowired
    private UserService userService;
    @Autowired
    private HotelBookingService hotelBookingService;
    @Autowired
    private FeedbackService feedbackService;

    @GetMapping("/customer/viewBooking")
    public String booking(Authentication authentication, Model model,
                          @RequestParam(defaultValue = "0") Integer page,
                          @RequestParam(defaultValue = "10") Integer size,
                          @RequestParam(value = "checkIn", required = false) String checkin,
                          @RequestParam(name = "status", required = false) Integer status) {
        LocalDate checkIn = null;
        if (checkin == null || checkin.isEmpty()) {
            checkIn = null;
        } else {
            String expectedDateFormat = "yyyy-MM-dd";
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(expectedDateFormat);
            try {
                checkIn = LocalDate.parse(checkin, formatter);
            } catch (DateTimeParseException e) {
                model.addAttribute("error1", "Ngày tháng năm không đúng định dạng");
                return "customer/bookingHistory";
            }
        }


        int defaultPage = (page != null && page >= 0) ? page : 0;
        int defaultSize = (size != null && size > 0) ? size : 10;
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userService.getUserbyEmail(userDetails.getUsername());

        Page<ViewHotelBookingDTO> hotelBookings = hotelBookingService.findAllBookingByUserId(user.getUserId(), checkIn, status, defaultPage, defaultSize);


        List<ViewHotelBookingDTO> viewHotelBookingDTOS = hotelBookings.getContent();

        model.addAttribute("currentPage", hotelBookings.getNumber());
        model.addAttribute("pageSize", hotelBookings.getSize());
        model.addAttribute("totalPages", hotelBookings.getTotalPages());

        model.addAttribute("checkInDate", checkIn);
        model.addAttribute("status", status);
        if (viewHotelBookingDTOS.isEmpty()) {
            model.addAttribute("error", "Không tìm thấy");

            return "customer/bookingHistory";
        }

        model.addAttribute("viewHotelBookingDTOS", viewHotelBookingDTOS);

        return "customer/bookingHistory";
    }

    @PostMapping("/customer/submitFeedback")
    public ResponseEntity<?> submitFeedback(@RequestBody Feedback feedback, Model model) {
        try {

            feedbackService.saveFeedback(feedback);
            return ResponseEntity.ok("Phản hồi đã được lưu thành công.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi khi lưu phản hồi.");
        }


    }
}
