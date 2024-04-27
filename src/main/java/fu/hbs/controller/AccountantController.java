package fu.hbs.controller;

import fu.hbs.constant.TransactionMessage;
import fu.hbs.dto.AccountManagerDTO.RevenueDTO;
import fu.hbs.dto.CancellationDTO.ConfirmRefundDTO;
import fu.hbs.dto.CancellationDTO.ViewCancellationDTO;
import fu.hbs.dto.HotelBookingDTO.BookingDetailsDTO;
import fu.hbs.dto.VnpayDTO.ViewPaymentDTO;
import fu.hbs.entities.CustomerCancellation;
import fu.hbs.entities.HotelBooking;
import fu.hbs.entities.Transactions;
import fu.hbs.service.interfaces.*;
import fu.hbs.utils.RandomKey;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Controller
public class AccountantController {


    @Autowired
    private CustomerCancellationService customerCancellationService;
    @Autowired
    private BookingRoomDetailsService bookingRoomDetailsService;
    @Autowired
    private TransactionsService transactionsService;
    @Autowired
    private HotelBookingService hotelBookingService;
    @Autowired
    private RoomService Rservice;

    @GetMapping("/accounting/listConfirmRefund")
    public String getAllRefund(@RequestParam(name = "status", required = false) Integer status,
                               @RequestParam(value = "checkIn", required = false) String checkin,
                               @RequestParam(defaultValue = "0") Integer page,
                               @RequestParam(defaultValue = "10") Integer size, Model model) {
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
                return "accountant/listProcessRefund";
            }
        }

        int defaultPage = (page != null && page >= 0) ? page : 0;
        int defaultSize = (size != null && size > 0) ? size : 10;
        Page<ViewCancellationDTO> viewCancellationDTOPage = null;
        if (status == null && checkIn == null) {
            viewCancellationDTOPage = customerCancellationService.getAllByStatusConfirm(null, 1, defaultPage, defaultSize);
        } else if (status == -1 && checkIn == null) {
            viewCancellationDTOPage = customerCancellationService.getAllByStatusConfirm(null, null, defaultPage, defaultSize);
        } else {
            viewCancellationDTOPage = customerCancellationService.getAllByStatusConfirm(checkIn, status, defaultPage, defaultSize);
        }


        List<ViewCancellationDTO> viewCancellationDTOList = viewCancellationDTOPage.getContent();

        model.addAttribute("currentPage", viewCancellationDTOPage.getNumber());
        model.addAttribute("pageSize", viewCancellationDTOPage.getSize());
        model.addAttribute("totalPages", viewCancellationDTOPage.getTotalPages());

        model.addAttribute("checkInDate", checkIn);
        model.addAttribute("status", status);

        if (viewCancellationDTOList.isEmpty()) {
            model.addAttribute("error", "Không tìm thấy");
            model.addAttribute("activePage", "/accounting/listConfirmRefund");
            return "accountant/listProcessRefund";
        }
        model.addAttribute("activePage", "/accounting/listConfirmRefund");
        model.addAttribute("viewCancellationDTOList", viewCancellationDTOList);
        return "accountant/listProcessRefund";
    }

    @GetMapping("/accounting/processRefund/hotelBookingId={hotelBookingId}")
    public String confirmRefund(@PathVariable Long hotelBookingId,
                                Model model) {

        BookingDetailsDTO bookingDetailsDTO = bookingRoomDetailsService.getBookingDetailsByHotelBooking(hotelBookingId);
        model.addAttribute("bookingDetailsDTO", bookingDetailsDTO);
        model.addAttribute("RefundAccount", bookingDetailsDTO.getUserInBookingDetailsDTO());
        model.addAttribute("activePage", "/accounting/listConfirmRefund");
        return "accountant/processRefund";
    }

    @PostMapping("/accounting/processRefund")
    public ResponseEntity<?> confirmRefund(@RequestBody ConfirmRefundDTO data) {
        try {
            Long hotelBookingId = data.getHotelBookingId();
            Long userId = data.getUserId();
            Long cancellationId = data.getCancellationId();
            BigDecimal price = data.getRefundPrice();
            CustomerCancellation customerCancellation = customerCancellationService.findCustomerCancellationNew(hotelBookingId);
            if (customerCancellation != null) {
                customerCancellation.setCancelTime(new Date());
                customerCancellation.setStatus(2);
                customerCancellationService.saveCustomerCancellation(customerCancellation);
            }
            HotelBooking hotelBooking = hotelBookingService.findById(hotelBookingId);
            hotelBooking.setTotalPrice(hotelBooking.getTotalPrice().subtract(price));
            hotelBookingService.saveHotelBooking(hotelBooking);
            Transactions transactions = new Transactions();
            transactions.setVnpayTransactionId(RandomKey.generateRandomKey());
            transactions.setStatus("Thành công");
            transactions.setAmount(price.negate());
            transactions.setCreatedDate(LocalDateTime.now());
            transactions.setHotelBookingId(hotelBookingId);
            transactions.setPaymentId(2L);
            transactions.setTransactionsTypeId(4L);
            transactions.setContent(TransactionMessage.CANCEL.getMessage());
            transactionsService.saveTransactions(transactions);

            String successMessage = "Xác nhận thành công";
            return ResponseEntity.ok().body(Map.of("message", successMessage));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("exceptionMessage", e.getMessage()));
        }
    }

    @GetMapping("/accounting/viewPayment")
    public String viewPayment(
            @RequestParam(value = "checkDate", required = false) String checkin,
            @RequestParam(value = "paymentType", required = false) Integer paymentType,
            @RequestParam(value = "typeTransactionId", required = false) Integer typeTransactionId,
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "10") Integer size,
            Model model,
            HttpSession session
    ) {

        LocalDate checkIn = null;
        if (checkin != null && !checkin.isEmpty()) {
            String expectedDateFormat = "yyyy-MM-dd";
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(expectedDateFormat);
            try {
                checkIn = LocalDate.parse(checkin, formatter);
            } catch (DateTimeParseException e) {
                model.addAttribute("error1", "Ngày tháng không đúng định dạng");
                return "accountant/viewPaymentHistory";
            }
        }

        int defaultPage = (page != null && page >= 0) ? page : 0;
        int defaultSize = (size != null && size > 0) ? size : 10;

        Page<ViewPaymentDTO> transactionsPage;


        transactionsPage = transactionsService.findByCreateDateAndPaymentIdAndTypeId(
                checkIn,
                (paymentType == null) ? null : Long.valueOf(paymentType),
                (typeTransactionId == null) ? null : Long.valueOf(typeTransactionId),
                defaultPage,
                defaultSize
        );


        List<ViewPaymentDTO> vnpayTransactionsList = transactionsPage.getContent();
        // Add pagination information to the model
        model.addAttribute("currentPage", transactionsPage.getNumber());
        model.addAttribute("pageSize", transactionsPage.getSize());
        model.addAttribute("totalPages", transactionsPage.getTotalPages());

        model.addAttribute("checkInDate", checkIn);
        model.addAttribute("paymentType", paymentType);
        model.addAttribute("typeTransactionId", typeTransactionId);

        if (vnpayTransactionsList.isEmpty()) {
            model.addAttribute("error", "Chưa có hoá đơn nào");
            model.addAttribute("activePage", "/accounting/viewPayment");
            return "accountant/viewPaymentHistory";
        }
        model.addAttribute("vnpayTransactionsList", vnpayTransactionsList);
        model.addAttribute("activePage", "/accounting/viewPayment");

        return "accountant/viewPaymentHistory";
    }


    @GetMapping("/accounting/viewRevenue")
    public String viewRevenue(Model model) {
        LocalDate localDate = LocalDate.now();
        RevenueDTO revenueDTO = Rservice.viewRevenue(String.valueOf(localDate.getYear()));
        model.addAttribute("revenueDTO", revenueDTO);
        model.addAttribute("activePage", "/accounting/viewRevenue");
        return "accountant/viewRevenueChart1";
    }

    @PostMapping("/accounting/viewRevenue")
    public String viewRevenue(Model model, @RequestParam(name = "year") Integer year) {
        LocalDate localDate = LocalDate.now();
        RevenueDTO revenueDTO;
        if (year == null) {
            revenueDTO = Rservice.viewRevenue(String.valueOf(localDate.getYear()));
            model.addAttribute("year", year);
        } else {
            revenueDTO = Rservice.viewRevenue(String.valueOf(year));
            model.addAttribute("year", year);
        }

        model.addAttribute("revenueDTO", revenueDTO);
        model.addAttribute("activePage", "/accounting/viewRevenue");
        return "accountant/viewRevenueChart1";
    }


}
