package fu.hbs.dto.ServiceDTO;

import fu.hbs.entities.RoomService;
import fu.hbs.entities.ServicePriceHistory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ViewServiceDTO {
    private RoomService roomService;
    private ServicePriceHistory servicePriceHistory;
}
