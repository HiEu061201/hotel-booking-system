package fu.hbs.controller;

import java.time.format.DateTimeParseException;
import java.util.List;

import fu.hbs.dto.ReceptionistDTO.ViewBookingDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import fu.hbs.validator.BookingValidator;
import fu.hbs.dto.User.CancellationFormDTO;
import fu.hbs.dto.HotelBookingDTO.BookingDetailsDTO;
import fu.hbs.entities.BankList;
import fu.hbs.entities.CustomerCancellationReasons;
import fu.hbs.entities.HotelBooking;
import fu.hbs.exceptionHandler.CancellationExistException;
import fu.hbs.service.interfaces.BankListService;
import fu.hbs.service.interfaces.BookingRoomDetailsService;
import fu.hbs.service.interfaces.CategoryRoomPriceService;
import fu.hbs.service.interfaces.CustomerCancellationReasonService;
import fu.hbs.service.interfaces.HotelBookingService;
import fu.hbs.service.interfaces.ReceptionistBookingService;
import fu.hbs.service.interfaces.RoomCategoryService;
import fu.hbs.service.interfaces.ServiceService;
import fu.hbs.service.interfaces.TransactionsService;
import jakarta.servlet.http.HttpSession;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Date;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import fu.hbs.constant.TransactionMessage;
import fu.hbs.dto.HotelBookingDTO.*;
import fu.hbs.dto.RoomCategoryDTO.ViewRoomCategoryDTO;
import fu.hbs.dto.RoomServiceDTO.RoomBookingServiceDTO;
import fu.hbs.entities.*;
import fu.hbs.entities.RoomService;
import fu.hbs.exceptionHandler.ResetExceptionHandler;
import fu.hbs.service.interfaces.*;
import fu.hbs.service.impl.VNPayService;
import fu.hbs.utils.EmailUtil;
import fu.hbs.utils.StringDealer;
import jakarta.servlet.http.HttpServletRequest;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Controller
public class ReceptionistBookingController {
    @Autowired
    private ReceptionistBookingService bookingService;
    @Autowired
    BookingRoomDetailsService bookingRoomDetailsService;
    @Autowired
    private HotelBookingService hotelBookingService;
    @Autowired
    private ServiceService roomServiceService;
    @Autowired
    private PaymentTypeService paymentTypeService;
    @Autowired
    private VNPayService vnPayService;
    @Autowired
    private RoomCategoryService roomCategoryService;
    @Autowired
    private HotelBookingServiceService hotelBookingServiceService;
    @Autowired
    private CategoryRoomPriceService categoryRoomPriceService;
    @Autowired
    private TransactionsService transactionsService;
    @Autowired
    private BankListService bankListService;
    @Autowired
    private CustomerCancellationReasonService customerCancellationReasonService;
    private final EmailUtil emailUtil;
    @Autowired
    private TemplateEngine templateEngine;

    @Autowired
    private ReceptionistBookingService receptionistBookingService;


    public ReceptionistBookingController(EmailUtil emailUtil) {
        this.emailUtil = emailUtil;
    }

    @GetMapping("/create")
    public String createBooking(Model model) {
        model.addAttribute("booking", new HotelBooking());
        return "booking/create";
    }

