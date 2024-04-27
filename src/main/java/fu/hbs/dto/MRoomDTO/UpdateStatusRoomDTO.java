package fu.hbs.dto.MRoomDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class UpdateStatusRoomDTO {
    private Long roomId;
    private int status;
}
