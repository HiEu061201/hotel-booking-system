package fu.hbs.service.impl;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import fu.hbs.dto.CancellationDTO.ViewCancellationDTO;
import fu.hbs.entities.CustomerCancellation;
import fu.hbs.entities.HotelBooking;
import fu.hbs.entities.User;
import fu.hbs.repository.BookingRoomDetailsRepository;
import fu.hbs.repository.CustomerCancellationRepository;
import fu.hbs.repository.HotelBookingRepository;
import fu.hbs.repository.RoomCategoriesRepository;
import fu.hbs.repository.UserRepository;
import fu.hbs.service.interfaces.CustomerCancellationService;

@Service
public class CustomerCancellationServiceImpl implements CustomerCancellationService {
    @Autowired
    CustomerCancellationRepository customerCancellationRepository;
    @Autowired
    HotelBookingRepository hotelBookingRepository;
    @Autowired
    RoomCategoriesRepository roomCategoriesRepository;
    @Autowired
    BookingRoomDetailsRepository bookingRoomDetailsRepository;
    @Autowired
    UserRepository userRepository;


    @Override
    public CustomerCancellation saveCustomerCancellation(CustomerCancellation customerCancellation) {
        return customerCancellationRepository.save(customerCancellation);
    }

    @Override
    public CustomerCancellation findCustomerCancellationNew(Long cancelId) {
        return customerCancellationRepository.findCustomerCancellationNewHotelBookingId(cancelId);
    }

    public Page<ViewCancellationDTO> getAllByStatusConfirm(LocalDate checkIn, Integer status, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<CustomerCancellation> customerCancellationsPage = null;
        if ("".equals(status) || status == null && checkIn == null) {
            customerCancellationsPage = customerCancellationRepository.findAllStatusConfirm(pageRequest);
        } else if (status != null && checkIn == null) {
            customerCancellationsPage = customerCancellationRepository.findByStatusConfirm(status, pageRequest);
        } else if (status == null && checkIn != null) {
            customerCancellationsPage = customerCancellationRepository.findByCheckInAndStatusConfirm(checkIn, null,
                    pageRequest);
        } else {
            customerCancellationsPage = customerCancellationRepository.findByCheckInAndStatusConfirm(checkIn, status,
                    pageRequest);
        }

        return customerCancellationsPage.map(customerCancellation1 -> {
            ViewCancellationDTO viewCancellationDTO = new ViewCancellationDTO();
            HotelBooking hotelBooking = hotelBookingRepository
                    .findByHotelBookingId(customerCancellation1.getHotelBookingId());
            User user = userRepository.findByUserId(hotelBooking.getUserId());

            viewCancellationDTO.setCustomerCancellation(customerCancellation1);
            viewCancellationDTO.setHotelBooking(hotelBooking);

            // Kiểm tra xem User có là null hay không trước khi gán
            if (user != null) {
                viewCancellationDTO.setUser(user);
            }

            return viewCancellationDTO;
        });
    }

    @Override
    public Page<ViewCancellationDTO> getAllByStatusProcess(LocalDate checkIn, Integer status, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<CustomerCancellation> customerCancellationsPage = null;
        if ("".equals(status) || status == null && checkIn == null) {
            customerCancellationsPage = customerCancellationRepository.findAllStatusProcess(pageRequest);
        } else if (status != null && checkIn == null) {
            customerCancellationsPage = customerCancellationRepository.findByStatusConfirm(status, pageRequest);
        } else if (status == null && checkIn != null) {
            customerCancellationsPage = customerCancellationRepository.findByCheckInAndStatusProcess(checkIn, null,
                    pageRequest);
        } else {
            customerCancellationsPage = customerCancellationRepository.findByCheckInAndStatusProcess(checkIn, status,
                    pageRequest);
        }

        return customerCancellationsPage.map(customerCancellation1 -> {
            ViewCancellationDTO viewCancellationDTO = new ViewCancellationDTO();
            HotelBooking hotelBooking = hotelBookingRepository
                    .findByHotelBookingId(customerCancellation1.getHotelBookingId());
            User user = userRepository.findByUserId(hotelBooking.getUserId());

            viewCancellationDTO.setCustomerCancellation(customerCancellation1);
            viewCancellationDTO.setHotelBooking(hotelBooking);

            // Kiểm tra xem User có là null hay không trước khi gán
            if (user != null) {
                viewCancellationDTO.setUser(user);
            }

            return viewCancellationDTO;
        });
    }


}