    @PostMapping("/receptionist-save-booking")
    public String saveBooking(@ModelAttribute("booking") CreateHotelBookingDTO bookingRequest, BindingResult result,
                              HttpSession session, Model model) {
        Long hotelBookingId = bookingService.createHotelBookingByReceptionist(bookingRequest);
        if (bookingRequest.getPaymentTypeId() == 1L) {
            HotelBooking hotelBooking = bookingService.findById(hotelBookingId);
            if (hotelBooking == null) {
                return "error";
            }
            //    BigDecimal totalPriceOfHotelBooking = bookingService.getTotalPriceOfHotelBooking(hotelBookingId);
            session.setAttribute("userEmail", hotelBooking.getEmail().trim());
            //session.setAttribute("orderTotal", totalPriceOfHotelBooking.multiply(BigDecimal.valueOf(0.5)).setScale(0, RoundingMode.HALF_DOWN));
            session.setAttribute("orderTotal", hotelBooking.getDepositPrice());
            session.setAttribute("orderInfo",
                    "Thanh toán tiền cọc phòng cho đơn đặt:" + hotelBooking.getHotelBookingId());
            return "redirect:/payment";
        }
        String message = URLEncoder.encode("Đặt phòng thành công", StandardCharsets.UTF_8);
        return "redirect:/receptionist/listBookingReceptionist?status=1&message=" + message;
    }

//    @PostMapping("/handle-payment")
//    public String saveCheckoutPaymentReceptionist(@ModelAttribute("checkoutDTO") ViewCheckoutDTO checkoutDTO,
//                                                  Model model) {
//        // Retrieve booking details based on the bookingId
//        // Replace the following line with your actual logic to fetch booking details
//        HotelBooking hotelBooking = bookingService.findById(checkoutDTO.getHotelBookingId());
//        List<BookingRoomDetails> bookingDetailsList =
//                bookingRoomDetailsService.getBookingDetailsByHotelBookingId(hotelBooking.getHotelBookingId());
//
//        // Add booking details to the model
//        model.addAttribute("hotelBooking", hotelBooking);
//        model.addAttribute("bookingDetailsList", bookingDetailsList);
//        // Return the view name for checkout page
//        return "customer/orderCustomer";
//    }

    @PostMapping("/save")
    public String saveBooking(@ModelAttribute("booking") HotelBooking booking) {
        bookingService.save(booking);
        return "redirect:/listBookingReceptionist";
    }

    @GetMapping("/checkout-receptionist")
    public String checkoutReceptionist(@RequestParam("bookingId") Long bookingId, Model model) {
        // Retrieve booking details based on the bookingId
        // Replace the following line with your actual logic to fetch booking details
        HotelBooking hotelBooking = bookingService.findById(bookingId);

        // Add booking details to the model
        model.addAttribute("hotelBooking", hotelBooking);

        // Return the view name for checkout page
        return "receptionist/checkoutReceptionist";
    }

    @GetMapping("/receptionist/listBookingReceptionist")
    public String listBooking(Model model,
                              @RequestParam(defaultValue = "0") Integer page,
                              @RequestParam(defaultValue = "10") Integer size,
                              @RequestParam(value = "checkIn", required = false) String checkin,
                              @RequestParam(value = "checkOut", required = false) String checkout,
                              @RequestParam(required = false) String message,
                              @RequestParam(required = false) Integer status) {
        LocalDate checkIn = null;
        LocalDate checkOut = null;
        try {

            String expectedDateFormat = "yyyy-MM-dd";
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(expectedDateFormat);
            if (checkin != null && !checkin.isEmpty()) {
                checkIn = LocalDate.parse(checkin, formatter);
            }

            if (checkout != null && !checkout.isEmpty()) {
                checkOut = LocalDate.parse(checkout, formatter);
            }
        } catch (DateTimeParseException e) {
            model.addAttribute("error1", "Ngày tháng năm không đúng định dạng (dd-MM-yy)");
            model.addAttribute("activePage", "/receptionist/listBookingReceptionist");
            return "receptionist/viewBookingReceptionist";
        }


        int defaultPage = (page != null && page >= 0) ? page : 0;
        int defaultSize = (size != null && size > 0) ? size : 10;

        Page<ViewBookingDTO> viewHotelBookingDTOS = receptionistBookingService.searchCheckInAndCheckOut(checkIn, checkOut, 1, defaultPage, defaultSize);
        List<ViewBookingDTO> viewBookingDTOList = viewHotelBookingDTOS.getContent();


        model.addAttribute("currentPage", viewHotelBookingDTOS.getNumber());
        model.addAttribute("pageSize", viewHotelBookingDTOS.getSize());
        model.addAttribute("totalPages", viewHotelBookingDTOS.getTotalPages());

        model.addAttribute("checkInDate", checkIn);
        model.addAttribute("checkOutDate", checkOut);
        model.addAttribute("message", message);
        model.addAttribute("status", status);


        if (viewBookingDTOList.isEmpty()) {
            model.addAttribute("error", "Không tìm thấy");
            model.addAttribute("activePage", "/receptionist/listBookingReceptionist");
            return "receptionist/viewBookingReceptionist";
        }


        model.addAttribute("bookings", viewBookingDTOList);
        model.addAttribute("activePage", "/receptionist/listBookingReceptionist");

        return "receptionist/viewBookingReceptionist";
    }

