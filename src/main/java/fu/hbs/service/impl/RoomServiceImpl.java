package fu.hbs.service.impl;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import fu.hbs.dto.AccountManagerDTO.RevenueDTO;
import fu.hbs.dto.AccountManagerDTO.RoomManagementItemDTO;
import fu.hbs.dto.AccountManagerDTO.RoomStatusTodayDTO;
import fu.hbs.dto.HotelBookingDTO.SearchingResultRoomDTO;
import fu.hbs.dto.MRoomDTO.ViewRoomDTO;
import fu.hbs.dto.SaleManagerController.UpdateServiceDTO;
import fu.hbs.dto.ServiceDTO.ViewServiceDTO;
import fu.hbs.entities.BookingRoomDetails;
import fu.hbs.entities.CategoryRoomPrice;
import fu.hbs.entities.HotelBooking;
import fu.hbs.entities.Room;
import fu.hbs.entities.RoomCategories;
import fu.hbs.entities.RoomStatus;
import fu.hbs.repository.BookingRoomDetailsRepository;
import fu.hbs.repository.CategoryRoomPriceRepository;
import fu.hbs.repository.HotelBookingRepository;
import fu.hbs.repository.RoomCategoriesRepository;
import fu.hbs.repository.RoomRepository;
import fu.hbs.repository.RoomServiceRepository;
import fu.hbs.repository.RoomStatusRepository;
import fu.hbs.repository.TransactionsRepository;
import fu.hbs.service.interfaces.RoomService;
import fu.hbs.utils.BookingUtil;

@Service
public class RoomServiceImpl implements RoomService {
    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private RoomServiceRepository roomServiceRepository;
    @Autowired
    private RoomCategoriesRepository roomCategoriesRepository;
    @Autowired
    private RoomStatusRepository roomStatusRepository;
    @Autowired
    private CategoryRoomPriceRepository categoryRoomPriceRepository;
    @Autowired
    private HotelBookingRepository hotelBookingRepository;
    @Autowired
    private TransactionsRepository transactionsRepository;
    @Autowired
    private BookingRoomDetailsRepository bookingRoomDetailsRepository;

    @Override

    public Page<ViewServiceDTO> findByNameAndStatus(String name, Integer status, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);

        Page<fu.hbs.entities.RoomService> roomServicePage = (Page<fu.hbs.entities.RoomService>) roomServiceRepository
                .searchByNameAndStatus(name, status, pageRequest);

