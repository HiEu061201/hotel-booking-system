package fu.hbs.service.interfaces;

import fu.hbs.dto.AccountManagerDTO.RevenueDTO;
import fu.hbs.dto.SaleManagerController.UpdateServiceDTO;
import fu.hbs.dto.MRoomDTO.ViewRoomDTO;
import fu.hbs.dto.ServiceDTO.ViewServiceDTO;
import fu.hbs.entities.Room;
import org.springframework.data.domain.Page;

import fu.hbs.dto.HotelBookingDTO.SearchingResultRoomDTO;

import java.time.LocalDate;
import java.util.List;

public interface RoomService {
    Page<ViewServiceDTO> findByNameAndStatus(String name, Integer status, int page, int size);

    Page<ViewRoomDTO> findByFloorAndStatus(Integer floor, Integer status, int page, int size);

    RevenueDTO viewRevenue(String year);

    List<SearchingResultRoomDTO> getSearchingRoomForBooking(Long roomCategoryId, LocalDate checkIn, LocalDate checkOut);

    void updateRoomService(UpdateServiceDTO updateServiceDTO);

    fu.hbs.entities.RoomService findByServiceId(Long id);

    void createRoomService(UpdateServiceDTO dto);

    void updateRoomByStatusId(int status, Long roomId);

    Room findByRoomId(Long roomId);

    List<Room> findAvailableRoom(Long roomCategoryId, LocalDate checkIn, LocalDate checkOut);

    Room findRoomById(Long id);

    List<Room> getAllRoom();

}
