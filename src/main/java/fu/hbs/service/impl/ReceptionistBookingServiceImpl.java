package fu.hbs.service.impl;

import java.util.List;
import java.util.Optional;

import fu.hbs.dto.ReceptionistDTO.ViewBookingDTO;
import fu.hbs.utils.EmailUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import fu.hbs.repository.HotelBookingRepository;
import fu.hbs.repository.RoomStatusRepository;
import fu.hbs.service.interfaces.BookingRoomDetailsService;
import fu.hbs.service.interfaces.ReceptionistBookingService;
import fu.hbs.constant.TransactionMessage;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import fu.hbs.dto.HotelBookingDTO.*;
import fu.hbs.entities.*;
import fu.hbs.exceptionHandler.CheckoutException;
import fu.hbs.exceptionHandler.MailExceptionHandler;
import fu.hbs.exceptionHandler.NotEnoughRoomAvalaibleException;
import fu.hbs.exceptionHandler.ResetExceptionHandler;
import fu.hbs.repository.*;
import fu.hbs.utils.BookingUtil;
import fu.hbs.utils.RandomKey;
import fu.hbs.validator.BookingValidator;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
@Transactional
public class ReceptionistBookingServiceImpl implements ReceptionistBookingService {

    @Autowired
    private HotelBookingRepository bookingRepository;
    @Autowired
    private RoomStatusRepository roomStatusRepository; // Autowire RoomStatusRepository
    @Autowired
    private BookingRoomDetailsRepository bookingRoomDetailsRepository;
    @Autowired
    private EarlyCheckoutRefundsRepository earlyCheckoutRefundsRepository;
    @Autowired
    private RoomRepository roomRepository;
    @Autowired
    private HotelBookingServiceRepository hotelBookingServiceRepository;
    @Autowired
    private TransactionsRepository transactionsRepository;
    @Autowired
    private CategoryRoomPriceRepository categoryRoomPriceRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private HotelBookingStatusRepository hotelBookingStatusRepository;
    private final EmailUtil emailUtil;
    @Autowired
    private TemplateEngine templateEngine;
    @Autowired
    private BookingRoomDetailsService bookingRoomDetailsService;

    public ReceptionistBookingServiceImpl(EmailUtil emailUtil) {
        this.emailUtil = emailUtil;
    }

    @Override
    public List<HotelBooking> findAll() {
        return bookingRepository.findAll();
    }

    @Override
    public HotelBooking findById(Long id) {
        Optional<HotelBooking> result = bookingRepository.findById(id);
        HotelBooking booking = null;
        if (result.isPresent()) {
            booking = result.get();
        } else {
            throw new RuntimeException("Không tìm thấy đặt phòng có id - " + id);
        }
        return booking;
    }

    @Override
    public void save(HotelBooking booking) {
        bookingRepository.save(booking);
    }

