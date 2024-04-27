/*
 * Copyright (C) 2023, FPT University
 * SEP490 - SEP490_G77
 * HBS
 * Hotel Booking System
 *
 * Record of change:
 * DATE          Version    Author           DESCRIPTION
 * 08/11/2023    1.0        HieuLBM          First Deploy
 *
 */

package fu.hbs.service.impl;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import fu.hbs.dto.CategoryRoomPriceDTO.DateInfoCategoryRoomPriceDTO;
import fu.hbs.dto.HotelBookingDTO.BookingDetailsDTO;
import fu.hbs.dto.HotelBookingDTO.CreateBookingDTO;
import fu.hbs.dto.HotelBookingDTO.GuestBookingDTO;
import fu.hbs.dto.HotelBookingDTO.ViewHotelBookingDTO;
import fu.hbs.dto.ReceptionistDTO.ViewBookingDTO;
import fu.hbs.dto.User.CancellationFormDTO;
import fu.hbs.dto.User.HotelBookingAvailable;
import fu.hbs.entities.BookingRoomDetails;
import fu.hbs.entities.CategoryRoomFurniture;
import fu.hbs.entities.CategoryRoomPrice;
import fu.hbs.entities.CustomerCancellation;
import fu.hbs.entities.Feedback;
import fu.hbs.entities.HotelBooking;
import fu.hbs.entities.HotelBookingStatus;
import fu.hbs.entities.RefundAccount;
import fu.hbs.entities.Room;
import fu.hbs.entities.RoomCategories;
import fu.hbs.entities.RoomFurniture;
import fu.hbs.entities.RoomImage;
import fu.hbs.entities.RoomStatusHistory;
import fu.hbs.entities.Transactions;
import fu.hbs.entities.User;
import fu.hbs.entities.UserRole;
import fu.hbs.exceptionHandler.CancellationExistException;
import fu.hbs.exceptionHandler.CreateBooingExceptionHandler;
import fu.hbs.exceptionHandler.MailExceptionHandler;
import fu.hbs.exceptionHandler.ResetExceptionHandler;
import fu.hbs.exceptionHandler.RoomCategoryNamesNullException;
import fu.hbs.exceptionHandler.SearchRoomCustomerExceptionHandler;
import fu.hbs.repository.BookingRoomDetailsRepository;
import fu.hbs.repository.CategoryRoomFurnitureRepository;
import fu.hbs.repository.CategoryRoomPriceRepository;
import fu.hbs.repository.CustomerCancellationReasonRepository;
import fu.hbs.repository.CustomerCancellationRepository;
import fu.hbs.repository.FeedbackRepository;
import fu.hbs.repository.HotelBookingRepository;
import fu.hbs.repository.HotelBookingStatusRepository;
import fu.hbs.repository.RefundAccountRepository;
import fu.hbs.repository.RoomCategoriesRepository;
import fu.hbs.repository.RoomFurnitureRepository;
import fu.hbs.repository.RoomImageRepository;
import fu.hbs.repository.RoomRepository;
import fu.hbs.repository.RoomStatusRepository;
import fu.hbs.repository.TransactionsRepository;
import fu.hbs.repository.UserRepository;
import fu.hbs.repository.UserRoleRepository;
import fu.hbs.service.interfaces.BookingRoomDetailsService;
import fu.hbs.service.interfaces.HotelBookingService;
import fu.hbs.utils.BookingDayUtils;
import fu.hbs.utils.EmailUtil;
import fu.hbs.utils.StringDealer;
import jakarta.servlet.http.HttpSession;

@Service
public class HotelBookingServiceImpl implements HotelBookingService {