    @GetMapping("/edit/{id}")
    public String editBooking(@PathVariable("id") Long id, Model model) {
        HotelBooking booking = bookingService.findById(id);
        model.addAttribute("booking", booking);
        return "booking/edit";
    }

    @PostMapping("/update")
    public String updateBooking(@ModelAttribute("booking") HotelBooking booking, Model model) {
        bookingService.save(booking);
        model.addAttribute("activePage", "/receptionist/listBookingReceptionist");
        return "redirect:/receptionist/listBookingReceptionist";
    }


    @GetMapping("/getBookingDetails")
    public ResponseEntity<HotelBooking> getBookingDetails(@RequestParam("bookingId") Long bookingId) {
        HotelBooking booking = bookingService.findById(bookingId);
        return new ResponseEntity<>(booking, HttpStatus.OK);
    }


    @GetMapping("/receptionist/listRoomInUse")
    public String listRoomInUse(Model model, @RequestParam(defaultValue = "0") Integer page,
                                @RequestParam(defaultValue = "10") Integer size,
                                @RequestParam(value = "checkIn", required = false) String checkin,
                                @RequestParam(value = "checkOut", required = false) String checkout,
                                @RequestParam(required = false) String message,
                                @RequestParam(required = false) Integer status) {

        LocalDate checkIn = null;
        LocalDate checkOut = null;
        try {

            String expectedDateFormat = "yyyy-MM-dd";
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(expectedDateFormat);
            if (checkin != null && !checkin.isEmpty()) {
                checkIn = LocalDate.parse(checkin, formatter);
            }

            if (checkout != null && !checkout.isEmpty()) {
                checkOut = LocalDate.parse(checkout, formatter);
            }
        } catch (DateTimeParseException e) {
            model.addAttribute("error1", "Ngày tháng năm không đúng định dạng (dd-MM-yy)");
            model.addAttribute("activePage", "/receptionist/listRoomInUse");
            return "receptionist/listRoomInUseReceptionist";
        }


        int defaultPage = (page != null && page >= 0) ? page : 0;
        int defaultSize = (size != null && size > 0) ? size : 10;

        Page<ViewBookingDTO> viewHotelBookingDTOS = receptionistBookingService.searchCheckInAndCheckOut(checkIn, checkOut, 2, defaultPage, defaultSize);
        List<ViewBookingDTO> viewBookingDTOList = viewHotelBookingDTOS.getContent();

        model.addAttribute("currentPage", viewHotelBookingDTOS.getNumber());
        model.addAttribute("pageSize", viewHotelBookingDTOS.getSize());
        model.addAttribute("totalPages", viewHotelBookingDTOS.getTotalPages());

        model.addAttribute("checkInDate", checkIn);
        model.addAttribute("checkOutDate", checkOut);
        model.addAttribute("message", message);
        model.addAttribute("status", status);

        if (viewBookingDTOList.isEmpty()) {
            model.addAttribute("error", "Không tìm thấy");
            model.addAttribute("activePage", "/receptionist/listRoomInUse");

            return "receptionist/listRoomInUseReceptionist";
        }


        model.addAttribute("bookings", viewBookingDTOList);
        model.addAttribute("activePage", "/receptionist/listRoomInUse");

        return "receptionist/listRoomInUseReceptionist";
    }

