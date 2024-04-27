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
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import fu.hbs.entities.*;
import fu.hbs.repository.*;
import fu.hbs.utils.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import fu.hbs.dto.CategoryRoomPriceDTO.DateInfoCategoryRoomPriceDTO;
import fu.hbs.dto.HotelBookingDTO.BookingDetailsDTO;
import fu.hbs.dto.HotelBookingDTO.RoomInDetailsDTO;
import fu.hbs.dto.HotelBookingDTO.UserInBookingDetailsDTO;
import fu.hbs.service.interfaces.BookingRoomDetailsService;
import fu.hbs.utils.BookingDayUtils;

@Service
public class BookingRoomDetailsImpl implements BookingRoomDetailsService {

    @Autowired
    private BookingRoomDetailsRepository bookingRoomDetailsRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private HotelBookingRepository hotelBookingRepository;
    @Autowired
    private RoomRepository roomRepository;
    @Autowired
    private RoomCategoriesRepository roomCategoriesRepository;
    @Autowired
    private CategoryRoomPriceRepository categoryRoomPriceRepository;
    @Autowired
    private RefundAccountRepository refundAccountRepository;
    @Autowired
    private BankListRepository bankListRepository;
    @Autowired
    private CustomerCancellationRepository customerCancellationRepository;
    @Autowired
    private TransactionsRepository transactionsRepository;
    @Autowired
    private PaymentTypeRepository paymentTypeRepository;
    @Autowired
    private TransactionsTyPeRepository transactionsTyPeRepository;
    @Autowired
    private HotelBookingServiceRepository hotelBookingServiceRepository;
    @Autowired
    private RoomServiceRepository roomServiceRepository;
    @Autowired
    private BookingDayUtils bookingDayUtils;

    @Override
    public BookingRoomDetails save(BookingRoomDetails bookingRoomDetails) {

        return bookingRoomDetailsRepository.save(bookingRoomDetails);
    }