    @Autowired
    private HotelBookingRepository hotelBookingRepository;
    @Autowired
    private RoomRepository roomRepository;
    @Autowired
    private RoomCategoriesRepository roomCategoriesRepository;
    @Autowired
    private CategoryRoomFurnitureRepository categoryRoomFurnitureRepository;
    @Autowired
    private RoomFurnitureRepository roomFurnitureRepository;
    @Autowired
    private CategoryRoomPriceRepository categoryRoomPriceRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoomStatusRepository roomStatusRepository;
    @Autowired
    private CustomerCancellationReasonRepository customerCancellationReasonRepository;
    @Autowired
    private CustomerCancellationRepository customerCancellationRepository;
    @Autowired
    private RefundAccountRepository refundAccountRepository;
    @Autowired
    private BookingRoomDetailsRepository bookingRoomDetailsRepository;
    @Autowired
    private BookingRoomDetailsService bookingRoomDetailsService;
    @Autowired
    private HotelBookingStatusRepository hotelBookingStatusRepository;
    @Autowired
    private TransactionsRepository transactionsRepository;
    @Autowired
    private FeedbackRepository feedbackRepository;
    @Autowired
    private UserRoleRepository userRoleRepository;
    @Autowired
    private TemplateEngine templateEngine;
    @Autowired
    private RoomImageRepository roomImageRepository;

    private StringDealer stringDealer;
    @Autowired
    private BookingDayUtils bookingDayUtils;

    private final EmailUtil emailUtil;

    public HotelBookingServiceImpl(RoomRepository roomRepository, RoomCategoriesRepository roomCategoriesRepository,
                                   CategoryRoomFurnitureRepository categoryRoomFurnitureRepository,
                                   RoomFurnitureRepository roomFurnitureRepository, CategoryRoomPriceRepository categoryRoomPriceRepository,
                                   BookingDayUtils bookingDayUtils, EmailUtil emailUtil) {
        this.roomRepository = roomRepository;
        this.roomCategoriesRepository = roomCategoriesRepository;
        this.categoryRoomFurnitureRepository = categoryRoomFurnitureRepository;
        this.roomFurnitureRepository = roomFurnitureRepository;
        this.categoryRoomPriceRepository = categoryRoomPriceRepository;
        this.emailUtil = emailUtil;
        this.stringDealer = new StringDealer();
        this.bookingDayUtils = bookingDayUtils;
    }

    @Override
    public HotelBooking saveHotelBooking(HotelBooking hotelBooking) {
        return hotelBookingRepository.save(hotelBooking);
    }

