/*
 * Copyright (C) 2023, FPT University
 * SEP490 - SEP490_G77
 * HBS
 * Hotel Booking System
 *
 * Record of change:
 * DATE          Version    Author           DESCRIPTION
 * 14/10/2023    1.0        HieuLBM          First Deploy
 * 16/10/2023    1.1        HieuLBM          Edit Link
 *  *
 */
package fu.hbs.controller;


import fu.hbs.dto.RoomCategoryDTO.ViewRoomCategoryDTO;
import fu.hbs.service.interfaces.RoomCategoryService;
import fu.hbs.service.interfaces.UserRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import fu.hbs.entities.User;
import fu.hbs.service.interfaces.UserService;
import jakarta.servlet.http.HttpSession;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
public class LoginController {
    @Autowired
    private UserService userService;
    @Autowired
    private UserRoleService userRoleService;
    @Autowired
    private RoomCategoryService roomCategoryService;

    /**
     * Displays the login form if the user is not already authenticated.
     *
     * @param action The authentication object.
     * @return The login form view.
     */
    @GetMapping("/login")
    public String loginForm(Authentication action) {
        if (action != null) {

            return "redirect:/homepage";
        }
        return "authentication/login";
    }


    /**
     * Handles the rendering of the homepage based on the user's role.
     *
     * @param authentication The authentication object.
     * @param model          The model to add attributes.
     * @param session        The HTTP session for storing user-related attributes.
     * @return The view of the homepage based on the user's role.
     */
    @GetMapping("/homepage")
    public String homepage(Authentication authentication, Model model, HttpSession session) {
        if (authentication != null) {
            List<ViewRoomCategoryDTO> categories = roomCategoryService.getAllRoom();
            session.setAttribute("categories", categories);
            LocalDate today = LocalDate.now();
            LocalDate nextDay = today.plusDays(1);
            String todayString = today.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            String nextDayString = nextDay.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            session.setAttribute("defaultDate", todayString);
            session.setAttribute("defaultDate1", nextDayString);
            UserDetails user = (UserDetails) authentication.getPrincipal();

            User user1 = userService.getUserbyEmail(user.getUsername());

            session.setAttribute("name", user1.getName());
            session.setAttribute("accountDetail", user.getUsername());


            for (GrantedAuthority authority : user.getAuthorities()) {
                String authorityName = authority.getAuthority();
                if (authorityName.equalsIgnoreCase("ADMIN")) {
                    return "redirect:/admin/viewListUser";
                } else if (authorityName.equalsIgnoreCase("MANAGEMENT")) {
                    return "redirect:/management/viewRoom";
                } else if (authorityName.equalsIgnoreCase("RECEPTIONISTS")) {
                    return "redirect:/receptionist/createRoomReceptionist";
                } else if (authorityName.equalsIgnoreCase("SALEMANAGER")) {
                    return "redirect:/saleManager/listRefund";
                } else if (authorityName.equalsIgnoreCase("ACCOUNTING")) {
                    return "redirect:/accounting/listConfirmRefund";
                } else if (authorityName.equalsIgnoreCase("CUSTOMER")) {
                    return "homepage";
                }
            }
        }
        return "redirect:/login";
    }
}