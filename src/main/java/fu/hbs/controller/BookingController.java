/*
 * Copyright (C) 2023, FPT University
 * SEP490 - SEP490_G77
 * HBS
 * Hotel Booking System
 *
 * Record of change:
 * DATE          Version    Author           DESCRIPTION
 * 10/11/2023    1.0        HieuLBM          First Deploy
 *
 */

package fu.hbs.controller;

import fu.hbs.constant.TransactionMessage;
import fu.hbs.dto.User.CancellationFormDTO;
import fu.hbs.dto.HotelBookingDTO.BookingDetailsDTO;
import fu.hbs.dto.HotelBookingDTO.CreateBookingDTO;
import fu.hbs.dto.HotelBookingDTO.GuestBookingDTO;
import fu.hbs.dto.HotelBookingDTO.SearchingResultRoomDTO;
import fu.hbs.dto.HotelBookingDTO.SearchingRoomDTO;
import fu.hbs.entities.*;
import fu.hbs.exceptionHandler.CancellationExistException;
import fu.hbs.exceptionHandler.ResetExceptionHandler;
import fu.hbs.service.interfaces.*;
import fu.hbs.service.interfaces.HotelBookingService;
import fu.hbs.service.interfaces.RoomService;
import fu.hbs.service.impl.VNPayService;
import fu.hbs.utils.DateUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.google.gson.Gson;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
public class BookingController {
    @Autowired
    private UserService userService;
    @Autowired
    private HotelBookingService hotelBookingService;
    @Autowired
    private RoomService roomService;
    @Autowired
    private VNPayService vnPayService;
    @Autowired
    private TransactionsService transactionsService;
    @Autowired
    private BookingRoomDetailsService bookingRoomDetailsService;
    @Autowired
    private CustomerCancellationReasonService customerCancellationReasonService;
    @Autowired
    private UserRoleService userRoleService;
    @Autowired
    private BankListService bankListService;

    @GetMapping("/room/addBooking")
    public String addBooking(
            @RequestParam(name = "RoomCategoryId", required = false) List<Long> roomCategoryNames,
            @RequestParam(name = "selectedRoomCategory", required = false) List<Integer> selectedRoomCategories,
            Model model, HttpSession session, Authentication authentication) throws MethodArgumentTypeMismatchException {

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


        LocalDate checkIn = (LocalDate) session.getAttribute("defaultDate");
        LocalDate checkOut = (LocalDate) session.getAttribute("defaultDate1");

        CreateBookingDTO createBookingDTO = hotelBookingService.createBooking(
                roomCategoryNames, selectedRoomCategories, checkIn, checkOut);

        model.addAttribute("checkIn", checkIn.format(DateTimeFormatter.ofPattern("EEEE, dd MMMM , yyyy")));
        model.addAttribute("checkOut", checkOut.format(DateTimeFormatter.ofPattern("EEEE, dd MMMM , yyyy")));
        model.addAttribute("createBookingDTO", createBookingDTO);
        session.setAttribute("createBookingDTO", createBookingDTO);
        model.addAttribute("isUserAuthenticated", isUserAuthenticated);
        return "customer/createBooking";

    }