    /**
     * Find available hotel bookings based on check-in, check-out, and number of
     * persons.
     *
     * @param checkIn      The check-in date.
     * @param checkOut     The check-out date.
     * @param numberPerson The number of persons.
     * @return An instance of HotelBookingAvailable with available rooms and related
     * information.
     */
    public HotelBookingAvailable findBookingsByDates(LocalDate checkIn, LocalDate checkOut, int numberPerson) {
        HotelBookingAvailable hotelBookingAvailable = new HotelBookingAvailable();
        List<RoomCategories> addedCategories = new ArrayList<>();
        RoomCategories categories = new RoomCategories();
        List<RoomImage> roomImages = new ArrayList<>();
        List<CategoryRoomFurniture> categoryRoomFurnitures = new ArrayList<>();
        List<RoomFurniture> roomFurnitures = new ArrayList<>();
        List<DateInfoCategoryRoomPriceDTO> dateInfoList = new ArrayList<>();

        if (checkIn == null || checkOut == null) {
            throw new SearchRoomCustomerExceptionHandler("CheckIn và CheckOut không thể null");
        }

        validateDate("CheckIn", checkIn);
        validateDate("CheckOut", checkOut);

        if (checkIn.equals(checkOut)) {
            throw new SearchRoomCustomerExceptionHandler("CheckIn không trùng CheckOut");
        }
        if (checkIn.isAfter(checkOut) || checkOut.isBefore(checkIn)) {
            throw new SearchRoomCustomerExceptionHandler("CheckIn phải trước CheckOut");
        }

        LocalDate currentDate = LocalDate.now(); // Current date

        if (checkIn.isBefore(currentDate) && checkOut.isBefore(currentDate)) {
            throw new SearchRoomCustomerExceptionHandler("CheckIn và CheckOut phải sau ngày hiện tại");
        }

        List<Room> rooms = roomRepository.getAllRoom(checkIn, checkOut, numberPerson);

        // check xem phòng có sẵn sàng trong ngày hôm nay không
        LocalDate newDate = LocalDate.now();
        List<Room> roomCheckToday = new ArrayList<>();
        if (checkIn.isEqual(newDate)) {
            roomCheckToday = rooms.stream()
                    .filter(room -> room.getRoomStatusId() != null && room.getRoomStatusId() == 2)
                    .collect(Collectors.toList());
        } else {
            roomCheckToday = rooms;
        }
        RoomImage roomImage = new RoomImage();
        Map<Long, List<Room>> groupedRooms = roomCheckToday.stream()
                .collect(Collectors.groupingBy(Room::getRoomCategoryId));
        for (Map.Entry<Long, List<Room>> entry : groupedRooms.entrySet()) {
            Long categoryId = entry.getKey();
            List<Room> roomsWithSameCategory = entry.getValue();
            addedCategories.add(roomCategoriesRepository.findDistinctByRoomCategoryId(categoryId));
            for (int i = 0; i < roomsWithSameCategory.size(); i++) {
                roomImage = roomImageRepository.findByRoomImageId(roomsWithSameCategory.get(i).getRoomImageId());
                roomImages.add(roomImage);
            }
        }
        if (addedCategories == null) {
            throw new RoomCategoryNamesNullException("Khônng có phòng nào trong khoảng thời gian này");
        }
        // Group rooms by room category

        for (int i = 0; i < addedCategories.size(); i++) {
            categoryRoomFurnitures = categoryRoomFurnitureRepository
                    .findByRoomCategoryId(addedCategories.get(i).getRoomCategoryId());
        }

        RoomFurniture roomFurniture = new RoomFurniture();
        for (int i = 0; i < categoryRoomFurnitures.size(); i++) {
            roomFurniture = roomFurnitureRepository.findByFurnitureId(categoryRoomFurnitures.get(i).getFurnitureId());
            roomFurnitures.add(roomFurniture);
        }
        System.out.println(roomFurnitures);

        CategoryRoomPrice categoryRoomPrice = new CategoryRoomPrice();
        List<CategoryRoomPrice> categoryRoomPrices = new ArrayList<>();
        for (int i = 0; i < addedCategories.size(); i++) {
            categoryRoomPrice = categoryRoomPriceRepository.getCategoryId(addedCategories.get(i).getRoomCategoryId());

            categoryRoomPrices.add(categoryRoomPrice);
        }

        LocalDate startDate1 = checkIn;
        LocalDate endDate1 = checkOut != null ? checkIn.plusDays(30) : null;

        if (startDate1 != null && endDate1 != null) {
            while (!startDate1.isAfter(endDate1)) {
                int dayType = getDayType(startDate1);

                dateInfoList.add(new DateInfoCategoryRoomPriceDTO(startDate1, dayType));
                startDate1 = startDate1.plusDays(1);
            }
        }

        hotelBookingAvailable.setRooms(rooms);
        hotelBookingAvailable.setRoomCategories(addedCategories);
        hotelBookingAvailable.setCategoryRoomFurnitures(categoryRoomFurnitures);
        hotelBookingAvailable.setRoomFurnitures(roomFurnitures);
        hotelBookingAvailable.setTotalRoom(groupedRooms);
        hotelBookingAvailable.setCategoryRoomPrices(categoryRoomPrices);
        hotelBookingAvailable.setDateInfoCategoryRoomPriceDTOS(dateInfoList);
        hotelBookingAvailable.setRoomImages(roomImages);

        return hotelBookingAvailable;
    }

