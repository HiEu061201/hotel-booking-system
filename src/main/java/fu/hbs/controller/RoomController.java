/*
 * Copyright (C) 2023, FPT University
 * SEP490 - SEP490_G77
 * HBS
 * Hotel Booking System
 *
 * Record of change:
 * DATE          Version    Author           DESCRIPTION
 * 26/10/2023    1.0        HieuLBM          First Deploy
 * 27/10/2023	 2.0	    HieuLBM			 update category
 */

package fu.hbs.controller;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import fu.hbs.dto.CategoryRoomPriceDTO.DateInfoCategoryRoomPriceDTO;
import fu.hbs.dto.User.HotelBookingAvailable;
import fu.hbs.dto.RoomCategoryDTO.ViewRoomCategoryDTO;
import fu.hbs.entities.User;
import fu.hbs.entities.UserRole;
import fu.hbs.exceptionHandler.SearchRoomCustomerExceptionHandler;
import fu.hbs.service.interfaces.UserRoleService;
import fu.hbs.service.interfaces.UserService;
import fu.hbs.service.impl.HotelBookingServiceImpl;
import fu.hbs.utils.StringDealer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import fu.hbs.dto.User.BookRoomByCategory;
import fu.hbs.service.interfaces.RoomByCategoryPriceService;
import fu.hbs.service.interfaces.RoomCategoryService;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class RoomController {
    public static final Integer DEFAULT_NUMBERPERSON = 1;
    private RoomCategoryService roomCategoryService;
    private RoomByCategoryPriceService roomByCategoryService;
    private HotelBookingServiceImpl hotelBookingService;
    @Autowired
    private UserRoleService userRoleService;
    @Autowired
    private UserService userService;
    private StringDealer stringDealer;


    public RoomController(RoomCategoryService roomCategoryService, RoomByCategoryPriceService roomByCategoryService, HotelBookingServiceImpl hotelBookingService) {
        this.roomCategoryService = roomCategoryService;
        this.roomByCategoryService = roomByCategoryService;
        this.hotelBookingService = hotelBookingService;
        this.stringDealer = new StringDealer();
    }

    /**
     * Handles the request to display all room categories available.
     *
     * @param model The model used to pass data to the view.
     * @return The name of the view to render.
     */
    @GetMapping("/room/all")
    public String getRoomCate(Model model, HttpSession session) {
        List<ViewRoomCategoryDTO> categories = roomCategoryService.getAllRoom();
        if (categories != null) {
            model.addAttribute("categories", categories);
            session.setAttribute("categories", categories);
            return "room/viewRoomCustomer";
        }
        model.addAttribute("NotFound", "Không có loại phòng mà bạn cần tìm");
        return "room/viewRoomCustomer";

    }

    /**
     * Handles the request to display rooms of a specific category.
     *
     * @param model      The model used to pass data to the view.
     * @param categoryId The ID of the room category.
     * @return The name of the view to render.
     */
    @GetMapping("/room/category/{categoryId}")
    public String getRoomByCategory(Model model, @PathVariable Long categoryId) {

        BookRoomByCategory bookRoomByCategories = roomCategoryService.getRoomCategoryDetails(categoryId);

        List<ViewRoomCategoryDTO> distinctCategories = roomCategoryService.getAllRoom().stream()
                .filter(roomCategory -> roomCategory.getRoomCategoryId() != categoryId)
                .collect(Collectors.toList());
        model.addAttribute("distinctCategories", distinctCategories);
        model.addAttribute("bookRoomByCategories", bookRoomByCategories);
        return "room/detailRoomCustomer";

    }


    @GetMapping("/room/search")
    public String searchRooms(
            @RequestParam(value = "checkIn", required = false) String checkInString,
            @RequestParam(value = "checkOut", required = false) String checkOutString,
            @RequestParam(value = "numberOfPeople", required = false) Integer numberOfPeople,
            Model model, HttpSession session, Authentication authentication
    ) {
        try {
            HotelBookingAvailable conflictingBookings;
            LocalDate checkIn = LocalDate.parse(checkInString);
            LocalDate checkOut = LocalDate.parse(checkOutString);
            if (numberOfPeople == null) {
                numberOfPeople = DEFAULT_NUMBERPERSON;
                conflictingBookings = hotelBookingService.findBookingsByDates(checkIn,
                        checkOut, numberOfPeople);

            } else {
                conflictingBookings = hotelBookingService.findBookingsByDates(checkIn,
                        checkOut, numberOfPeople);

            }

            if (conflictingBookings.getRoomCategories().isEmpty()) {
                model.addAttribute("error", "Không còn phòng nào trống");
                return "room/errorSearchRoomCustomer";
            }

            List<DateInfoCategoryRoomPriceDTO> dataList = conflictingBookings.getDateInfoCategoryRoomPriceDTOS();


            int pageSize = 7; // Number of records per page

            List<List<DateInfoCategoryRoomPriceDTO>> pages = new ArrayList<>();
            for (int i = 0; i < dataList.size(); i += pageSize) {
                int end = Math.min(i + pageSize, dataList.size());
                pages.add(dataList.subList(i, end));
            }

            boolean checkBooking = true;
            boolean isUserAuthenticated;
            if (authentication != null) {
                UserDetails userDetails = (UserDetails) authentication.getPrincipal();
                User user = userService.getUserbyEmail(userDetails.getUsername());
                UserRole userRole = userRoleService.findByUserIdAndAndRoleId(user.getUserId(), 6L);
                session.setAttribute("checkUser", userRole);
                model.addAttribute("checkUser", userRole);
                isUserAuthenticated = !checkBooking;
            } else {
                isUserAuthenticated = checkBooking;
            }

            session.setAttribute("defaultDate", checkIn);
            session.setAttribute("defaultDate1", checkOut);
            session.setAttribute("numberOfPeople", numberOfPeople);
            model.addAttribute("carouselPages", pages);

            model.addAttribute("conflictingBookings", conflictingBookings);
            model.addAttribute("isUserAuthenticated", isUserAuthenticated);

            return "room/searchRoomCustomer";


        } catch (SearchRoomCustomerExceptionHandler sr) {
            model.addAttribute("error", sr.getMessage());
            return "room/errorSearchRoomCustomer";
        } catch (DateTimeParseException e) {
            model.addAttribute("error", "Ngày không đúng định dạng.");
            return "room/errorSearchRoomCustomer";
        }

    }


}