    @GetMapping("/receptionist/listHistoryBooking")
    public String listHistoryBooking(Model model, @RequestParam(defaultValue = "0") Integer page,
                                     @RequestParam(defaultValue = "10") Integer size,
                                     @RequestParam(value = "checkIn", required = false) String checkin,
                                     @RequestParam(value = "checkOut", required = false) String checkout,
                                     @RequestParam(name = "status", required = false) Integer status) {

        LocalDate checkIn = null;
        LocalDate checkOut = null;
        try {

            String expectedDateFormat = "yyyy-MM-dd";
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(expectedDateFormat);
            if (checkin != null && !checkin.isEmpty()) {
                checkIn = LocalDate.parse(checkin, formatter);
            }

            if (checkout != null && !checkout.isEmpty()) {
                checkOut = LocalDate.parse(checkout, formatter);
            }
        } catch (DateTimeParseException e) {
            model.addAttribute("error1", "Ngày tháng năm không đúng định dạng (dd-MM-yy)");
            model.addAttribute("activePage", "/receptionist/listHistoryBooking");
            return "receptionist/bookingHistoryReceptionist";
        }


        int defaultPage = (page != null && page >= 0) ? page : 0;
        int defaultSize = (size != null && size > 0) ? size : 10;


        Page<ViewBookingDTO> viewHotelBookingDTOS = receptionistBookingService.searchCheckInAndCheckOutAndStatus(checkIn, checkOut, status, defaultPage, defaultSize);
        List<ViewBookingDTO> viewBookingDTOList = viewHotelBookingDTOS.getContent();

        model.addAttribute("currentPage", viewHotelBookingDTOS.getNumber());
        model.addAttribute("pageSize", viewHotelBookingDTOS.getSize());
        model.addAttribute("totalPages", viewHotelBookingDTOS.getTotalPages());

        model.addAttribute("checkInDate", checkIn);
        model.addAttribute("checkOutDate", checkOut);
        model.addAttribute("status", status);

        if (viewBookingDTOList.isEmpty()) {
            model.addAttribute("error", "Không tìm thấy");
            model.addAttribute("activePage", "/receptionist/listHistoryBooking");
            return "receptionist/bookingHistoryReceptionist";
        }


        model.addAttribute("bookings", viewBookingDTOList);
        model.addAttribute("activePage", "/receptionist/listHistoryBooking");
        return "receptionist/bookingHistoryReceptionist";
    }

