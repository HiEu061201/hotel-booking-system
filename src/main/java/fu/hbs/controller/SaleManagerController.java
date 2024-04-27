package fu.hbs.controller;

import fu.hbs.dto.CancellationDTO.ConfirmRefundDTO;
import fu.hbs.dto.CancellationDTO.ViewCancellationDTO;
import fu.hbs.dto.HotelBookingDTO.BookingDetailsDTO;
import fu.hbs.dto.ReceptionistDTO.ViewBookingDTO;
import fu.hbs.dto.SaleManagerController.*;
import fu.hbs.dto.ServiceDTO.ViewServiceDTO;
import fu.hbs.entities.CustomerCancellation;
import fu.hbs.entities.New;
import fu.hbs.repository.ContactRepository;
import fu.hbs.service.interfaces.*;

import fu.hbs.utils.StringDealer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class SaleManagerController {
    @Autowired
    CustomerCancellationService customerCancellationService;
    @Autowired
    BookingRoomDetailsService bookingRoomDetailsService;
    @Autowired
    private ReceptionistBookingService bookingService;
    @Autowired
    private HotelBookingService hotelBookingService;
    @Autowired
    private RoomService roomService;
    @Autowired
    private NewsService newsService;
    @Autowired
    private ContactService contactService;
    @Autowired
    private FeedbackService feedbackService;
    @Autowired
    private ContactRepository contactRepository;

    private StringDealer stringDealer = new StringDealer();

    @GetMapping("/saleManager/listRefund")
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
                model.addAttribute("error1", "Ngày tháng không đúng định dạng");
                return "salemanager/listRefund";
            }
        }

        Page<ViewCancellationDTO> viewCancellationDTOPage = null;
        int defaultPage = (page != null && page >= 0) ? page : 0;
        int defaultSize = (size != null && size > 0) ? size : 10;

        if (status == null && checkIn == null) {
            viewCancellationDTOPage = customerCancellationService.getAllByStatusProcess(null, 0, defaultPage, defaultSize);
        } else if (status == -1 && checkIn == null) {
            viewCancellationDTOPage = customerCancellationService.getAllByStatusProcess(null, null, defaultPage, defaultSize);
        } else {
            viewCancellationDTOPage = customerCancellationService.getAllByStatusProcess(checkIn, status, defaultPage, defaultSize);
        }


        List<ViewCancellationDTO> viewCancellationDTOList = viewCancellationDTOPage.getContent();

        model.addAttribute("currentPage", viewCancellationDTOPage.getNumber());
        model.addAttribute("pageSize", viewCancellationDTOPage.getSize());
        model.addAttribute("totalPages", viewCancellationDTOPage.getTotalPages());

        model.addAttribute("checkInDate", checkIn);
        model.addAttribute("status", status);

        if (viewCancellationDTOList.isEmpty()) {
            model.addAttribute("error", "Không tìm thấy");
            model.addAttribute("activePage", "/saleManager/listRefund");
            return "salemanager/listRefund";
        }

        model.addAttribute("viewCancellationDTOList", viewCancellationDTOList);
        model.addAttribute("activePage", "/saleManager/listRefund");
        return "salemanager/listRefund";
    }

    @GetMapping("/saleManager/ConfirmRefund/hotelBookingId={hotelBookingId}")
    public String confirmRefund(@PathVariable Long hotelBookingId,
                                Model model) {

        BookingDetailsDTO bookingDetailsDTO = bookingRoomDetailsService.getBookingDetailsByHotelBooking(hotelBookingId);
        model.addAttribute("activePage", "/saleManager/listRefund");
        model.addAttribute("bookingDetailsDTO", bookingDetailsDTO);
        return "salemanager/confirmRefund";
    }

    @PostMapping("/saleManager/ConfirmRefund")
    public ResponseEntity<?> confirmRefund(@RequestBody ConfirmRefundDTO data) {
        try {
            Long hotelBookingId = data.getHotelBookingId();
            Long userId = data.getUserId();
            Long cancellationId = data.getCancellationId();
            BigDecimal price = data.getRefundPrice();
            CustomerCancellation customerCancellation = customerCancellationService.findCustomerCancellationNew(hotelBookingId);
            if (customerCancellation != null) {
                customerCancellation.setCancelTime(new Date());
                customerCancellation.setStatus(1);
                customerCancellation.setRefundAmount(price);
                customerCancellationService.saveCustomerCancellation(customerCancellation);
            }
            String successMessage = "Xác nhận thành công";
            return ResponseEntity.ok().body(Map.of("message", successMessage));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("exceptionMessage", e.getMessage()));
        }

    }

    @GetMapping("/saleManager/listHistoryBooking")
    public String listHistoryBooking(Model model, @RequestParam(defaultValue = "0") Integer page,
                                     @RequestParam(defaultValue = "10") Integer size,
                                     @RequestParam(value = "checkIn", required = false) String checkin,
                                     @RequestParam(value = "checkOut", required = false) String checkout,
                                     @RequestParam(value = "status", required = false) Integer status
    ) {
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
            return "salemanager/saleViewBookingHistory";
        }

        int defaultPage = (page != null && page >= 0) ? page : 0;
        int defaultSize = (size != null && size > 0) ? size : 10;

        Page<ViewBookingDTO> viewHotelBookingDTOS = hotelBookingService.searchCheckInAndCheckOutAndStatus(checkIn, checkOut, status, defaultPage, defaultSize);
        List<ViewBookingDTO> viewBookingDTOList = viewHotelBookingDTOS.getContent();

        model.addAttribute("currentPage", viewHotelBookingDTOS.getNumber());
        model.addAttribute("pageSize", viewHotelBookingDTOS.getSize());
        model.addAttribute("totalPages", viewHotelBookingDTOS.getTotalPages());

        model.addAttribute("checkInDate", checkIn);
        model.addAttribute("checkOutDate", checkOut);
        model.addAttribute("status", status);


        if (viewBookingDTOList.isEmpty()) {
            model.addAttribute("error", "Không tìm thấy");
            model.addAttribute("activePage", "/saleManager/listHistoryBooking");
            return "salemanager/saleViewBookingHistory";
        }


        model.addAttribute("bookings", viewBookingDTOList);
        model.addAttribute("activePage", "/saleManager/listHistoryBooking");


        return "salemanager/saleViewBookingHistory";

    }

    @GetMapping("/saleManager/bookingDetails/{hotelBookingId}")
    public String bookingDetails(Model model, Authentication authentication,
                                 @PathVariable Long hotelBookingId) {
        // lấy tất cả booking đã có
        BookingDetailsDTO bookingDetailsDTO = bookingRoomDetailsService.getBookingDetails(authentication, hotelBookingId);
        model.addAttribute("bookingDetailsDTO", bookingDetailsDTO);
        return "salemanager/saleViewBookingDetail";
    }

    @GetMapping("/saleManager/viewService")
    public String viewAllService(@RequestParam(name = "name", required = false) String name,
                                 @RequestParam(name = "status", required = false) Integer status,
                                 @RequestParam(defaultValue = "0") Integer page,
                                 @RequestParam(defaultValue = "10") Integer size,
                                 Model model) {

        int defaultPage = (page != null && page >= 0) ? page : 0;
        int defaultSize = (size != null && size > 0) ? size : 10;

        Page<ViewServiceDTO> servicePage = roomService.findByNameAndStatus(name, status, defaultPage, defaultSize);
        List<ViewServiceDTO> viewServiceDTOList = servicePage.getContent();

        model.addAttribute("currentPage", servicePage.getNumber());
        model.addAttribute("pageSize", servicePage.getSize());
        model.addAttribute("totalPages", servicePage.getTotalPages());

        model.addAttribute("name", name);
        model.addAttribute("status", status);


        if (viewServiceDTOList.isEmpty()) {
            model.addAttribute("error", "Không tìm thấy");
            model.addAttribute("activePage", "/saleManager/viewService");
            return "salemanager/viewListService";
        }

        model.addAttribute("viewServiceDTOList", viewServiceDTOList);
        model.addAttribute("activePage", "/saleManager/viewService");
        return "salemanager/viewListService";
    }

    @GetMapping("/saleManager/viewListNews")
    public String viewListNews(@RequestParam(name = "name", required = false) String name,
                               @RequestParam(name = "status", required = false) Integer status,
                               @RequestParam(defaultValue = "0") Integer page,
                               @RequestParam(defaultValue = "10") Integer size,
                               Model model) {
        int defaultPage = (page != null && page >= 0) ? page : 0;
        int defaultSize = (size != null && size > 0) ? size : 10;

        Page<ViewNewsDTO> viewNewsDTOS = newsService.searchByNameAndStatus(name, status, defaultPage, defaultSize);
        List<ViewNewsDTO> viewNewsDTOList = viewNewsDTOS.getContent();

        model.addAttribute("currentPage", viewNewsDTOS.getNumber());
        model.addAttribute("pageSize", viewNewsDTOS.getSize());
        model.addAttribute("totalPages", viewNewsDTOS.getTotalPages());

        model.addAttribute("name", name);
        model.addAttribute("status", status);

        if (viewNewsDTOList.isEmpty()) {
            model.addAttribute("error", "Không tìm thấy");
            model.addAttribute("activePage", "/saleManager/viewListNews");
            return "salemanager/listNewManager";
        }

        model.addAttribute("allNews", viewNewsDTOList);
        model.addAttribute("activePage", "/saleManager/viewListNews");
        return "salemanager/listNewManager";
    }

    @GetMapping("/saleManager/getNewsDetails/{newsId}")
    @ResponseBody
    public New getNewsDetails(@PathVariable Long newsId) {
        // Sử dụng service để lấy chi tiết tin tức
        return newsService.getNewsById(newsId);
    }

    @PostMapping("/saleManager/updateRoomService")
    public ResponseEntity<?> updateRoomService(
            @RequestParam(value = "serviceImage", required = false) MultipartFile image,
            @RequestParam("serviceId") String serviceId,
            @RequestParam("serviceName") String serviceName,
            @RequestParam("servicePrice") String servicePrice,
            @RequestParam("status") String status,
            @RequestParam("serviceDes") String serviceDescription, Model model) throws IOException {

        try {
            UpdateServiceDTO updateServiceDTO = new UpdateServiceDTO();
            if (image == null || image.isEmpty()) {
                fu.hbs.entities.RoomService roomService1 = roomService.findByServiceId(Long.valueOf(serviceId));
                System.out.println(roomService1);
                updateServiceDTO.setServiceImage(roomService1.getServiceImage());
            } else {
                String uploadDirectory = "src/main/resources/static/assets/img/services/";
                stringDealer.uploadFile(image, uploadDirectory);
                updateServiceDTO.setServiceImage(image.getOriginalFilename());
            }


            updateServiceDTO.setServiceId(Long.valueOf(serviceId));
            updateServiceDTO.setServiceDes(serviceDescription);
            updateServiceDTO.setServiceName(serviceName);
            Double price = Double.valueOf(servicePrice);
            updateServiceDTO.setStatus(Boolean.valueOf(status));
            updateServiceDTO.setServicePrice(BigDecimal.valueOf(price));

            roomService.updateRoomService(updateServiceDTO);

            String successMessage = "Cập nhật dịch vụ thành công";
            return ResponseEntity.ok().body(Map.of("message", successMessage));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("exceptionMessage", e.getMessage()));
        }
    }

    @PostMapping("/saleManager/createService")
    public ResponseEntity<?> createService(@RequestParam("serviceImage") MultipartFile image,
                                           @RequestParam("serviceName") String serviceName,
                                           @RequestParam("servicePrice") String servicePrice,
                                           @RequestParam("status") String status,
                                           @RequestParam("serviceDes") String serviceDescription, Model model) {
        try {
            String uploadDirectory = "src/main/resources/static/assets/img/services/";
            UpdateServiceDTO dto = new UpdateServiceDTO();
            dto.setStatus(Boolean.valueOf(status));
            Double price = Double.valueOf(servicePrice);
            dto.setServicePrice(BigDecimal.valueOf(price));
            dto.setServiceDes(serviceDescription);
            dto.setServiceImage(image.getOriginalFilename());
            dto.setServiceName(serviceName);
            roomService.createRoomService(dto);
            stringDealer.uploadFile(image, uploadDirectory);

            String successMessage = "Thêm dịch vụ thành công";
            return ResponseEntity.ok().body(Map.of("message", successMessage));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("exceptionMessage", e.getMessage()));
        }

    }

    @PostMapping("/saleManager/createNews")
    public ResponseEntity<?> createNews(@RequestParam("image") MultipartFile image, @RequestParam("date") String dateStr,
                                        @RequestParam("newsTitle") String title,
                                        @RequestParam("newsDescription") String descriptions,
                                        @RequestParam("status") String status,
                                        @RequestParam("userId") Long userId,
                                        Model model) {
        try {
            String uploadDirectory = "src/main/resources/static/assets/img/news/";


            UpdateNewsDTO dto = new UpdateNewsDTO();
            System.out.println("Received status: " + status);
            //dto.setStatus(Boolean.valueOf(status));
            dto.setStatus(Boolean.parseBoolean(status));
            System.out.println("DTO status: " + dto.getStatus());
            //dto.setUserId(userId);
            dto.setUserId(userId);
            dto.setDescriptions(descriptions);
            dto.setImage(image.getOriginalFilename());
            dto.setTitle(title);
            // Chuyển đổi chuỗi thành java.sql.Date
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/mm/yyyy");
            java.util.Date utilDate = dateFormat.parse(dateStr);
            java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());
            dto.setDate(sqlDate);
            // dto.setDate((java.sql.Date) date);
            newsService.createNews(dto);
            stringDealer.uploadFile(image, uploadDirectory);
            return ResponseEntity.ok("Thêm thành công.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi khi thêm.");
        }

    }

    @PostMapping("/saleManager/updateNews/{newId}")
    public ResponseEntity<Map<String, Object>> updateNews(
            @RequestParam(value = "image", required = false) MultipartFile image,
            @RequestParam("newsTitle") String title,
            @PathVariable("newId") String newId,
            @RequestParam("newsDescription") String descriptions,
            @RequestParam("status") String status,
            Model model) throws IOException {
        Map<String, Object> resposne = new HashMap<String, Object>();
        UpdateNewsDTO updateNewsDTO = new UpdateNewsDTO();
        if (image == null || image.isEmpty()) {
            fu.hbs.entities.New news = newsService.findByNewsId(Long.valueOf(newId));
            System.out.println(news);
            updateNewsDTO.setImage(news.getImage());
        } else {
            String uploadDirectory = "src/main/resources/static/assets/img/news/";
            stringDealer.uploadFile(image, uploadDirectory);
            updateNewsDTO.setImage(image.getOriginalFilename());
        }


        updateNewsDTO.setNewId(Long.valueOf(newId));
        updateNewsDTO.setTitle(title);
        updateNewsDTO.setDescriptions(descriptions);
        updateNewsDTO.setStatus(Boolean.valueOf(status));
        System.out.println(updateNewsDTO.getStatus());
        newsService.updateNews(updateNewsDTO);

        resposne.put("success", "Cập nhật tin tức thành công");
        resposne.put("targetPage", "/saleManager/viewListNews");
        return ResponseEntity.ok(resposne);
    }

    @GetMapping("/saleManager/viewListContact")
    public String viewListContact(@RequestParam(name = "name", required = false) String name,
                                  @RequestParam(name = "status", required = false) Integer status,
                                  @RequestParam(defaultValue = "0") Integer page,
                                  @RequestParam(defaultValue = "10") Integer size,
                                  Model model) {

        int defaultPage = (page != null && page >= 0) ? page : 0;
        int defaultSize = (size != null && size > 0) ? size : 10;

        Page<ViewContactDTO> viewContactDTOSPage = contactService.findAllByTitleAndStatus(name, status, defaultPage, defaultSize);
        List<ViewContactDTO> viewContactDTOList = viewContactDTOSPage.getContent();

        model.addAttribute("currentPage", viewContactDTOSPage.getNumber());
        model.addAttribute("pageSize", viewContactDTOSPage.getSize());
        model.addAttribute("totalPages", viewContactDTOSPage.getTotalPages());

        model.addAttribute("name", name);
        model.addAttribute("status", status);

        if (viewContactDTOList.isEmpty()) {
            model.addAttribute("error", "Không tìm thấy");
            model.addAttribute("activePage", "/saleManager/viewListContact");
            return "salemanager/listContactManager";
        }

        model.addAttribute("contacts", viewContactDTOList);
        model.addAttribute("activePage", "/saleManager/viewListContact");

        return "salemanager/listContactManager";
    }

    @GetMapping("/saleManager/listFeedback")
    public String viewListFeedback(@RequestParam(name = "name", required = false) String name,
                                   @RequestParam(name = "status", required = false) Integer status,
                                   @RequestParam(defaultValue = "0") Integer page,
                                   @RequestParam(defaultValue = "10") Integer size,
                                   Model model) {

        int defaultPage = (page != null && page >= 0) ? page : 0;
        int defaultSize = (size != null && size > 0) ? size : 10;

        Page<ViewFeedBackDTO> viewFeedBackDTOS = feedbackService.findAllByTitleAndStatus(name, status, defaultPage, defaultSize);
        List<ViewFeedBackDTO> viewFeedBackDTOList = viewFeedBackDTOS.getContent();

        model.addAttribute("currentPage", viewFeedBackDTOS.getNumber());
        model.addAttribute("pageSize", viewFeedBackDTOS.getSize());
        model.addAttribute("totalPages", viewFeedBackDTOS.getTotalPages());

        model.addAttribute("name", name);
        model.addAttribute("status", status);

        if (viewFeedBackDTOList.isEmpty()) {
            model.addAttribute("error", "Không tìm thấy");
            model.addAttribute("activePage", "/saleManager/listFeedback");
            return "salemanager/listCustomerFeedback";
        }

        model.addAttribute("viewFeedBackDTOList", viewFeedBackDTOList);
        model.addAttribute("activePage", "/saleManager/listFeedback");

        return "salemanager/listCustomerFeedback";
    }

    @PostMapping("/updateContact")
    public ResponseEntity<String> updateContact(@RequestParam("contactId") Long contactId) {
        System.out.println(contactId + "contact");
        // Thay đổi trạng thái đặt phòng thành "Đã Check In"
        // ...
        //long newStatusId = 2L; // Đã Check In
        // Cập nhật trạng thái đặt phòng trong dữ liệu
        // Ví dụ: Sử dụng một service để cập nhật trạng thái đặt phòng
        contactRepository.updateStatus(true, contactId);

        return new ResponseEntity<>("Cập nhật thành công", HttpStatus.OK);
    }

}