    @Override
    public Page<ViewHotelBookingDTO> findAllBookingByUserId(Long id, LocalDate checkIn, Integer status, int page,
                                                            int size) {

        List<RoomCategories> roomCategoriesList = new ArrayList<>();
        Page<HotelBooking> hotelBookingPage = null;
        PageRequest pageRequest = PageRequest.of(page, size);
        if (checkIn == null && status == null) {
            hotelBookingPage = hotelBookingRepository.findAllByUserId(id, pageRequest);
        } else if (checkIn != null && status == null) {

            hotelBookingPage = hotelBookingRepository.findAllByUserIdAndCheckIn(id, checkIn, pageRequest);
        } else if (checkIn == null && status != null) {
            hotelBookingPage = hotelBookingRepository.findAllByUserIdAndStatusId(id, status, pageRequest);
        } else {

            hotelBookingPage = hotelBookingRepository.findAllByUserIdAndCheckInAndStatusId(id, checkIn, status,
                    pageRequest);
        }
        return hotelBookingPage.map(hotelBooking -> {
            ViewHotelBookingDTO viewHotelBookingDTO = new ViewHotelBookingDTO();
            CustomerCancellation customerCancellation = customerCancellationRepository
                    .findCustomerCancellationNewHotelBookingId(hotelBooking.getHotelBookingId());
            HotelBookingStatus hotelBookingStatus = hotelBookingStatusRepository
                    .findByStatusId(hotelBooking.getStatusId());
            User user = userRepository.findByUserId(hotelBooking.getUserId());
            Feedback feedback = feedbackRepository.findByHotelBookingId(hotelBooking.getHotelBookingId());
            viewHotelBookingDTO.setHotelBooking(hotelBooking);
            viewHotelBookingDTO.setHotelBookingStatus(hotelBookingStatus);
            viewHotelBookingDTO.setCustomerCancellation(customerCancellation);
            viewHotelBookingDTO.setFeedback(feedback);
            viewHotelBookingDTO.setUser(user);
            return viewHotelBookingDTO;
        });
    }

    @Override

    public CreateBookingDTO createBooking(List<Long> roomCategoryNames, List<Integer> selectedRoomCategories,
                                          LocalDate checkIn, LocalDate checkOut) {
        CreateBookingDTO createBookingDTO = new CreateBookingDTO();
        Map<Long, Integer> roomCategoryMap = new HashMap<>();

        // Check if roomCategoryNames is null or empty
        if (roomCategoryNames == null || roomCategoryNames.isEmpty()) {
            throw new CreateBooingExceptionHandler("Bạn chưa đặt phòng nào");
        }

        // Check if selectedRoomCategories is null or empty
        if (selectedRoomCategories == null || selectedRoomCategories.isEmpty()) {
            throw new CreateBooingExceptionHandler("Số lượng phòng không hợp lệ");
        }

        // Check if both lists have the same size
        if (roomCategoryNames.size() != selectedRoomCategories.size()) {
            throw new CreateBooingExceptionHandler("Số lượng phòng không khớp với loại phòng");
        }

        // Lấy ra các Loại phòng và số phòng còn trống
        if (roomCategoryNames.size() == selectedRoomCategories.size()) {
            for (int i = 0; i < roomCategoryNames.size(); i++) {
                Long category = roomCategoryNames.get(i);
                int roomCount = selectedRoomCategories.get(i);
                if (roomCount > 0) {
                    roomCategoryMap.put(category, roomCount);
                }
            }
        }
        if (roomCategoryMap.isEmpty()) {
            throw new CreateBooingExceptionHandler("Bạn chưa đặt phòng nào");
        }
        // Room Available by CategoryId
        List<Room> rooms = new ArrayList<>();

        List<RoomCategories> roomCategoriesList = new ArrayList<>();

        for (Map.Entry<Long, Integer> entry : roomCategoryMap.entrySet()) {
            Long category = entry.getKey();

            rooms = roomRepository.findAvailableRoomsByCategoryId(category, checkIn, checkOut);
            roomCategoriesList.add(roomCategoriesRepository.findByRoomCategoryId(category));
        }

        for (Map.Entry<Long, Integer> entry : roomCategoryMap.entrySet()) {
            Long category = entry.getKey();
            List<Room> roomsByCategory = roomRepository.findAvailableRoomsByCategoryId(category, checkIn, checkOut);
            rooms.addAll(roomsByCategory);
        }

        // Group rooms by room category
        Map<Long, List<Room>> roomMap = rooms.stream().collect(Collectors.groupingBy(Room::getRoomCategoryId));

        List<CategoryRoomPrice> categoryRoomPrices = new ArrayList<>();

        // Price theo Category
        for (RoomCategories roomCategory : roomCategoriesList) {
            CategoryRoomPrice categoryRoomPrice = categoryRoomPriceRepository
                    .getCategoryId(roomCategory.getRoomCategoryId());
            categoryRoomPrices.add(categoryRoomPrice);
        }

        List<DateInfoCategoryRoomPriceDTO> dateInfoList = bookingDayUtils.processDateInfo(checkIn, checkOut);

        BigDecimal total_Price = BigDecimal.ZERO;
        Map<Long, BigDecimal> totalPriceByCategoryId = new HashMap<>();

        for (CategoryRoomPrice cpr : categoryRoomPrices) {
            BigDecimal totalForCategory = BookingDayUtils.calculateTotalForCategory(cpr, dateInfoList);
            totalPriceByCategoryId.put(cpr.getRoomCategoryId(), totalForCategory);
        }

        // Lặp qua map 1 và kiểm tra xem khóa có tồn tại trong map 2 không
        for (Map.Entry<Long, Integer> entry1 : roomCategoryMap.entrySet()) {
            Long category = entry1.getKey();
            Integer roomCount = entry1.getValue();

            if (totalPriceByCategoryId.containsKey(category)) {
                // Nếu khóa tồn tại trong map 2, lấy giá trị từ cả hai map
                BigDecimal totalPrice1 = totalPriceByCategoryId.get(category);
                BigDecimal totalPrice2 = totalPrice1.multiply(BigDecimal.valueOf(roomCount));
                total_Price = total_Price.add(totalPrice2);

            }
        }
        BigDecimal totalPrice = total_Price.multiply(BigDecimal.valueOf(1.1));
        BigDecimal divisor = new BigDecimal("2");
        createBookingDTO.setRoomCategoriesList(roomCategoriesList);
        createBookingDTO.setTotalPrice(total_Price);
        createBookingDTO.setAllPrice(totalPrice);
        createBookingDTO.setDepositPrice(totalPrice.divide(divisor, 0, BigDecimal.ROUND_HALF_UP));
        createBookingDTO.setCheckIn(checkIn);
        createBookingDTO.setCheckOut(checkOut);
        createBookingDTO.setRoomCategoryMap(roomCategoryMap);
        createBookingDTO.setTotalPriceByCategoryId(totalPriceByCategoryId);
        createBookingDTO.setDateInfoList(dateInfoList);
        createBookingDTO.setCategoryRoomPrice(categoryRoomPrices);

        return createBookingDTO;

    }