    @Override
    public BookingDetailsDTO getBookingDetails(Authentication authentication, Long hotelBookingId) {
        BookingDetailsDTO dto = new BookingDetailsDTO();
        CustomerCancellation cancellation = new CustomerCancellation();

        // User's details
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userRepository.getUserByEmail(userDetails.getUsername());
        dto.setUser(user);

        // Hotel booking
        HotelBooking hotelBooking = hotelBookingRepository.findByHotelBookingId(hotelBookingId);
        List<fu.hbs.entities.RoomService> roomServiceList = new ArrayList<>();
        List<fu.hbs.entities.HotelBookingService> hotelBookingServiceList = hotelBookingServiceRepository.getAllByHotelBookingId(hotelBookingId);
        for (int i = 0; i < hotelBookingServiceList.size(); i++) {
            fu.hbs.entities.RoomService service = roomServiceRepository.findByServiceId(hotelBookingServiceList.get(i).getServiceId());
            roomServiceList.add(service);
        }
        dto.setHotelBookingServiceList(hotelBookingServiceList);
        dto.setRoomServiceList(roomServiceList);

        cancellation = customerCancellationRepository.findCustomerCancellationNewHotelBookingId(hotelBookingId);
        if (cancellation != null) {
            dto.setCustomerCancellation(cancellation);
        }

        // Transactions
        List<Transactions> transactionsList = transactionsRepository.findByHotelBookingId(hotelBookingId);
        for (int i = 0; i < transactionsList.size(); i++) {
            dto.setTransactions(transactionsList.get(i));
            // TransactiosType
            TransactionsType transactionsType = transactionsTyPeRepository.findByTransactionsTypeId(transactionsList.get(i).getTransactionsTypeId());
            dto.setTransactionsType(transactionsType);

            // PaymentType
            PaymentType paymentType = paymentTypeRepository.findByPaymentId(transactionsList.get(i).getPaymentId());
            dto.setPaymentType(paymentType);
        }
        dto.setTransactionsList(transactionsList);

        dto.setCheckIn(DateUtil.formatInstantToPattern(hotelBooking.getCheckIn()));
        dto.setCheckOut(DateUtil.formatInstantToPattern(hotelBooking.getCheckOut()));
        dto.setCheckInActual(DateUtil.formatInstantToPattern(hotelBooking.getCheckInActual()));
        dto.setCheckOutActual(DateUtil.formatInstantToPattern(hotelBooking.getCheckOutActual()));

        Instant checkIn = hotelBooking.getCheckIn();
        Instant checkOut = hotelBooking.getCheckOut();

        dto.setHotelBooking(hotelBooking);
        // Booking details
        List<BookingRoomDetails> bookingRoomDetails = bookingRoomDetailsRepository
                .getAllByHotelBookingId(hotelBookingId);
        List<RoomInDetailsDTO> roomInDetailsDTOS = new ArrayList<>();

        Room room = new Room();
        RoomCategories roomCategories = new RoomCategories();
        CategoryRoomPrice categoryRoomPrice = new CategoryRoomPrice();
        List<Room> rooms = new ArrayList<>();
        int count = 0;
        // distinct Category
        Set<RoomCategories> distinctRoomCategories = new HashSet<>();

        for (BookingRoomDetails item : bookingRoomDetails) {
            room = roomRepository.findById(item.getRoomId()).get();
            rooms.add(room);
            roomCategories = roomCategoriesRepository.findByRoomCategoryId(item.getRoomCategoryId());
            distinctRoomCategories.add(roomCategories);
        }

        List<CategoryRoomPrice> categoryRoomPrices = new ArrayList<>();

        for (RoomCategories roomCategory : distinctRoomCategories) {
            roomInDetailsDTOS.add(new RoomInDetailsDTO(room, roomCategory,
                    categoryRoomPriceRepository.getCategoryId(roomCategory.getRoomCategoryId())));
        }

        // Group rooms by room category
        Map<Long, List<Room>> groupedRooms = rooms.stream().collect(Collectors.groupingBy(Room::getRoomCategoryId));
        for (Map.Entry<Long, List<Room>> entry : groupedRooms.entrySet()) {
            Long categoryId = entry.getKey();
            List<Room> roomsWithSameCategory = entry.getValue();
        }
        // converst
        LocalDate localDateCheckIn = checkIn.atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate localDateCheckOut = checkOut.atZone(ZoneId.systemDefault()).toLocalDate();

        List<DateInfoCategoryRoomPriceDTO> dateInfoList = bookingDayUtils.processDateInfo(localDateCheckIn,
                localDateCheckOut);
        BigDecimal total_Price = BigDecimal.ZERO;
        Map<Long, BigDecimal> totalPriceByCategoryId = new HashMap<>();

        // tổng giá cho một CategoryRoomPrice theo Category
        for (int i = 0; i < roomInDetailsDTOS.size(); i++) {
            BigDecimal totalForCategory = bookingDayUtils
                    .calculateTotalForCategory(roomInDetailsDTOS.get(i).getCategoryRoomPrice(), dateInfoList);
            totalPriceByCategoryId.put(roomInDetailsDTOS.get(i).getCategoryRoomPrice().getRoomCategoryId(),
                    totalForCategory);
        }
        BigDecimal totalPrice2 = BigDecimal.ZERO;
        // CategoryRoomPrice * total Room
        for (Map.Entry<Long, List<Room>> entry : groupedRooms.entrySet()) {
            Long categoryId = entry.getKey();

            List<Room> roomsWithSameCategory = entry.getValue();

            if (totalPriceByCategoryId.containsKey(categoryId)) {

                BigDecimal totalPrice1 = totalPriceByCategoryId.get(categoryId);

                totalPrice2 = totalPrice1.multiply(BigDecimal.valueOf(roomsWithSameCategory.size()));
                total_Price = total_Price.add(totalPrice2);
            }
            dto.setRoomPrice(total_Price);
        }

        dto.setTotalPriceByCategoryId(totalPriceByCategoryId);
        dto.setGroupedRooms(groupedRooms);
        dto.setBookingRoomDetails(roomInDetailsDTOS);
        dto.setDateInfoList(dateInfoList);

        return dto;
    }