    @GetMapping("receptionist/checkOutReceptionist")
    public String checkOutReceptionist(@RequestParam("hotelBookingId") Long hotelBookingId, Model model) {
        HotelBooking hotelBooking = hotelBookingService.findById(hotelBookingId);
        if (hotelBooking != null && hotelBooking.getValidBooking()) {
            if (!BookingValidator.isValidToCheckOut(hotelBooking)) {
                return "error";
            }
            List<BookingRoomDetails> bookingRoomDetails =
                    bookingRoomDetailsService.getBookingDetailsByHotelBookingId(hotelBookingId);
            List<RoomService> roomServices = roomServiceService.getAllServicesByStatus(true);
            Transactions transaction = transactionsService.findFirstTransactionOfHotelBooking(hotelBookingId);
            PaymentType paymentType = paymentTypeService.getPaymentTypeById(transaction.getPaymentId());
            List<fu.hbs.entities.HotelBookingService> usedServices =
                    hotelBookingServiceService.findAllByHotelBookingId(hotelBookingId);

            Map<Long, RoomCategories> roomCategoriesMap =
                    this.roomCategoryService.getAllRoomCategories().stream().collect(Collectors.toMap(RoomCategories::getRoomCategoryId, Function.identity()));
            ViewCheckoutDTO viewCheckoutDTO = ViewCheckoutDTO.valueOf(hotelBooking, bookingRoomDetails, paymentType,
                    roomCategoriesMap);
            model.addAttribute("viewCheckoutDTO", viewCheckoutDTO);
            model.addAttribute("roomServices", roomServices);
            model.addAttribute("currentTime", Date.valueOf(LocalDate.now()));
            SaveCheckoutDTO checkoutModel = new SaveCheckoutDTO();
            List<SaveCheckoutHotelServiceDTO> hotelServices = checkoutModel.getHotelServices();
            for (RoomBookingServiceDTO roomBookingServiceDTO : viewCheckoutDTO.getRoomBookingServiceDTOS()) {
                SaveCheckoutHotelServiceDTO saveCheckoutHotelServiceDTO = new SaveCheckoutHotelServiceDTO();
                saveCheckoutHotelServiceDTO.setServiceId(roomBookingServiceDTO.getServiceId());
                saveCheckoutHotelServiceDTO.setQuantity(roomBookingServiceDTO.getQuantity());
                hotelServices.add(saveCheckoutHotelServiceDTO);

            }
            checkoutModel.setHotelBookingId(hotelBookingId);
            checkoutModel.setServicePrice(viewCheckoutDTO.getTotalServicePrice());
            model.addAttribute("saveCheckoutDTO", checkoutModel);
        } else {
            return "error";
        }
        model.addAttribute("activePage", "/receptionist/listRoomInUse");
        return "receptionist/checkOutReceptionist";
    }

    @PostMapping("receptionist/checkOutReceptionist")
    public String saveCheckOutReceptionist(@ModelAttribute("saveCheckoutDTO") SaveCheckoutDTO checkoutDTO,
                                           HttpSession session, Model model) {
        if (checkoutDTO.getServicePrice().compareTo(BigDecimal.valueOf(1000000000)) >= 0) {
            return "error";
        }
        try {
            bookingService.checkout(checkoutDTO);
            if (checkoutDTO.getPaymentTypeId() == 1) {
                HotelBooking hotelBooking = bookingService.findById(checkoutDTO.getHotelBookingId());
                if (hotelBooking.getTotalPrice().compareTo(hotelBooking.getDepositPrice()) <= 0) {
                    hotelBooking.setStatusId(3L);
                    hotelBookingService.saveHotelBooking(hotelBooking);

                    return "redirect:/receptionist/listRoomInUse";
                }
                if (hotelBooking == null) {
                    return "error";
                }
                BigDecimal totalPrice =
                        hotelBooking.getTotalPrice().subtract(hotelBooking.getDepositPrice()).setScale(0,
                                RoundingMode.UP);
                session.setAttribute("userEmail", hotelBooking.getEmail().trim());
                session.setAttribute("orderTotal", totalPrice);
                session.setAttribute("orderInfo",
                        "Thanh toán tiền phòng cho đơn đặt phòng:" + hotelBooking.getHotelBookingId());
                return "redirect:/payment";
            }
        } catch (Exception e) {
            return "error";
        }

        model.addAttribute("activePage", "/receptionist/listRoomInUse");
        return "redirect:/receptionist/listRoomInUse?status=1&message=" + URLEncoder.encode("Check out thành công",
                StandardCharsets.UTF_8);
    }

    @GetMapping("/payment")
    public String payment(HttpSession session, HttpServletRequest request) {
        BigDecimal orderTotal = (BigDecimal) session.getAttribute("orderTotal");
        String orderInfo = (String) session.getAttribute("orderInfo");
        String userEmail = (String) session.getAttribute("userEmail");
        String baseUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
        String vnpayUrl = vnPayService.createOrder(orderTotal, orderInfo, baseUrl, "/receptionist-payment");

        Context context = new Context();
        context.setVariable("orderInfo", orderInfo);
        context.setVariable("orderTotal", orderTotal);
        context.setVariable("vnpayUrl", vnpayUrl);
        String emailContent = templateEngine.process("email/VNPayMail", context);
        emailUtil.sendBookingEmail(userEmail, "Thanh toán VNPay", emailContent);
        return "redirect:/receptionist/listHistoryBooking";
    }