    @Override
    public void cancelBooking(CancellationFormDTO cancellationFormDTO, Authentication authentication)
            throws CancellationExistException {

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userRepository.getUserByEmail(userDetails.getUsername());
        UserRole userRole = userRoleRepository.findByUserIdAndAndRoleId(user.getUserId(), 6L);
        Transactions transactions = transactionsRepository
                .findByHotelBookingIdNew(cancellationFormDTO.getHotelBookingId());

        // RefundAccount
        RefundAccount refundAccount = new RefundAccount();
        refundAccount.setAccountNumber(cancellationFormDTO.getAccountNumber());
        refundAccount.setAccountName(cancellationFormDTO.getAccountName());
        refundAccount.setBankId(cancellationFormDTO.getBankId());
        if (userRole != null) {
            refundAccount.setUserId(user.getUserId());
        } else {
            refundAccount.setUserId(null);
        }

        RefundAccount newRefundAccount = refundAccountRepository.save(refundAccount);

        List<CustomerCancellation> cancellationList = customerCancellationRepository.findAll();
        CustomerCancellation customerCancellation = new CustomerCancellation();
        customerCancellation.setHotelBookingId(cancellationFormDTO.getHotelBookingId());
        customerCancellation.setCancelTime(new Date());
        customerCancellation.setReasonId(cancellationFormDTO.getReasonId());
        customerCancellation.setOtherReason(cancellationFormDTO.getOtherReason());
        customerCancellation.setAccountId(newRefundAccount.getAccountId());
        customerCancellation.setStatus(0);
        customerCancellation.setTransactionId(transactions.getTransactionId());

        customerCancellationRepository.save(customerCancellation);

    }