    @Override
    @Transactional
    public Long createHotelBookingByReceptionist(CreateHotelBookingDTO bookingRequest) {
        // Create basic object booking and save to get the id
        HotelBooking hotelBooking = new HotelBooking();
        hotelBooking.setUserId(null);
        hotelBooking.setStatusId(1L);
        hotelBooking.setName(bookingRequest.getName());
        hotelBooking.setEmail(bookingRequest.getEmail());
        hotelBooking.setAddress(bookingRequest.getAddress());
        hotelBooking.setPhone(bookingRequest.getPhone());
        hotelBooking.setNote(bookingRequest.getNotes().trim());
        LocalDate checkInLocalDate = bookingRequest.getCheckIn();
        LocalDate checkOutLocalDate = bookingRequest.getCheckOut();
        LocalDateTime checkInWithSpecificTime = LocalDateTime.of(checkInLocalDate.getYear(),
                checkInLocalDate.getMonth(), checkInLocalDate.getDayOfMonth(), 14, 0, 0); // Year, Month, Day, Hour,
        // Minute, Second
        LocalDateTime checkoutWithSpecificTime = LocalDateTime.of(checkOutLocalDate.getYear(),
                checkOutLocalDate.getMonth(), checkOutLocalDate.getDayOfMonth(), 12, 0, 0); // Year, Month, Day,
        // Hour, Minute, Second
        ZoneId zoneId = ZoneId.of("Asia/Ho_Chi_Minh");

        Instant checkOut = checkoutWithSpecificTime.atZone(zoneId).toInstant();
        Instant checkIn = checkInWithSpecificTime.atZone(zoneId).toInstant();

        hotelBooking.setCheckIn(checkIn);
        hotelBooking.setCheckOut(checkOut);
        hotelBooking.setCheckInActual(checkIn);
        hotelBooking.setCheckOutActual(checkOut);
        boolean validTimeForBooking = BookingValidator.isValidTimeForBooking(checkIn, checkOut);
        if (!validTimeForBooking) {
            throw new RuntimeException("Thời gian đặt phòng không hợp lệ!");
        }
        // Calculate total room number
        Optional<Integer> totalRoomNumberOp = bookingRequest.getBookingDetails().stream()
                .map(CreateHotelBookingDetailDTO::getRoomNumber).reduce(Integer::sum);
        totalRoomNumberOp.ifPresent(hotelBooking::setTotalRoom);

        // Save to get id before create Booking Details
        bookingRepository.save(hotelBooking);

        // Base on request and then create booking detail
        List<BookingRoomDetails> bookingDetails = createBookingDetails(bookingRequest, checkIn, checkOut, hotelBooking);
        bookingRoomDetailsRepository.saveAll(bookingDetails);
        BigDecimal totalPrice = this.calculateTotalPrice(bookingDetails, checkIn, checkOut);
        totalPrice = totalPrice.add(totalPrice.multiply(BigDecimal.valueOf(0.1))).setScale(0, RoundingMode.HALF_DOWN);
        // Deposit Option Yes/No
        if (bookingRequest.isPayFull()) {
            hotelBooking.setDepositPrice(totalPrice);
        } else {
            hotelBooking
                    .setDepositPrice(totalPrice.multiply(BigDecimal.valueOf(0.5)).setScale(0, RoundingMode.HALF_DOWN));
        }
        if (bookingRequest.getPaymentTypeId() != 1L) {
            Transactions transaction = new Transactions();
            transaction.setVnpayTransactionId(RandomKey.generateRandomKey());
            transaction.setStatus("Thành công");
            transaction.setAmount(hotelBooking.getDepositPrice());
            transaction.setCreatedDate(LocalDateTime.ofInstant(Instant.now(), ZoneId.systemDefault()));
            transaction.setHotelBookingId(hotelBooking.getHotelBookingId());
            transaction.setPaymentId(2L);
            transaction.setContent(TransactionMessage.PRE_PAY.getMessage());
            transaction.setPaymentId(bookingRequest.getPaymentTypeId());
            transaction.setTransactionsTypeId(1L);
            hotelBooking.setValidBooking(true);
            hotelBooking.setTotalPrice(totalPrice);
            try {
                sendBookingRequest(hotelBooking.getHotelBookingId());
            } catch (ResetExceptionHandler e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            transactionsRepository.save(transaction);
        }

        return hotelBooking.getHotelBookingId();
    }

    public List<BookingRoomDetails> createBookingDetails(CreateHotelBookingDTO bookingRequest, Instant checkIn,
                                                         Instant checkOut, HotelBooking hotelBooking) {
        List<BookingRoomDetails> bookingRoomDetails = new ArrayList<>(); // All result of BookingRoomDetails
        Map<Long, List<Room>> availableRoomWithCategoryMap = new HashMap<>(); // Map for save temporary all available
        // room in this creating session

        for (CreateHotelBookingDetailDTO bookingDetailRequest : bookingRequest.getBookingDetails()) {
            // Get available room from DB if we not yet store in map
            List<Room> availableRoomsByCategoryIdAndDateBetween = availableRoomWithCategoryMap
                    .get(bookingDetailRequest.getRoomCategoryId());
            if (availableRoomsByCategoryIdAndDateBetween == null) {
                availableRoomsByCategoryIdAndDateBetween = this.findAvailableRoomsByCategoryIdAndDateBetween(checkIn,
                        checkOut, bookingDetailRequest.getRoomCategoryId());
                availableRoomWithCategoryMap.put(bookingDetailRequest.getRoomCategoryId(),
                        availableRoomsByCategoryIdAndDateBetween);
            }
            // Check if available room number less than booking number
            Integer bookingRoomNumber = bookingDetailRequest.getRoomNumber();
            if (availableRoomsByCategoryIdAndDateBetween.size() < bookingDetailRequest.getRoomNumber()) {
                throw new NotEnoughRoomAvalaibleException("Không còn đủ phòng để đặt!");
            }

            for (int i = 0; i < bookingRoomNumber; i++) {
                Room room = availableRoomsByCategoryIdAndDateBetween.stream().findFirst().get();
                BookingRoomDetails bookingRoomDetail = new BookingRoomDetails();
                bookingRoomDetail.setHotelBookingId(hotelBooking.getHotelBookingId());
                bookingRoomDetail.setRoomCategoryId(bookingDetailRequest.getRoomCategoryId());
                bookingRoomDetail.setRoomId(room.getRoomId());
                bookingRoomDetails.add(bookingRoomDetail);
                // Temporarily remove room from Available Room after create a Booking Detail
                availableRoomsByCategoryIdAndDateBetween.remove(room);
            }
        }
        return bookingRoomDetails;
    }

    public List<Room> findAvailableRoomsByCategoryIdAndDateBetween(Instant start, Instant end, Long roomCategoryId) {
        LocalDateTime startDateTime = LocalDateTime.ofInstant(start, ZoneOffset.UTC);
        LocalDateTime endDateTime = LocalDateTime.ofInstant(end, ZoneOffset.UTC);

        return roomRepository.findAvailableRoomsByCategoryId(roomCategoryId, startDateTime.toLocalDate(),
                endDateTime.toLocalDate());
    }

    public BigDecimal calculateTotalPrice(List<BookingRoomDetails> bookingRoomDetailsList, Instant checkin,
                                          Instant checkout) {
        List<Long> bookRoomCategoryIds = bookingRoomDetailsList.stream().map(BookingRoomDetails::getRoomCategoryId)
                .distinct().collect(Collectors.toList());
        Map<Long, CategoryRoomPrice> priceMap = categoryRoomPriceRepository.findAllById(bookRoomCategoryIds).stream()
                .collect(Collectors.toMap(CategoryRoomPrice::getRoomCategoryId, Function.identity()));
        BigDecimal totalPrice = BigDecimal.ZERO;
        for (BookingRoomDetails bookingRoomDetails : bookingRoomDetailsList) {
            categoryRoomPriceRepository.getCategoryId(bookingRoomDetails.getRoomCategoryId());
            BigDecimal priceWithDate = BookingUtil.calculatePriceBetweenDate(checkin, checkout,
                    bookingRoomDetails.getRoomCategoryId(), false);
            totalPrice = totalPrice.add(priceWithDate);
        }
        return totalPrice;
    }

    @Override
    public boolean checkout(SaveCheckoutDTO saveCheckoutDTO) {
        Optional<HotelBooking> hotelBookingOptional = bookingRepository.findById(saveCheckoutDTO.getHotelBookingId());
        if (hotelBookingOptional.isPresent()) {
            // Update hotel booking status
            HotelBooking hotelBooking = hotelBookingOptional.get();

            if (!BookingValidator.isValidToCheckOut(hotelBooking)) {
                throw new CheckoutException("Trạng thái của đặt phòng chưa sẵn sàng cho checkout!");
            }
            Instant exptectedCheckoutDate = hotelBooking.getCheckOut();
            hotelBooking.setCheckOutActual(Instant.now());
            // Change room status
            List<BookingRoomDetails> hotelBookingDetails =
                    bookingRoomDetailsRepository.getAllByHotelBookingId(hotelBooking.getHotelBookingId());
            changeRoomStatus(hotelBookingDetails);

            List<SaveCheckoutHotelServiceDTO> hotelBookingServices = saveCheckoutDTO.getHotelServices();
            hotelBookingServiceRepository.deleteAllByHotelBookingId(hotelBooking.getHotelBookingId()); // Clean all
            // old used service and use latest ones
            Map<Long, RoomService> allRoomServiceAsMap = BookingUtil.getAllRoomServiceAsMap();

            List<HotelBookingService> hotelBookingServiceList = new ArrayList<>();
            BigDecimal servicePrice = BigDecimal.ZERO;
            servicePrice = calculateServicePrice(hotelBookingServices, allRoomServiceAsMap, servicePrice,
                    hotelBooking, hotelBookingServiceList);

            BigDecimal roomPrice = BigDecimal.ZERO;
            roomPrice = calculateRoomPrice(hotelBookingDetails, roomPrice, hotelBooking.getCheckIn(),
                    hotelBooking.getCheckOut());

            updateTotalPriceOfBooking(servicePrice, roomPrice, hotelBooking, saveCheckoutDTO.getSurcharge());

            if (!(saveCheckoutDTO.getPaymentTypeId() == 1)) {
                String content = getCheckoutContent(hotelBooking);
                Transactions transactions = makeTransaction(hotelBooking.getTotalPrice(), hotelBooking,
                        hotelBooking.getDepositPrice(), saveCheckoutDTO.getPaymentTypeId(), content);
                transactionsRepository.save(transactions);
                List<Long> allRoomId = hotelBookingDetails.stream().map(BookingRoomDetails::getRoomId).collect(Collectors.toList());
                List<Room> allRoom = roomRepository.findAllById(allRoomId);
                Context context = new Context();
                context.setVariable("hotelBookingDetails", allRoom);

                String contentMail = templateEngine.process("email/notificationManagerRoom", context);

                // Gửi email
                try {
                    List<User> managementUser = userRepository.findAllUserByRole(5L);
                    for (User obj : managementUser) {
                        System.out.println("send mail to management:" + obj.getEmail());
                        emailUtil.sendBookingEmail(obj.getEmail(), "Những phòng cần dọn", contentMail);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                if (transactions.getTransactionsTypeId() == 3L) {
                    EarlyCheckoutRefunds earlyCheckoutRefunds = makeEarlyCheckoutRefunds(transactions);
                    earlyCheckoutRefundsRepository.save(earlyCheckoutRefunds);
                }
                hotelBooking.setStatusId(3L);
            }
            hotelBookingServiceRepository.saveAll(hotelBookingServiceList);
            bookingRepository.save(hotelBooking);
            return true;
        }
        return false;
    }

    public static void updateTotalPriceOfBooking(BigDecimal servicePrice, BigDecimal roomPrice,
                                                 HotelBooking hotelBooking, BigDecimal additionalFee) {
        BigDecimal actualTotalPrice = BookingUtil.calculateTotalPriceOfBooking(servicePrice, roomPrice, additionalFee);
        BigDecimal refund = hotelBooking.getDepositPrice().subtract(actualTotalPrice);
        if (refund.compareTo(BigDecimal.ZERO) > 0) {
            hotelBooking.setRefundPrice(refund);
        }
        hotelBooking.setTotalPrice(actualTotalPrice);
    }

    public static Transactions makeTransaction(BigDecimal totalPrice, HotelBooking hotelBooking, BigDecimal prePay,
                                               Long paymentTypeId, String content) {
        Transactions transactions = new Transactions();
        transactions.setVnpayTransactionId(RandomKey.generateRandomKey());
        transactions.setStatus("Thành công");
        BigDecimal remainPrice = totalPrice.subtract(prePay);
        transactions.setAmount(remainPrice);
        transactions.setCreatedDate(LocalDateTime.now());
        transactions.setHotelBookingId(hotelBooking.getHotelBookingId());
        transactions.setPaymentId(paymentTypeId);
        transactions.setContent(content);
        if (remainPrice.compareTo(BigDecimal.ZERO) < 0) {
            transactions.setTransactionsTypeId(3L);
        } else {
            transactions.setTransactionsTypeId(2L);
        }
        return transactions;
    }

    public static EarlyCheckoutRefunds makeEarlyCheckoutRefunds(Transactions transactions) {
        EarlyCheckoutRefunds earlyCheckoutRefunds = new EarlyCheckoutRefunds();
        earlyCheckoutRefunds.setRefundedAmount(transactions.getAmount().abs());
        earlyCheckoutRefunds.setStatus(true);
        earlyCheckoutRefunds.setTransactionId(transactions.getTransactionId());
        earlyCheckoutRefunds.setReason("");
        earlyCheckoutRefunds.setRefundDate(Instant.now());
        return earlyCheckoutRefunds;
    }

    public static BigDecimal calculateRoomPrice(List<BookingRoomDetails> hotelBookingDetails, BigDecimal roomPrice,
                                                Instant checkIn, Instant checkOut) {
        LocalDate checkInDate = LocalDateTime.ofInstant(checkIn, ZoneId.systemDefault()).toLocalDate();
        LocalDate checkoutDate = LocalDateTime.ofInstant(checkOut, ZoneId.systemDefault()).toLocalDate();
        for (BookingRoomDetails bookingRoomDetail : hotelBookingDetails) {
            BigDecimal price = BookingUtil.calculateRoomCostForCheckOut(checkInDate, checkoutDate,
                    bookingRoomDetail.getRoomCategoryId());
            roomPrice = roomPrice.add(price);
        }
        return roomPrice;
    }

    public static BigDecimal calculateServicePrice(List<SaveCheckoutHotelServiceDTO> hotelBookingServices,
                                                   Map<Long, RoomService> allRoomServiceAsMap, BigDecimal servicePrice, HotelBooking hotelBooking,
                                                   List<HotelBookingService> hotelBookingServiceList) {
        for (SaveCheckoutHotelServiceDTO hotelBookingService : hotelBookingServices) {
            RoomService roomService = allRoomServiceAsMap.get(hotelBookingService.getServiceId());
            servicePrice = servicePrice
                    .add(roomService.getServicePrice().multiply(BigDecimal.valueOf(hotelBookingService.getQuantity())));

            HotelBookingService newHotelBookingService = new HotelBookingService();
            newHotelBookingService.setHotelBookingId(hotelBooking.getHotelBookingId());
            newHotelBookingService.setServiceId(roomService.getServiceId());
            newHotelBookingService.setCreateDate(Instant.now());
            newHotelBookingService.setRoomId(1L); // Mặc định là 1L trước về sau sẽ xóa
            newHotelBookingService.setQuantity(hotelBookingService.getQuantity());
            newHotelBookingService.setTotalPrice(roomService.getServicePrice().multiply(BigDecimal.valueOf(hotelBookingService.getQuantity())));
            hotelBookingServiceList.add(newHotelBookingService);
        }
        return servicePrice;
    }

    public void changeRoomStatus(List<BookingRoomDetails> hotelBookingDetails) {
        List<Long> bookedRoomIds = hotelBookingDetails.stream().map(BookingRoomDetails::getRoomId)
                .collect(Collectors.toList());
        List<Room> bookedRooms = roomRepository.findAllById(bookedRoomIds);
        for (Room bookedRoom : bookedRooms) {
            bookedRoom.setRoomStatusId(3L);
        }
        roomRepository.saveAll(bookedRooms);
    }

    @Override
    public BigDecimal getTotalPriceOfHotelBooking(Long hotelBookingId) {
        HotelBooking hotelBooking = bookingRepository.findByHotelBookingId(hotelBookingId);
        if (hotelBooking != null) {
            return hotelBooking.getTotalPrice();
        }
        return null;
    }

    @Override
    public Boolean checkIn(Long hotelBookingId) {
        HotelBooking hotelBooking = bookingRepository.findByHotelBookingId(hotelBookingId);
        if (!BookingValidator.isValidToCheckIn(hotelBooking.getCheckIn())) {
            return false;
        }
        List<BookingRoomDetails> hotelBookingDetails = bookingRoomDetailsRepository
                .getAllByHotelBookingId(hotelBookingId);
        List<Long> allBookedRoomIds = hotelBookingDetails.stream().map(BookingRoomDetails::getRoomId)
                .collect(Collectors.toList());
        List<Room> allBookedRooms = roomRepository.findAllById(allBookedRoomIds);
        boolean isExistNotReadyRoom = BookingValidator.isExistNotReadyRoom(allBookedRooms);
        if (isExistNotReadyRoom) {
            return false;
        }
        // Change status room / booking
        hotelBooking.setStatusId(2L);
        Instant actualCheckIn = Instant.now();
        hotelBooking.setCheckInActual(
                actualCheckIn);

        for (Room room : allBookedRooms) {
            room.setRoomStatusId(1L);
        }
        bookingRepository.save(hotelBooking);
        roomRepository.saveAll(allBookedRooms);
        return true;
    }

    @Override
    public Page<ViewBookingDTO> searchCheckInAndCheckOutAndStatus(LocalDate checkIn, LocalDate checkOut, Integer status,
                                                                  int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<HotelBooking> hotelBookingPage = null;

        if (checkIn != null && status != null && checkOut != null) {
            LocalDateTime checkInTime = LocalDateTime.of(checkIn, LocalTime.MIN);
            LocalDateTime checkOutTime = LocalDateTime.of(checkOut, LocalTime.MAX);
            hotelBookingPage = bookingRepository.findAllCheckInAndCheckOutAndStatus(checkInTime, checkOutTime, status,
                    pageRequest);
        } else if (checkIn != null && checkOut != null && status == null) {
            LocalDateTime checkInTime = LocalDateTime.of(checkIn, LocalTime.MIN);
            LocalDateTime checkOutTime = LocalDateTime.of(checkOut, LocalTime.MAX);
            hotelBookingPage = bookingRepository.findAllCheckInAndCheckOutAndStatus(checkInTime, checkOutTime, null,
                    pageRequest);
        } else if (checkIn != null && checkOut == null) {
            hotelBookingPage = bookingRepository.findByCheckIn(checkIn, status, pageRequest);
        } else if (checkIn == null && checkOut != null) {
            hotelBookingPage = bookingRepository.findByCheckOut(checkOut, status, pageRequest);
        } else if (checkIn == null && checkOut == null && status != null) {
            hotelBookingPage = bookingRepository.findByStatusId(status, pageRequest);
        } else {
            hotelBookingPage = bookingRepository.findAllByValidBooking(pageRequest);
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
    public Page<ViewBookingDTO> searchCheckInAndCheckOut(LocalDate checkIn, LocalDate checkOut, Integer status,
                                                         int page, int size) {

        PageRequest pageRequest = PageRequest.of(page, size);
        Page<HotelBooking> hotelBookingPage = null;
        if (checkIn != null && checkOut != null) {
            LocalDateTime checkInTime = LocalDateTime.of(checkIn, LocalTime.MIN);
            LocalDateTime checkOutTime = LocalDateTime.of(checkOut, LocalTime.MAX);
            hotelBookingPage = bookingRepository.findAllCheckInAndCheckOutAndStatus(checkInTime, checkOutTime, status,
                    pageRequest);
        } else if (checkIn != null && checkOut == null) {
            hotelBookingPage = bookingRepository.findByCheckIn(checkIn, status, pageRequest);
        } else if (checkIn == null && checkOut != null) {
            hotelBookingPage = bookingRepository.findByCheckOut(checkOut, status, pageRequest);
        } else {
            hotelBookingPage = bookingRepository.findByStatusId(status, pageRequest);
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
    public Boolean checkIn(SaveCheckinDTO checkinDTO) {
        Long hotelBookingId = checkinDTO.getHotelBookingId();
        HotelBooking hotelBooking = bookingRepository.findByHotelBookingId(hotelBookingId);
        if (!BookingValidator.isValidToCheckIn(hotelBooking.getCheckIn())) {
            return false;
        }
        if (!hotelBooking.getStatusId().equals(1L)) {
            return false;
        }
        List<BookingRoomDetails> hotelBookingDetails = bookingRoomDetailsRepository
                .getAllByHotelBookingId(hotelBookingId);
        Map<Long, SaveCheckinDetailDTO> newCheckinDetailMap = checkinDTO.getSaveCheckinDetailDTOS().stream()
                .collect(Collectors.toMap(SaveCheckinDetailDTO::getBookingRoomId, Function.identity()));

        // Update new room user choose when checkIn
        List<BookingRoomDetails> updateBookingRooms = hotelBookingDetails.stream().map(hotelBookingDetail -> {
            SaveCheckinDetailDTO chosenRoom = newCheckinDetailMap.get(hotelBookingDetail.getBookingRoomId());
            Long bookingRoomId = hotelBookingDetail.getRoomId();
            Long newBookingRoomId = chosenRoom.getRoomID();
            if (!bookingRoomId.equals(newBookingRoomId)) {
                hotelBookingDetail.setRoomId(chosenRoom.getRoomID());
            }
            return hotelBookingDetail;
        }).collect(Collectors.toList());

        List<Long> allBookedRoomIds = updateBookingRooms.stream().map(BookingRoomDetails::getRoomId)
                .collect(Collectors.toList());

        List<Room> allBookedRooms = roomRepository.findAllById(allBookedRoomIds);
        Instant bookCheckInTime = hotelBooking.getCheckIn().truncatedTo(ChronoUnit.DAYS);
        Instant actualCheckInTime = Instant.now().truncatedTo(ChronoUnit.DAYS);

        if (!bookCheckInTime.equals(actualCheckInTime)) {
            boolean isExistNotReadyRoom = BookingValidator.isExistNotReadyRoom(allBookedRooms);
            if (isExistNotReadyRoom) {
                return false;
            }
        }
        // Change status room / booking
        hotelBooking.setStatusId(2L);
        hotelBooking.setCheckInActual(Instant.now());

        for (Room room : allBookedRooms) {
            room.setRoomStatusId(1L);
        }
        bookingRepository.save(hotelBooking);
        bookingRoomDetailsRepository.saveAll(updateBookingRooms);
        roomRepository.saveAll(allBookedRooms);
        return true;
    }

    public static String getCheckoutContent(HotelBooking hotelBooking) {
        if (hotelBooking.getRefundPrice().compareTo(BigDecimal.ZERO) > 0) {
            return TransactionMessage.REFUND.getMessage();
        } else {
            return TransactionMessage.PAY.getMessage();
        }
    }

    public void sendBookingRequest(Long hotelBookingId) throws ResetExceptionHandler {
        BookingDetailsDTO bookingDetailsDTO = bookingRoomDetailsService.getBookingDetailsByHotelBooking(hotelBookingId);

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
}