    @GetMapping("/receptionist-payment")
    public String GetMapping(HttpServletRequest request, Model model) throws ResetExceptionHandler {
        int paymentStatus = vnPayService.orderReturn(request);
        Transactions transaction = new Transactions();

        String orderInfo = request.getParameter("vnp_OrderInfo");
        String paymentTime = request.getParameter("vnp_PayDate");
        String transactionId = request.getParameter("vnp_TransactionNo");
        String totalPrice = request.getParameter("vnp_Amount");
        String hotelBookingIdAsString = StringDealer.extractNumberFromString(orderInfo);
        Long hotelBookingId;
        if (!hotelBookingIdAsString.isEmpty()) {
            hotelBookingId = Long.valueOf(hotelBookingIdAsString);
        } else {
            return "error";
        }
        BigDecimal bigDecimalValue = new BigDecimal(totalPrice).divide(BigDecimal.valueOf(100), 2,
                BigDecimal.ROUND_HALF_UP);

        model.addAttribute("orderId", orderInfo);
        model.addAttribute("totalPrice", bigDecimalValue);
        model.addAttribute("paymentTime", paymentTime);
        model.addAttribute("transactionId", transactionId);

        // Define the date format
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

        // Parse the string into a LocalDateTime object
        LocalDateTime parsedDate = LocalDateTime.parse(paymentTime, formatter);
        if (paymentStatus == 1) {
            transaction.setVnpayTransactionId(transactionId);
            transaction.setHotelBookingId(hotelBookingId);
            transaction.setAmount(bigDecimalValue);
            transaction.setCreatedDate(parsedDate);
            transaction.setStatus("Thành công");
            transaction.setPaymentId(1L);
            transaction.setContent(TransactionMessage.PAY.getMessage());
            HotelBooking hotelBooking = hotelBookingService.findById(hotelBookingId);
            hotelBooking.setValidBooking(true);
            if (hotelBooking.getStatusId() == 2) {
                hotelBooking.setStatusId(3L);
                transaction.setTransactionsTypeId(2L);
            } else {
                transaction.setTransactionsTypeId(1L);
            }
            if (hotelBooking.getStatusId() == 1L) {
                hotelBooking.setValidBooking(true);
                hotelBookingService.sendBookingRequest(hotelBookingId);
            }
            transactionsService.saveTransactions(transaction);
            hotelBookingService.saveHotelBooking(hotelBooking);
            return "customer/ordersuccess";
        }
        return "customer/orderfail";
    }

    @GetMapping("receptionist/createRoomReceptionist")
    public String createRoomReceptionist(Model model) {
        List<ViewRoomCategoryDTO> categories = roomCategoryService.getAllRoom();
        CreateHotelBookingDTO attributeValue = new CreateHotelBookingDTO();
        attributeValue.setBookingDetails(new ArrayList<>());
        model.addAttribute("booking", attributeValue);
        model.addAttribute("categories", categories);
        model.addAttribute("searchingModel", new SearchingRoomDTO());
        model.addAttribute("activePage", "/receptionist/createRoomReceptionist");
        return "receptionist/createRoomReceptionist";
    }


