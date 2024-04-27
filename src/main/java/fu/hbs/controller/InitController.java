/*
 * Copyright (C) 2023, FPT University
 * SEP490 - SEP490_G77
 * HBS
 * Hotel Booking System
 *
 * Record of change:
 * DATE          Version    Author           DESCRIPTION
 * 14/10/2023    1.0        HieuLBM          First Deploy
 * 16/10/2023	 1.1		HieuLBM			 Add accessDenied
 */

package fu.hbs.controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;


import fu.hbs.dto.RoomCategoryDTO.ViewRoomCategoryDTO;
import fu.hbs.service.interfaces.RoomCategoryService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class InitController {
    @Autowired
    private RoomCategoryService roomCategoryService;

    /**
     * Displays the home page when the root URL is accessed.
     *
     * @return The home page view.
     */
    @GetMapping("/")
    public String home(HttpSession session, Model model) {
        List<ViewRoomCategoryDTO> categories = roomCategoryService.getAllRoom();
        session.setAttribute("categories", categories);
        // Check if default date is already set in the session
        if (session.getAttribute("defaultDate") == null) {
            // Lấy ngày hôm nay và định dạng thành chuỗi ngày-tháng-năm
            LocalDate today = LocalDate.now();
            LocalDate nextDay = today.plusDays(1);
            String todayString = today.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            String nextDayString = nextDay.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            // Đặt giá trị mặc định cho trường ngày
            session.setAttribute("defaultDate", todayString);
            session.setAttribute("defaultDate1", nextDayString);
        }
        model.addAttribute("activePage", "/");
        return "homepage";
    }

    @GetMapping("/room/reversion")
    public String redirectToLongURL(HttpSession session, Model model) {
        LocalDate today = LocalDate.now();
        LocalDate nextDay = today.plusDays(1);
        String todayString = today.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String nextDayString = nextDay.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        model.addAttribute("activePage", "/room/reversion");
        return "redirect:/room/search?checkIn=" + todayString + "&checkOut=" + nextDayString + "&numberOfPeople=";

    }
}