    @Override
    public Long saveRoomAfterBooking(Authentication authentication, HttpSession session, BigDecimal totalPrice)
            throws ResetExceptionHandler {

        CreateBookingDTO createBookingDTO = (CreateBookingDTO) session.getAttribute("createBookingDTO");
        Map<Long, Integer> roomCategoryMap = createBookingDTO.getRoomCategoryMap();
        BigDecimal result = createBookingDTO.getDepositPrice();
        result = createBookingDTO.getDepositPrice().setScale(totalPrice.scale());
        int comparisonResult = result.compareTo(totalPrice);
        if (authentication != null) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            User user = userRepository.getUserByEmail(userDetails.getUsername());
            if (user != null) {
                HotelBooking hotelBooking = new HotelBooking();
                // find new id

                HotelBooking newHotelBooking = null;
                int totalRoom = 0;

                // save hotelBooking
                hotelBooking.setUserId(user.getUserId());

                LocalDate checkInDate = createBookingDTO.getCheckIn();
                LocalDate checkOutDate = createBookingDTO.getCheckOut();
                LocalDateTime checkInWithSpecificTime = LocalDateTime.of(checkInDate.getYear(), checkInDate.getMonth(),
                        checkInDate.getDayOfMonth(), 14, 0, 0); // Year, Month, Day, Hour, Minute, Second
                LocalDateTime checkoutWithSpecificTime = LocalDateTime.of(checkOutDate.getYear(),
                        checkOutDate.getMonth(), checkOutDate.getDayOfMonth(), 12, 0, 0); // Year, Month, Day, Hour,
                // Minute, Second
                ZoneId zoneId = ZoneId.of("Asia/Ho_Chi_Minh");

                Instant checkOut = checkoutWithSpecificTime.atZone(zoneId).toInstant();
                Instant checkIn = checkInWithSpecificTime.atZone(zoneId).toInstant();

                hotelBooking.setCheckIn(checkIn);
                hotelBooking.setCheckInActual(checkIn);
                hotelBooking.setCheckOut(checkOut);
                hotelBooking.setCheckOutActual(checkOut);
                hotelBooking.setName(user.getName());
                hotelBooking.setEmail(user.getEmail());
                hotelBooking.setPhone(user.getPhone());
                hotelBooking.setAddress(user.getAddress());
//                // so sánh với totalPrice
                if (comparisonResult == 0) {
                    hotelBooking.setDepositPrice(totalPrice);
                } else { // deposit
                    hotelBooking.setDepositPrice(totalPrice);
                }
//                hotelBooking.setDepositPrice(result);
                hotelBooking.setTotalPrice(createBookingDTO.getTotalPrice().multiply(BigDecimal.valueOf(1.1)));
                for (Map.Entry<Long, Integer> entry : roomCategoryMap.entrySet()) {
                    Integer value = entry.getValue();
                    totalRoom += value;
                }
                hotelBooking.setTotalRoom(totalRoom);
                hotelBooking.setStatusId((Long) 1L);
                hotelBooking.setValidBooking(true);
                newHotelBooking = hotelBookingRepository.save(hotelBooking);

                // converst
                LocalDate localDateCheckIn = newHotelBooking.getCheckIn().atZone(ZoneId.systemDefault()).toLocalDate();
                LocalDate localDateCheckOut = newHotelBooking.getCheckOut().atZone(ZoneId.systemDefault())
                        .toLocalDate();

                for (Map.Entry<Long, Integer> entry : roomCategoryMap.entrySet()) {
                    Long categoryId = entry.getKey();
                    Integer roomCount = entry.getValue();
                    List<Room> rooms = roomRepository.findAvailableRoomsByCategoryId(categoryId, localDateCheckIn,
                            localDateCheckOut);

                    // Khai báo biến đếm số lần đã thêm phòng
                    int roomsAdded = 0;

                    for (Room room : rooms) {
                        if (roomsAdded < roomCount) {
                            BookingRoomDetails bookingRoomDetails = new BookingRoomDetails();
                            bookingRoomDetails.setRoomId(room.getRoomId());
                            bookingRoomDetails.setHotelBookingId(newHotelBooking.getHotelBookingId());
                            bookingRoomDetails.setRoomCategoryId(categoryId);
                            bookingRoomDetailsRepository.save(bookingRoomDetails);

                            // Tăng biến đếm số phòng đã thêm
                            roomsAdded++;
                        } else {
                            break;
                        }

                    }

                }
                return newHotelBooking.getHotelBookingId();
            }
        } else {
            HotelBooking hotelBooking = new HotelBooking();
            GuestBookingDTO guestBookingDTO1 = (GuestBookingDTO) session.getAttribute("guestBookingDTO");
            // find new id
            HotelBooking newHotelBooking = null;
            int totalRoom = 0;

            // save hotelBooking
            hotelBooking.setUserId(null);
            hotelBooking.setName(guestBookingDTO1.getName());
            hotelBooking.setAddress(guestBookingDTO1.getAddress());
            hotelBooking.setPhone(guestBookingDTO1.getPhone());
            hotelBooking.setEmail(guestBookingDTO1.getEmail());
            hotelBooking.setNote(guestBookingDTO1.getNote());

            LocalDate checkInDate = createBookingDTO.getCheckIn();
            LocalDate checkOutDate = createBookingDTO.getCheckOut();
            LocalDateTime checkInWithSpecificTime = LocalDateTime.of(checkInDate.getYear(), checkInDate.getMonth(),
                    checkInDate.getDayOfMonth(), 14, 00, 0); // Year, Month, Day, Hour, Minute, Second
            LocalDateTime checkoutWithSpecificTime = LocalDateTime.of(checkOutDate.getYear(), checkOutDate.getMonth(),
                    checkOutDate.getDayOfMonth(), 12, 00, 0); // Year, Month, Day, Hour, Minute, Second
            ZoneId zoneId = ZoneId.of("Asia/Ho_Chi_Minh");

            Instant checkOut = checkoutWithSpecificTime.atZone(zoneId).toInstant();
            Instant checkIn = checkInWithSpecificTime.atZone(zoneId).toInstant();

            hotelBooking.setCheckIn(checkIn);
            hotelBooking.setCheckInActual(checkIn);
            hotelBooking.setCheckOut(checkOut);
            hotelBooking.setCheckOutActual(checkOut);

            if (comparisonResult == 0) {
                hotelBooking.setDepositPrice(totalPrice);
            } else { // deposit
                hotelBooking.setDepositPrice(totalPrice);
            }
            hotelBooking.setTotalPrice(createBookingDTO.getTotalPrice().multiply(BigDecimal.valueOf(1.1)));

            for (Map.Entry<Long, Integer> entry : roomCategoryMap.entrySet()) {
                Integer value = entry.getValue();
                totalRoom += value;
            }
            hotelBooking.setTotalRoom(totalRoom);
            hotelBooking.setStatusId((Long) 1L);
            hotelBooking.setValidBooking(true);
            newHotelBooking = hotelBookingRepository.save(hotelBooking);

            // converst
            LocalDate localDateCheckIn = newHotelBooking.getCheckIn().atZone(ZoneId.systemDefault()).toLocalDate();
            LocalDate localDateCheckOut = newHotelBooking.getCheckOut().atZone(ZoneId.systemDefault()).toLocalDate();

            RoomStatusHistory roomStatusHistory = new RoomStatusHistory();
            for (Map.Entry<Long, Integer> entry : roomCategoryMap.entrySet()) {
                Long categoryId = entry.getKey();
                Integer roomCount = entry.getValue();
                List<Room> rooms = roomRepository.findAvailableRoomsByCategoryId(categoryId, localDateCheckIn,
                        localDateCheckOut);

                // Khai báo biến đếm số lần đã thêm phòng
                int roomsAdded = 0;

                for (Room room : rooms) {
                    if (roomsAdded < roomCount) {
                        BookingRoomDetails bookingRoomDetails = new BookingRoomDetails();
                        bookingRoomDetails.setRoomId(room.getRoomId());
                        bookingRoomDetails.setHotelBookingId(newHotelBooking.getHotelBookingId());
                        bookingRoomDetails.setRoomCategoryId(categoryId);
                        bookingRoomDetailsRepository.save(bookingRoomDetails);

                        roomsAdded++;
                    } else {
                        break;
                    }

                }

            }
            sendBookingRequest(newHotelBooking.getHotelBookingId());

            return newHotelBooking.getHotelBookingId();
        }
        return 0L;
    }

    @Override
    public void sendBookingRequest(Long hotelBookingId) throws ResetExceptionHandler {
        BookingDetailsDTO bookingDetailsDTO = bookingRoomDetailsService.getBookingDetailsByHotelBookingIsValid(hotelBookingId);

        if (bookingDetailsDTO == null || bookingDetailsDTO.getHotelBooking() == null) {
            throw new ResetExceptionHandler("Chi tiết đặt phòng không hợp lệ");
        }

        String email = bookingDetailsDTO.getHotelBooking().getEmail();

        if (email == null) {
            throw new MailExceptionHandler("Email không hợp lệ");
        }

        try {
            Context context = new Context();
            context.setVariable("checkIn", bookingDetailsDTO.getHotelBooking().getCheckIn());
            context.setVariable("checkOut", bookingDetailsDTO.getHotelBooking().getCheckOut());
            context.setVariable("totalRoom", bookingDetailsDTO.getHotelBooking().getTotalRoom());
            context.setVariable("bookingDetailsDTO", bookingDetailsDTO);
            context.setVariable("userId", bookingDetailsDTO.getHotelBooking().getName());

            String emailContent = templateEngine.process("email/createBookingEmail", context);

            // Gửi email
            emailUtil.sendBookingEmail(email, "Thông tin đặt phòng", emailContent);
        } catch (Exception e) {
            // Log or handle the exception if needed
            throw new MailExceptionHandler("Lỗi gửi mail");
        }
    }

    @Override
    public HotelBooking findById(Long id) {
        return hotelBookingRepository.findById(id).orElse(null);
    }

    @Override
    public void changeStatusId(Long hotelBookingId) {
        HotelBooking hotelBooking = hotelBookingRepository.findByHotelBookingId(hotelBookingId);
        hotelBooking.setStatusId(4L);
        hotelBookingRepository.save(hotelBooking);
    }

    @Override
    public Page<ViewBookingDTO> searchCheckInAndCheckOutAndStatus(LocalDate checkIn, LocalDate checkOut, Integer status,
                                                                  int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<HotelBooking> hotelBookingPage = null;

        if (checkIn != null && checkOut != null) {
            LocalDateTime checkInTime = LocalDateTime.of(checkIn, LocalTime.MIN);
            LocalDateTime checkOutTime = LocalDateTime.of(checkOut, LocalTime.MAX);
            hotelBookingPage = hotelBookingRepository.findAllCheckInAndCheckOutAndStatus(checkInTime, checkOutTime,
                    status, pageRequest);
        } else if (checkIn != null && checkOut == null) {
            hotelBookingPage = hotelBookingRepository.findByCheckIn(checkIn, status, pageRequest);
        } else if (checkIn == null && checkOut != null) {
            hotelBookingPage = hotelBookingRepository.findByCheckOut(checkOut, status, pageRequest);
        } else {
            hotelBookingPage = hotelBookingRepository.findByStatusId(status, pageRequest);
        }

        return hotelBookingPage.map(hotelBooking -> {
            ViewBookingDTO viewBookingDTO = new ViewBookingDTO();
            User user = userRepository.findByUserId(hotelBooking.getUserId());
            HotelBookingStatus hotelBookingStatus = hotelBookingStatusRepository
                    .findByStatusId(hotelBooking.getStatusId());
            if (user != null) {
                viewBookingDTO.setUser(user);
            }
            viewBookingDTO.setHotelBookingStatus(hotelBookingStatus);
            viewBookingDTO.setHotelBooking(hotelBooking);
            return viewBookingDTO;
        });

    }

    @Override
    public int getDayType(LocalDate startDate) {
        return bookingDayUtils.getDayType(startDate);
    }

    private void validateDate(String dateName, LocalDate date) {
        try {
            // Validate the LocalDate format
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            String formattedDate = date.format(formatter);
            LocalDate localDate = LocalDate.parse(formattedDate, formatter);
        } catch (DateTimeParseException e) {
            throw new SearchRoomCustomerExceptionHandler("Ngày " + dateName + " không đúng định dạng");
        }
    }

}