    @PostMapping(value = "/room/addBooking", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> submitOrder(@RequestBody GuestBookingDTO guestBookingDTO, Model model, HttpSession session) {
        try {
            BigDecimal doubleValue = guestBookingDTO.getPaymentAmount();
            session.setAttribute("orderTotal", doubleValue);
            session.setAttribute("orderInfo", guestBookingDTO.getOrderInfo());
            session.setAttribute("guestBookingDTO", guestBookingDTO);

            return ResponseEntity.ok("Gửi thành công.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Gửi thất bại.");
        }

    }

    @GetMapping("/room/invoice")
    public String showInvoice(Model model) {
        return "redirect:/submitOrder";
    }

    @GetMapping("/submitOrder")
    public String submitOrder(HttpSession session, HttpServletRequest request) {

        BigDecimal orderTotal = (BigDecimal) session.getAttribute("orderTotal");
        String orderInfo = (String) session.getAttribute("orderInfo");
        String baseUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
        String vnpayUrl = vnPayService.createOrder(orderTotal, orderInfo, baseUrl, "/vnpay-payment");
        return "redirect:" + vnpayUrl;
    }

    @GetMapping("/vnpay-payment")
    public String GetMapping(HttpServletRequest request, Model model, Authentication authentication, HttpSession session) throws ResetExceptionHandler {
        int paymentStatus = vnPayService.orderReturn(request);
        Transactions vnpayTransactions = new Transactions();


        String orderInfo = request.getParameter("vnp_OrderInfo");
        String paymentTime = request.getParameter("vnp_PayDate");
        String transactionId = request.getParameter("vnp_TransactionNo");
        String totalPrice = request.getParameter("vnp_Amount");

        BigDecimal bigDecimalValue = new BigDecimal(totalPrice).divide(BigDecimal.valueOf(100), 2, BigDecimal.ROUND_HALF_UP);
        LocalDateTime localDatePaymentTime = DateUtil.convertStringToDateTime(paymentTime);

        DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        String formattedDateTime = localDatePaymentTime.format(formatter1);

        model.addAttribute("orderId", orderInfo);
        model.addAttribute("totalPrice", bigDecimalValue);
        model.addAttribute("paymentTime", formattedDateTime);
        model.addAttribute("transactionId", transactionId);

        // Define the date format

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");


        // Parse the string into a LocalDateTime object
        LocalDateTime localDateTime = LocalDateTime.parse(paymentTime, formatter);

        if (paymentStatus == 1) {

            Long hotelBookingId = hotelBookingService.saveRoomAfterBooking(authentication, session, bigDecimalValue);
            vnpayTransactions.setVnpayTransactionId(transactionId);
            vnpayTransactions.setHotelBookingId(hotelBookingId);
            vnpayTransactions.setAmount(bigDecimalValue);
            vnpayTransactions.setCreatedDate(localDateTime);
            vnpayTransactions.setStatus("Thành công");
            vnpayTransactions.setPaymentId(1L);
            vnpayTransactions.setContent(TransactionMessage.PRE_PAY.getMessage());
            vnpayTransactions.setTransactionsTypeId(1L);
            transactionsService.saveTransactions(vnpayTransactions);


            return "customer/ordersuccess";
        }
        return "customer/orderfail";
    }

    @GetMapping("/customer/bookingDetails/{hotelBookingId}")
    public String bookingDetails(Model model, Authentication authentication,
                                 @PathVariable Long hotelBookingId) {
        // lấy tất cả booking đã có
        BookingDetailsDTO bookingDetailsDTO = bookingRoomDetailsService.getBookingDetails(authentication, hotelBookingId);
        model.addAttribute("bookingDetailsDTO", bookingDetailsDTO);
        return "customer/bookingDetail";
    }


    @PostMapping("/customer/cancelBooking")
    public ResponseEntity<?> cancelBooking(@RequestBody CancellationFormDTO cancellationForm, Authentication authentication) {
        try {
            hotelBookingService.cancelBooking(cancellationForm, authentication);
            hotelBookingService.changeStatusId(cancellationForm.getHotelBookingId());
            return ResponseEntity.ok("Gửi yêu cầu thành công");
        } catch (CancellationExistException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Bạn đã gửi rồi.");

        }

    }


    @GetMapping("/customer/cancelBooking/{hotelBookingId}")
    public String cancelBooking(Model model, Authentication authentication,
                                @PathVariable Long hotelBookingId, HttpSession session, RedirectAttributes redirectAttributes) {
        BookingDetailsDTO bookingDetailsDTO = bookingRoomDetailsService.getBookingDetails(authentication, hotelBookingId);
        List<CustomerCancellationReasons> customerCancellationReasons = customerCancellationReasonService.findAllCancellationReasons();
        List<BankList> bankLists = bankListService.getAllBank();
        model.addAttribute("bookingDetailsDTO", bookingDetailsDTO);
        model.addAttribute("customerCancellationReasons", customerCancellationReasons);
        model.addAttribute("bankLists", bankLists);
        model.addAttribute("hotelBookingId", hotelBookingId);


        session.setAttribute("bookingDetailsDTO", bookingDetailsDTO);
        return "customer/cancelBooking";
    }

    @PostMapping("/search-room")
    public ResponseEntity<String> searchRoomForBooking(@RequestBody @Valid SearchingRoomDTO searchingRoomDTO) {
        List<SearchingResultRoomDTO> searchingRoomForBooking = this.roomService.getSearchingRoomForBooking(searchingRoomDTO.getCategoryId(), searchingRoomDTO.getCheckIn(), searchingRoomDTO.getCheckOut());
        Gson gson = new Gson();
        String json = gson.toJson(searchingRoomForBooking);
        return ResponseEntity.ok(json);
    }
}