    @Override
    public BookingDetailsDTO getBookingDetailsByHotelBooking(Long hotelBookingId) {
        BookingDetailsDTO dto = new BookingDetailsDTO();
        CustomerCancellation cancellation = new CustomerCancellation();
        UserInBookingDetailsDTO userInBookingDetailsDTO = new UserInBookingDetailsDTO();
        // Hotel booking
        HotelBooking hotelBooking = hotelBookingRepository.findByHotelBookingId(hotelBookingId);

        User user = userRepository.findByUserId(hotelBooking.getUserId());

        if (user != null) {
            dto.setUser(user);
            userInBookingDetailsDTO.setUser(user);
        }

        cancellation = customerCancellationRepository.findCustomerCancellationNewHotelBookingId(hotelBookingId);
        if (cancellation != null) {
            dto.setCustomerCancellation(cancellation);
            userInBookingDetailsDTO.setCustomerCancellation(cancellation);
        }

        // Transactions
        Transactions transactions = transactionsRepository.findByHotelBookingIdNew(hotelBookingId);
        if (transactions != null) {
            dto.setTransactions(transactions);
            userInBookingDetailsDTO.setTransactions(transactions);

            // refundAccount
            RefundAccount refundAccount = refundAccountRepository.findByAccountId(cancellation.getAccountId());
            if (refundAccount != null) {
                userInBookingDetailsDTO.setRefundAccount(refundAccount);

                // BankList
                BankList bankList = bankListRepository.findByBankId(refundAccount.getBankId());
                if (bankList != null) {
                    userInBookingDetailsDTO.setBankList(bankList);

                    // PaymentType
                    PaymentType paymentType = paymentTypeRepository.findByPaymentId(transactions.getPaymentId());
                    if (paymentType != null) {
                        dto.setPaymentType(paymentType);
                        userInBookingDetailsDTO.setPaymentType(paymentType);
                    }

                }
            }
        }

        dto.setUserInBookingDetailsDTO(userInBookingDetailsDTO);
        dto.setCheckIn(DateUtil.formatInstantToPattern(hotelBooking.getCheckIn()));
        dto.setCheckOut(DateUtil.formatInstantToPattern(hotelBooking.getCheckOut()));
        dto.setCheckInActual(DateUtil.formatInstantToPattern(hotelBooking.getCheckInActual()));
        dto.setCheckOutActual(DateUtil.formatInstantToPattern(hotelBooking.getCheckInActual()));
        Instant checkIn = hotelBooking.getCheckIn();
        Instant checkOut = hotelBooking.getCheckOut();

        dto.setHotelBooking(hotelBooking);
        // Booking details
        List<BookingRoomDetails> bookingRoomDetails = bookingRoomDetailsRepository
                .getAllByHotelBookingId(hotelBookingId);
        List<RoomInDetailsDTO> roomInDetailsDTOS = new ArrayList<>();

        Room room = new Room();
        RoomCategories roomCategories = new RoomCategories();
        CategoryRoomPrice categoryRoomPrice = new CategoryRoomPrice();
        List<Room> rooms = new ArrayList<>();
        int count = 0;
        Set<RoomCategories> distinctRoomCategories = new HashSet<>();

        // Distinct RoomCategory
        for (BookingRoomDetails item : bookingRoomDetails) {
            room = roomRepository.findById(item.getRoomId()).get();
            rooms.add(room);
            roomCategories = roomCategoriesRepository.findByRoomCategoryId(item.getRoomCategoryId());
            distinctRoomCategories.add(roomCategories);
        }

        List<CategoryRoomPrice> categoryRoomPrices = new ArrayList<>();

        for (RoomCategories roomCategory : distinctRoomCategories) {
            roomInDetailsDTOS.add(new RoomInDetailsDTO(room, roomCategory,
                    categoryRoomPriceRepository.getCategoryId(roomCategory.getRoomCategoryId())));
        }

        // Group rooms by room category

        Map<Long, List<Room>> groupedRooms = rooms.stream().collect(Collectors.groupingBy(Room::getRoomCategoryId));
        for (Map.Entry<Long, List<Room>> entry : groupedRooms.entrySet()) {
            Long categoryId = entry.getKey();
            List<Room> roomsWithSameCategory = entry.getValue();
        }

        // converst
        LocalDate localDateCheckIn = checkIn.atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate localDateCheckOut = checkOut.atZone(ZoneId.systemDefault()).toLocalDate();

        List<DateInfoCategoryRoomPriceDTO> dateInfoList = bookingDayUtils.processDateInfo(localDateCheckIn,
                localDateCheckOut);
        BigDecimal total_Price = BigDecimal.ZERO;
        Map<Long, BigDecimal> totalPriceByCategoryId = new HashMap<>();

        // tổng giá  CategoryRoomPrice theo Category
        for (int i = 0; i < roomInDetailsDTOS.size(); i++) {
            BigDecimal totalForCategory = bookingDayUtils
                    .calculateTotalForCategory(roomInDetailsDTOS.get(i).getCategoryRoomPrice(), dateInfoList);
            totalPriceByCategoryId.put(roomInDetailsDTOS.get(i).getCategoryRoomPrice().getRoomCategoryId(),
                    totalForCategory);
        }

        // CategoryRoomPrice * total Room
        for (Map.Entry<Long, List<Room>> entry : groupedRooms.entrySet()) {
            Long categoryId = entry.getKey();
            List<Room> roomsWithSameCategory = entry.getValue();

            if (totalPriceByCategoryId.containsKey(categoryId)) {

                BigDecimal totalPrice1 = totalPriceByCategoryId.get(categoryId);

                BigDecimal totalPrice2 = totalPrice1.multiply(BigDecimal.valueOf(roomsWithSameCategory.size()));
                total_Price = total_Price.add(totalPrice2);
            }
            dto.setRoomPrice(total_Price);
        }
        dto.setTotalPriceByCategoryId(totalPriceByCategoryId);
        dto.setGroupedRooms(groupedRooms);
        dto.setHotelBooking(hotelBooking);
        dto.setBookingRoomDetails(roomInDetailsDTOS);
        dto.setDateInfoList(dateInfoList);

        return dto;
    }