    @GetMapping("/receptionist/bookingDetails/{hotelBookingId}")
    public String bookingDetails(Model model, Authentication authentication, @PathVariable Long hotelBookingId) {
        // Retrieve booking details based on the bookingId
        BookingDetailsDTO bookingDetailsDTO = bookingRoomDetailsService.getBookingDetails(authentication, hotelBookingId);

        if (bookingDetailsDTO != null) {
            model.addAttribute("bookingDetailsDTO", bookingDetailsDTO);

            // Now, fetch check-in details using the same hotelBookingId
            HotelBooking hotelBooking = hotelBookingService.findById(hotelBookingId);
            if (hotelBooking != null && hotelBooking.getValidBooking()) {
                List<BookingRoomDetails> bookingRoomDetails = bookingRoomDetailsService.getBookingDetailsByHotelBookingId(hotelBookingId);
                PaymentType paymentType = paymentTypeService.getPaymentTypeById(2L);
                Map<Long, RoomCategories> roomCategoriesMap = this.roomCategoryService
                        .getAllRoomCategories().stream().collect(Collectors.toMap(RoomCategories::getRoomCategoryId, Function.identity()));
                ViewCheckInDTO viewCheckInDto = ViewCheckInDTO.valueOf(hotelBooking, bookingRoomDetails, paymentType, roomCategoriesMap);
                model.addAttribute("viewCheckInDTO", viewCheckInDto);
                model.addAttribute("currentTime", Date.valueOf(LocalDate.now()));
                SaveCheckinDTO saveCheckinDTO = SaveCheckinDTO.valueOf(hotelBooking, bookingRoomDetails);
                model.addAttribute("saveCheckinDTO", saveCheckinDTO);
                model.addAttribute("activePage", "/receptionist/listHistoryBooking");
                return "receptionist/bookingDetailReceptionist";
            } else {
                return "error";
            }
        } else {
            return "error";  // or redirect to an error page
        }
    }


    @GetMapping("/receptionist/cancelBooking/{hotelBookingId}")
    public String cancelBooking(Model model, Authentication authentication, @PathVariable Long hotelBookingId,
                                HttpSession session, RedirectAttributes redirectAttributes) {
        BookingDetailsDTO bookingDetailsDTO = bookingRoomDetailsService.getBookingDetails(authentication,
                hotelBookingId);
        List<CustomerCancellationReasons> customerCancellationReasons = customerCancellationReasonService.findAllCancellationReasons();
        List<BankList> bankLists = bankListService.getAllBank();
        model.addAttribute("bookingDetailsDTO", bookingDetailsDTO);
        model.addAttribute("customerCancellationReasons", customerCancellationReasons);
        model.addAttribute("bankLists", bankLists);
        model.addAttribute("hotelBookingId", hotelBookingId);
        model.addAttribute("activePage", "/receptionist/listHistoryBooking");
        session.setAttribute("bookingDetailsDTO", bookingDetailsDTO);
        return "receptionist/cancel-bookingReceptionist";
    }

    @PostMapping("/receptionist/cancelBooking")
    public ResponseEntity<?> cancelBooking(@RequestBody CancellationFormDTO cancellationForm, Model model,
                                           Authentication authentication) throws CancellationExistException {

        try {
            hotelBookingService.cancelBooking(cancellationForm, authentication);
            hotelBookingService.changeStatusId(cancellationForm.getHotelBookingId());
            return ResponseEntity.ok("Gửi yêu cầu thành công");
        } catch (CancellationExistException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Bạn đã gửi rồi.");

        }

    }

    @PostMapping("receptionist/checkIn")
    public String saveCheckIn(@RequestParam("hotelBookingId") Long hotelBookingId) {
        Boolean result = this.bookingService.checkIn(hotelBookingId);
        // Return to Page you want
        String successMesg = URLEncoder.encode("Check In thành công", StandardCharsets.UTF_8);
        String failMesg = URLEncoder.encode("Check In thất bại", StandardCharsets.UTF_8);
        if (result) {
            return "redirect:/receptionist/checkIn?" + hotelBookingId + "status=-1&" + "message=" + failMesg;
        } else {
            return "redirect:/receptionist/checkIn?" + hotelBookingId + "status=1&" + "message=" + successMesg;
        }
    }

