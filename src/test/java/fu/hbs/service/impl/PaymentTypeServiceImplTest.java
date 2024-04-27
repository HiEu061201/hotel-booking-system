package fu.hbs.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import fu.hbs.entities.PaymentType;
import fu.hbs.repository.PaymentTypeRepository;

@ExtendWith(MockitoExtension.class)
class PaymentTypeServiceImplTest {

    @Mock
    private PaymentTypeRepository paymentTypeRepository;

    @InjectMocks
    private PaymentTypeServiceImpl paymentTypeService;

    @Test
    @DisplayName("getPaymentTypeByIdUTCID01")
    void getPaymentTypeById_ExistingId_ShouldReturnPaymentType() {
        // Arrange
        Long paymentTypeId = 1L;
        PaymentType mockPaymentType = new PaymentType();
        mockPaymentType.setPaymentId(paymentTypeId);

        when(paymentTypeRepository.findById(paymentTypeId)).thenReturn(Optional.of(mockPaymentType));

        // Act
        PaymentType result = paymentTypeService.getPaymentTypeById(paymentTypeId);

        // Assert
        assertNotNull(result);
        assertEquals(paymentTypeId, result.getPaymentId());
    }

    @Test
    @DisplayName("getPaymentTypeByIdUTCID02")
    void getPaymentTypeById_NonExistingId_ShouldReturnNull() {
        // Arrange
        Long nonExistingPaymentTypeId = 999L;

        when(paymentTypeRepository.findById(nonExistingPaymentTypeId)).thenReturn(Optional.empty());

        // Act
        PaymentType result = paymentTypeService.getPaymentTypeById(nonExistingPaymentTypeId);

        // Assert
        assertNull(result);
    }


}