        return roomServicePage.map(roomService -> {
            ViewServiceDTO viewServiceDTO = new ViewServiceDTO();
            viewServiceDTO.setRoomService(roomService);
            return viewServiceDTO;
        });

    }

    @Override
    public Page<ViewRoomDTO> findByFloorAndStatus(Integer floor, Integer status, int page, int size) {

        PageRequest pageRequest = PageRequest.of(page, size);

        Page<Room> roomPage = roomRepository.findFloorAndStatusId(floor, status, pageRequest);
        return roomPage.map(room -> {
            RoomCategories roomCategories = roomCategoriesRepository.findByRoomCategoryId(room.getRoomCategoryId());
            CategoryRoomPrice categoryRoomPrice = categoryRoomPriceRepository
                    .findByRoomCategoryIdAndFlag(room.getRoomCategoryId(), 1);
            RoomStatus roomStatus = roomStatusRepository.findByRoomStatusId(room.getRoomStatusId());
            ViewRoomDTO viewRoomDTO = new ViewRoomDTO();
            viewRoomDTO.setRoom(room);
            viewRoomDTO.setRoomCategories(roomCategories);
            viewRoomDTO.setRoomStatus(roomStatus);
            viewRoomDTO.setCategoryRoomPrice(categoryRoomPrice);
            return viewRoomDTO;
        });
    }

    @Override
    public RevenueDTO viewRevenue(String year) {
        RevenueDTO dto = new RevenueDTO();
        RoomStatusTodayDTO roomStatusTodayDTO = new RoomStatusTodayDTO();
//        LocalDate date = LocalDate.now();
//        year = "" + date.getYear();

        // 1 not check in, 2 checked in, 3 checked out, 4 canceled
        dto.setRoomCheckIn(hotelBookingRepository.countBookingTodayByStatus(2L));
        dto.setRoomCheckOut(hotelBookingRepository.countBookingTodayByStatus(3L));
        dto.setRoomCanceled(hotelBookingRepository.countBookingTodayByStatus(4L));

        // 2 percent
        roomStatusTodayDTO.setTotalRoom(roomRepository.countAll());
        // phòng ở
        roomStatusTodayDTO.setOccupiedRooms(roomRepository.countAllByRoomStatusId(1L));
        // số phòng trống
        roomStatusTodayDTO.setVacantRooms(roomRepository.countAllByRoomStatusId(2L));

        // Tính toán phần trăm lấp đầy và trống
        if (roomStatusTodayDTO.getTotalRoom() > 0) {
            // Tính toán phần trăm lấp đầy và trống
            double occupancyPercentage = ((double) roomStatusTodayDTO.getOccupiedRooms()
                    / roomStatusTodayDTO.getTotalRoom()) * 100; // lấp đầy
            double vacancyPercentage = 100 - occupancyPercentage;

            roomStatusTodayDTO.setOccupancyPercentage(occupancyPercentage);
            roomStatusTodayDTO.setVacancyPercentage(vacancyPercentage);

        } else {
            // Nếu không có phòng, đặt phần trăm lấp đầy và trống thành 0
            roomStatusTodayDTO.setOccupancyPercentage(0);
            roomStatusTodayDTO.setVacancyPercentage(0);
        }

        dto.setRoomStatusTodayDTO(roomStatusTodayDTO);
        // revenue
        Float[] revenue = new Float[12];
        for (int i = 1; i < 13; i++) {
            revenue[i - 1] = transactionsRepository.getRevenueByYearAndMonth(year, "" + i);
        }
        dto.setRevenue(revenue);

        // room categories

        List<HotelBooking> hotelBookingList = hotelBookingRepository.getAllTodayBooking();

        List<Long> hotelBookingIdList = hotelBookingList.stream().map(HotelBooking::getHotelBookingId).toList();

        List<BookingRoomDetails> bookingRoomList = bookingRoomDetailsRepository
                .findByHotelBookingIdIn(hotelBookingIdList);

        // lấy ra số roomId có trong bookingDetails
        List<Long> roomIdList = bookingRoomList.stream().map(BookingRoomDetails::getRoomId).toList();

        List<Room> roomList = new ArrayList<>();
        for (int i = 0; i < roomIdList.size(); i++) {
            Room room = roomRepository.findRoomByRoomId(roomIdList.get(i));
            roomList.add(room);
        }

        List<RoomCategories> addedCategories = new ArrayList<>();
        // Group rooms by room category
        Map<Long, List<Room>> groupedRooms = roomList.stream().collect(Collectors.groupingBy(Room::getRoomCategoryId));
        for (Map.Entry<Long, List<Room>> entry : groupedRooms.entrySet()) {
            Long categoryId = entry.getKey();
            List<Room> roomsWithSameCategory = entry.getValue();
            addedCategories.add(roomCategoriesRepository.findDistinctByRoomCategoryId(categoryId));
        }

        List<RoomManagementItemDTO> roomManagementItemDTOList = new ArrayList<>();

        // Lấy ra số phòng đang ở và đã đặt và còn trống
        for (int i = 0; i < addedCategories.size(); i++) {
            RoomManagementItemDTO roomManagementItemDTO = new RoomManagementItemDTO();
            Long k = addedCategories.get(i).getRoomCategoryId();
            // còn trống
            List<Room> roomListAvailable = roomRepository.findAvailableRoomsByCategoryIdToday(k);
            System.out.println("con trống" + roomListAvailable);
            // đã ở
            List<Room> roomListOccupied = roomRepository.findOccupiedByCategoryIdToday(k, 2);
            System.out.println("đã ở" + roomListOccupied);
            // đã đặt
            List<Room> roomListBooked = roomRepository.findOccupiedByCategoryIdToday(k, 1);
            System.out.println("đã đặt" + roomListBooked);
            // tổng số phòng
            int countTotalRoom = roomRepository.countRoomByRoomCategoryId(k);
            roomManagementItemDTO.setUsing(roomListOccupied.size());
            roomManagementItemDTO.setBooked(roomListBooked.size());
            roomManagementItemDTO.setRoomCategories(addedCategories.get(i));
            roomManagementItemDTO.setEmpty(roomListAvailable.size());

            roomManagementItemDTOList.add(roomManagementItemDTO);

        }
        dto.setCategories(roomManagementItemDTOList);
        return dto;
    }

    @Override
    public List<SearchingResultRoomDTO> getSearchingRoomForBooking(Long roomCategoryId, LocalDate checkIn,
                                                                   LocalDate checkOut) {
        List<Room> rooms;
        if (roomCategoryId == -1) {
            rooms = roomRepository.findAvailableRoomsByDate(checkIn, checkOut);
        } else {
            rooms = roomRepository.findAvailableRoomsByCategoryId(roomCategoryId, checkIn, checkOut);
        }
        Instant now = Instant.now();
        LocalDate localDate = LocalDateTime.ofInstant(now, ZoneId.systemDefault()).toLocalDate();
        if (localDate.equals(checkIn)) {
            rooms = rooms.stream().filter(room -> room.getRoomStatusId().equals(2L)).collect(Collectors.toList());
        }

        Map<Long, List<Room>> roomMapWithCategoryIdAsKey = rooms.stream()
                .collect(Collectors.groupingBy(Room::getRoomCategoryId));

        List<Room> roomGroupByCategory = roomMapWithCategoryIdAsKey.values().stream()
                .map(roomsList -> roomsList.stream().findFirst().orElse(null)).filter(Objects::nonNull)
                .collect(Collectors.toList());

        Map<Long, RoomCategories> roomCategoryAsMap = BookingUtil.getAllRoomCategoryAsMap();
        List<SearchingResultRoomDTO> searchingResultRoomDTOList = new ArrayList<>();
        for (Room room : roomGroupByCategory) {
            List<Room> allRoomsByIdCategory = roomRepository.findByRoomCategoryId(room.getRoomCategoryId()); // All room
            // by
            // category
            List<Room> availableRoomsByCategoryId = roomRepository
                    .findAvailableRoomsByCategoryId(room.getRoomCategoryId(), checkIn, checkOut);

            if (localDate.equals(checkIn)) {
                availableRoomsByCategoryId = availableRoomsByCategoryId.stream()
                        .filter(roomCategory -> roomCategory.getRoomStatusId().longValue() == 2)
                        .collect(Collectors.toList());
            }

            RoomCategories category = roomCategoryAsMap.get(room.getRoomCategoryId());
            CategoryRoomPrice categoryRoomPrice = categoryRoomPriceRepository
                    .findByRoomCategoryIdAndFlag(category.getRoomCategoryId(), 1);
            SearchingResultRoomDTO searchingRoomDTO = new SearchingResultRoomDTO(availableRoomsByCategoryId.size(),
                    categoryRoomPrice.getPrice(), category.getRoomCategoryId(), category.getRoomCategoryName(),
                    category.getDescription(), category.getSquare(), category.getNumberPerson(), category.getImage(),
                    room.getBedSize(), allRoomsByIdCategory.size() - availableRoomsByCategoryId.size());
            searchingResultRoomDTOList.add(searchingRoomDTO);

        }
        return searchingResultRoomDTOList;
    }

    public void updateRoomService(UpdateServiceDTO updateServiceDTO) {

        fu.hbs.entities.RoomService roomService = roomServiceRepository
                .findByServiceId(updateServiceDTO.getServiceId());
        if (roomService != null) {
            roomService.setServiceDes(updateServiceDTO.getServiceDes());
            roomService.setServiceName(updateServiceDTO.getServiceName());
            roomService.setServicePrice(updateServiceDTO.getServicePrice());
            roomService.setServiceImage(updateServiceDTO.getServiceImage());
            roomService.setStatus(updateServiceDTO.getStatus());
            roomServiceRepository.save(roomService);
        }
    }

    @Override
    public fu.hbs.entities.RoomService findByServiceId(Long id) {
        return roomServiceRepository.findByServiceId(id);
    }

    @Override
    public void createRoomService(UpdateServiceDTO dto) {
        fu.hbs.entities.RoomService roomService = new fu.hbs.entities.RoomService();
        roomService.setServiceNote(dto.getServiceNote());
        roomService.setServiceDes(dto.getServiceDes());
        roomService.setServicePrice(dto.getServicePrice());
        roomService.setServiceName(dto.getServiceName());
        roomService.setServiceImage(dto.getServiceImage());
        roomService.setStatus(dto.getStatus());

        roomServiceRepository.save(roomService);

    }

    @Override
    public void updateRoomByStatusId(int status, Long roomId) {
        roomRepository.updateRoomByStatusId(status, roomId);
    }

    @Override
    public Room findByRoomId(Long roomId) {
        return roomRepository.findRoomByRoomId(roomId);
    }

    public List<Room> findAvailableRoom(Long roomCategoryId, LocalDate checkIn, LocalDate checkOut) {
        List<Room> rooms = roomRepository.findAvailableRoomsByCategoryId(roomCategoryId, checkIn, checkOut);
        return rooms.stream().filter(room -> room.getRoomStatusId().equals(2L)).collect(Collectors.toList());
    }

    @Override
    public Room findRoomById(Long id) {
        return roomRepository.findById(id).orElse(null);
    }

    @Override
    public List<Room> getAllRoom() {
        return roomRepository.findAll();
    }

}
