/*
 * Copyright (C) 2023, FPT University
 * SEP490 - SEP490_G77
 * HBS
 * Hotel Booking System
 *
 * Record of change:
 * DATE          Version    Author           DESCRIPTION
 * 27/10/2023    1.0        HieuLBM          First Deploy
 */
package fu.hbs.exceptionHandler;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import fu.hbs.entities.User;
import fu.hbs.service.interfaces.UserService;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;

@ControllerAdvice
public class ApplicationExceptionHandler {
    @Autowired
    UserService userService;

    @ExceptionHandler({RoomCategoryNamesNullException.class})
    public String handleRoomCategoryNamesNullException(RoomCategoryNamesNullException ex, RedirectAttributes redirectAttributes, HttpSession session) {
        LocalDate checkin = (LocalDate) session.getAttribute("defaultDate");
        LocalDate checkout = (LocalDate) session.getAttribute("defaultDate1");

        String errorMessage = ex.getMessage();

        if (errorMessage.equals("Không còn phòng nào trống")) {
            redirectAttributes.addFlashAttribute("error", "Không còn phòng nào trống");
        }


        return "redirect:/room/search?checkIn=" + checkin + "&checkOut=" + checkout + "&numberOfPeople=";
    }

    @ExceptionHandler({SearchRoomCustomerExceptionHandler.class})
    public String handleSearchRoomCustomerExceptionHandler(SearchRoomCustomerExceptionHandler sr, RedirectAttributes redirectAttributes, HttpSession session) {
        LocalDate checkin = (LocalDate) session.getAttribute("defaultDate");
        LocalDate checkout = (LocalDate) session.getAttribute("defaultDate1");
        Integer numberOfPeople = (Integer) session.getAttribute("numberOfPeople");
        String errorMessage1 = sr.getMessage();
        if (errorMessage1.equals("CheckIn và CheckOut không thể null")) {
            redirectAttributes.addFlashAttribute("error", "CheckIn và CheckOut không thể null");
        } else if (errorMessage1.equals("CheckIn không trùng CheckOut")) {
            redirectAttributes.addFlashAttribute("error", "CheckIn không trùng CheckOut");
        } else if (errorMessage1.equals("CheckIn phải trước CheckOut")) {
            redirectAttributes.addFlashAttribute("error", "CheckIn phải trước CheckOut");
        } else {
            redirectAttributes.addFlashAttribute("error", "CheckIn và CheckOut phải sau ngày hiện tại");
        }


        return "redirect:/room/search?checkIn=" + checkin + "&checkOut=" + checkout + "&numberOfPeople=" + numberOfPeople;

    }

//    @ExceptionHandler(RoomCategoryNamesNullException.class)
//    public String handleRoomCategoryNamesNullException(RedirectAttributes redirectAttributes, HttpSession session, RoomCategoryNamesNullException ex) {
//        LocalDate checkin = (LocalDate) session.getAttribute("defaultDate");
//        LocalDate checkout = (LocalDate) session.getAttribute("defaultDate1");
//        Integer numberOfPeople = (Integer) session.getAttribute("numberOfPeople");
//
//        redirectAttributes.addAttribute("error", "Không còn phòng nào trống");
//        return "redirect:/room/search?checkIn=" + checkin + "&checkOut=" + checkout + "&numberOfPeople=" + numberOfPeople;
//    }


    @ExceptionHandler(MailExceptionHandler.class)
    public ModelAndView handleMessagingException(MailExceptionHandler ex) {
        ModelAndView modelAndView = new ModelAndView("error");
        modelAndView.addObject("errorMessage", ex.getMessage());
        return modelAndView;
    }

    @ExceptionHandler(value = {UserNotFoundException.class})
    public String handlerContact(UserNotFoundException exception, final Model model) {
        System.err.println(exception.getMessage());
        model.addAttribute("errorMessage", exception.getMessage());

        return "error";
    }

    @ExceptionHandler(value = {UserIvalidException.class})
    public String handlerContact(UserIvalidException exception, final Model model, Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userService.getUserbyEmail(userDetails.getUsername());
        model.addAttribute("user", user);
        model.addAttribute("errorMessage", exception.getMessage());

        return "userProfile";
    }


    @ExceptionHandler(CreateBooingExceptionHandler.class)
    public String handleRuntimeException(Model model, CreateBooingExceptionHandler ex) {
        String errorMessage = ex.getMessage();

        if (errorMessage != null) {
            if (errorMessage.equals("Bạn chưa đặt phòng nào")) {
                model.addAttribute("error", "Bạn đã không đặt phòng nào.");
            } else if (errorMessage.equals("Số lượng phòng không hợp lệ")) {
                model.addAttribute("error", "Số lượng phòng không hợp lệ.");
            } else if (errorMessage.equals("Số lượng phòng không khớp với loại phòng")) {
                model.addAttribute("error", "Số lượng phòng không khớp với loại phòng.");
            } else {
                model.addAttribute("error", "Có lỗi xảy ra.");
            }
        } else {
            model.addAttribute("error", "Có lỗi xảy ra.");
        }

        return "customer/errorBooking";
    }

    @ExceptionHandler(NotEnoughRoomAvalaibleException.class)
    public String handleBooking(Model model, NotEnoughRoomAvalaibleException ex) {
        String errorMessage = ex.getMessage();
        if (errorMessage != null) {
            model.addAttribute("message", "Có lỗi xảy ra.");
        }
        return "errorPage";
    }


//    @ExceptionHandler(CustomIllegalArgumentException.class)
//    public String handleCustomIllegalArgumentException(Model model, CustomIllegalArgumentException ex) {
//        model.addAttribute("error", ex.getMessage());
//        return "errorPage";
//    }

//    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
//    public String handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex, Model model) {
////        model.addAttribute("errorMessage", "Đường link truy cập không đúng");
//        return "errorPage";
//    }


//    @ExceptionHandler(CustomIllegalArgumentException.class)
//    public String handleCustomIllegalArgumentException(Model model, CustomIllegalArgumentException ex) {
//        model.addAttribute("error", ex.getMessage());
//        return "errorPage"; // Create an error page for displaying the error message.
//    }
//    @ExceptionHandler(NotEnoughRoomAvalaibleException.class)
//    public String handleNotEnoughRoomAvalaibleException(Model model, NotEnoughRoomAvalaibleException ex) {
//        model.addAttribute("error", ex.getMessage());
//        return "errorPage"; // Create an error page for displaying the error message.
//    }

}
