package fu.hbs.dto.SaleManagerController;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.sql.Date;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class UpdateNewsDTO {
	private Long newId;
	private Long userId;
	private String title;
	private String descriptions;
	private Boolean status;
    private Date date;
    private String image;
}