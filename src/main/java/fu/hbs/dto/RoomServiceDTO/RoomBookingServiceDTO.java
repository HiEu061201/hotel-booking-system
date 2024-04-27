package fu.hbs.dto.RoomServiceDTO;

import fu.hbs.entities.RoomBookingServices;
import fu.hbs.entities.RoomService;
import fu.hbs.utils.BookingUtil;
import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoomBookingServiceDTO {
    private Long serviceId;
    private String serviceName;
    private BigDecimal servicePrice = BigDecimal.ZERO;
    private String serviceDes;
    private String serviceNote;
    private Boolean status;
    private String serviceImage;
    private int quantity;
    private BigDecimal totalPrice = BigDecimal.ZERO;

    public static RoomBookingServiceDTO valueOf(RoomService roomService, int quantity) {
        return new RoomBookingServiceDTO(roomService.getServiceId(),
                roomService.getServiceName(),
                roomService.getServicePrice(),
                roomService.getServiceDes(),
                roomService.getServiceNote(),
                roomService.getStatus(),
                roomService.getServiceImage(),
                quantity, BookingUtil.calculateTotalPriceOfUseService(roomService, quantity));
    }
}