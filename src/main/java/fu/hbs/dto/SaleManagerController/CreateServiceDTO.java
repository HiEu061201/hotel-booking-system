package fu.hbs.dto.SaleManagerController;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class CreateServiceDTO {
    private Long serviceId;
    private String serviceName;
    private BigDecimal servicePrice;
    private String serviceDes;
    private String serviceNote;
    private int status;
    private MultipartFile serviceImage;
}