    @GetMapping("receptionist/checkIn")
    public String getCheckIn(@RequestParam("hotelBookingId") Long hotelBookingId, @RequestParam(value = "message",
            required = false) String message, @RequestParam(value = "status",
            required = false) Integer status, Model model) {
        HotelBooking hotelBooking = hotelBookingService.findById(hotelBookingId);
        if (hotelBooking != null && hotelBooking.getValidBooking()) {
            List<BookingRoomDetails> bookingRoomDetails =
                    bookingRoomDetailsService.getBookingDetailsByHotelBookingId(hotelBookingId);
            Transactions transaction = transactionsService.findFirstTransactionOfHotelBooking(hotelBookingId);
            PaymentType paymentType = paymentTypeService.getPaymentTypeById(transaction.getPaymentId());
            Map<Long, RoomCategories> roomCategoriesMap = this.roomCategoryService.
                    getAllRoomCategories().stream().collect(Collectors.toMap(RoomCategories::getRoomCategoryId,
                            Function.identity()));
            ViewCheckInDTO viewCheckInDto = ViewCheckInDTO.valueOf(hotelBooking, bookingRoomDetails, paymentType,
                    roomCategoriesMap);
            model.addAttribute("viewCheckInDTO", viewCheckInDto);
            model.addAttribute("currentTime", Date.valueOf(LocalDate.now()));
            SaveCheckinDTO saveCheckinDTO = SaveCheckinDTO.valueOf(hotelBooking, bookingRoomDetails);
            model.addAttribute("saveCheckinDTO", saveCheckinDTO);
            if (message != null && !message.isEmpty()) {
                String decodedErrorMessage = URLDecoder.decode(message, StandardCharsets.UTF_8);
                model.addAttribute("message", decodedErrorMessage);
                model.addAttribute("status", status);
            }
        } else {
            return "error";
        }
        return "receptionist/checkInRecetionist";
    }

//  private static SaveCheckoutDTO makeSaveCheckoutDTO(Long hotelBookingId, ViewCheckoutDTO viewCheckInDto) {
//  SaveCheckoutDTO checkoutModel = new SaveCheckoutDTO();
//  List<SaveCheckoutHotelServiceDTO> hotelServices = checkoutModel.getHotelServices();
//  for (RoomBookingServiceDTO roomBookingServiceDTO : viewCheckInDto.getRoomBookingServiceDTOS()) {
//      SaveCheckoutHotelServiceDTO saveCheckoutHotelServiceDTO = new SaveCheckoutHotelServiceDTO();
//      saveCheckoutHotelServiceDTO.setServiceId(roomBookingServiceDTO.getServiceId());
//      saveCheckoutHotelServiceDTO.setQuantity(roomBookingServiceDTO.getQuantity());
//      hotelServices.add(saveCheckoutHotelServiceDTO);
//
//  }
//  checkoutModel.setHotelBookingId(hotelBookingId);
//  checkoutModel.setServicePrice(viewCheckInDto.getTotalServicePrice());
//  return checkoutModel;
//}


    @PostMapping("receptionist/new-checkIn")
    public String saveCheckInDetail(@ModelAttribute("checkin") SaveCheckinDTO checkinForm) {
        Boolean result = this.bookingService.checkIn(checkinForm);
        // Return to Page you want
        if (result) {
            String message = "Check-in thành công";
            String encodedMessage = URLEncoder.encode(message, StandardCharsets.UTF_8);
            return "redirect:/receptionist/listBookingReceptionist?status=1&message=" + encodedMessage;
        } else {
            String errorMessage = "Chưa đến thời gian check-in!";
            String encodedErrorMessage = URLEncoder.encode(errorMessage, StandardCharsets.UTF_8);
            return "redirect:/receptionist/checkIn?hotelBookingId=" + checkinForm.getHotelBookingId() + "&status=-1" +
                    "&message=" + encodedErrorMessage;
        }
    }
}