    @Override
    public BookingDetailsDTO getBookingDetailsByHotelBookingIsValid(Long hotelBookingId) {
        BookingDetailsDTO dto = new BookingDetailsDTO();
        CustomerCancellation cancellation = new CustomerCancellation();
        UserInBookingDetailsDTO userInBookingDetailsDTO = new UserInBookingDetailsDTO();
        // Hotel booking
        HotelBooking hotelBooking = hotelBookingRepository.findByHotelBookingIdIsValid(hotelBookingId);

        User user = userRepository.findByUserId(hotelBooking.getUserId());

        if (user != null) {
            dto.setUser(user);
            userInBookingDetailsDTO.setUser(user);
        }

        cancellation = customerCancellationRepository.findCustomerCancellationNewHotelBookingId(hotelBookingId);
        if (cancellation != null) {
            dto.setCustomerCancellation(cancellation);
            userInBookingDetailsDTO.setCustomerCancellation(cancellation);
        }

        // Transactions
        Transactions transactions = transactionsRepository.findByHotelBookingIdNew(hotelBookingId);
        if (transactions != null) {
            dto.setTransactions(transactions);
            userInBookingDetailsDTO.setTransactions(transactions);

            // refundAccount
            RefundAccount refundAccount = refundAccountRepository.findByAccountId(cancellation.getAccountId());
            if (refundAccount != null) {
                userInBookingDetailsDTO.setRefundAccount(refundAccount);

                // BankList
                BankList bankList = bankListRepository.findByBankId(refundAccount.getBankId());
                if (bankList != null) {
                    userInBookingDetailsDTO.setBankList(bankList);

                    // PaymentType
                    PaymentType paymentType = paymentTypeRepository.findByPaymentId(transactions.getPaymentId());
                    if (paymentType != null) {
                        dto.setPaymentType(paymentType);
                        userInBookingDetailsDTO.setPaymentType(paymentType);
                    }

                }
            }
        }

        dto.setUserInBookingDetailsDTO(userInBookingDetailsDTO);
        dto.setCheckIn(DateUtil.formatInstantToPattern(hotelBooking.getCheckIn()));
        dto.setCheckOut(DateUtil.formatInstantToPattern(hotelBooking.getCheckOut()));
        dto.setCheckInActual(DateUtil.formatInstantToPattern(hotelBooking.getCheckInActual()));
        dto.setCheckOutActual(DateUtil.formatInstantToPattern(hotelBooking.getCheckInActual()));
        Instant checkIn = hotelBooking.getCheckIn();
        Instant checkOut = hotelBooking.getCheckOut();

        dto.setHotelBooking(hotelBooking);
        // Booking details
        List<BookingRoomDetails> bookingRoomDetails = bookingRoomDetailsRepository
                .getAllByHotelBookingId(hotelBookingId);
        List<RoomInDetailsDTO> roomInDetailsDTOS = new ArrayList<>();

        Room room = new Room();
        RoomCategories roomCategories = new RoomCategories();
        CategoryRoomPrice categoryRoomPrice = new CategoryRoomPrice();
        List<Room> rooms = new ArrayList<>();
        int count = 0;
        Set<RoomCategories> distinctRoomCategories = new HashSet<>();

        // Distinct RoomCategory
        for (BookingRoomDetails item : bookingRoomDetails) {
            room = roomRepository.findById(item.getRoomId()).get();
            rooms.add(room);
            roomCategories = roomCategoriesRepository.findByRoomCategoryId(item.getRoomCategoryId());
            distinctRoomCategories.add(roomCategories);
        }

        List<CategoryRoomPrice> categoryRoomPrices = new ArrayList<>();

        for (RoomCategories roomCategory : distinctRoomCategories) {
            roomInDetailsDTOS.add(new RoomInDetailsDTO(room, roomCategory,
                    categoryRoomPriceRepository.getCategoryId(roomCategory.getRoomCategoryId())));
        }

        // Group rooms by room category

        Map<Long, List<Room>> groupedRooms = rooms.stream().collect(Collectors.groupingBy(Room::getRoomCategoryId));
        for (Map.Entry<Long, List<Room>> entry : groupedRooms.entrySet()) {
            Long categoryId = entry.getKey();
            List<Room> roomsWithSameCategory = entry.getValue();
        }

        // converst
        LocalDate localDateCheckIn = checkIn.atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate localDateCheckOut = checkOut.atZone(ZoneId.systemDefault()).toLocalDate();

        List<DateInfoCategoryRoomPriceDTO> dateInfoList = bookingDayUtils.processDateInfo(localDateCheckIn,
                localDateCheckOut);
        BigDecimal total_Price = BigDecimal.ZERO;
        Map<Long, BigDecimal> totalPriceByCategoryId = new HashMap<>();

        // tổng giá  CategoryRoomPrice theo Category
        for (int i = 0; i < roomInDetailsDTOS.size(); i++) {
            BigDecimal totalForCategory = bookingDayUtils
                    .calculateTotalForCategory(roomInDetailsDTOS.get(i).getCategoryRoomPrice(), dateInfoList);
            totalPriceByCategoryId.put(roomInDetailsDTOS.get(i).getCategoryRoomPrice().getRoomCategoryId(),
                    totalForCategory);
        }

        // CategoryRoomPrice * total Room
        for (Map.Entry<Long, List<Room>> entry : groupedRooms.entrySet()) {
            Long categoryId = entry.getKey();
            List<Room> roomsWithSameCategory = entry.getValue();

            if (totalPriceByCategoryId.containsKey(categoryId)) {

                BigDecimal totalPrice1 = totalPriceByCategoryId.get(categoryId);

                BigDecimal totalPrice2 = totalPrice1.multiply(BigDecimal.valueOf(roomsWithSameCategory.size()));
                total_Price = total_Price.add(totalPrice2);
            }
            dto.setRoomPrice(total_Price);
        }
        dto.setTotalPriceByCategoryId(totalPriceByCategoryId);
        dto.setGroupedRooms(groupedRooms);
        dto.setHotelBooking(hotelBooking);
        dto.setBookingRoomDetails(roomInDetailsDTOS);
        dto.setDateInfoList(dateInfoList);

        return dto;
    }

    @Override
    public List<BookingRoomDetails> getBookingDetailsByHotelBookingId(Long hotelBookingId) {
        return bookingRoomDetailsRepository.getAllByHotelBookingId(hotelBookingId);
    }
}
