package fu.hbs.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class VNPayServiceTest {

	@Test
	@DisplayName("Test createOrder method")
	void testCreateOrder() {
		// Given
		VNPayService vnPayService = new VNPayService();
		BigDecimal total = BigDecimal.valueOf(100);
		String orderInfo = "Test Order";
		String baseURL = "http://example.com";
		String detailURL = "/detail";
		// When
		String paymentUrl = vnPayService.createOrder(total, orderInfo, baseURL, detailURL);

		assertEquals(true, !paymentUrl.isEmpty());
	}
}