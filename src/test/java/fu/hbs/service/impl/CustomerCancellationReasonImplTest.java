package fu.hbs.service.impl;

import fu.hbs.entities.CustomerCancellationReasons;
import fu.hbs.repository.CustomerCancellationReasonRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerCancellationReasonImplTest {

    @Mock
    private CustomerCancellationReasonRepository customerCancellationReasonRepository;

    @InjectMocks
    private CustomerCancellationReasonImpl customerCancellationReasonService;

    @Test
    @DisplayName("FindAllCustomerCancellationReasonsUTCID01")
    void testFindAll() {
        // Given
        CustomerCancellationReasons reason1 = new CustomerCancellationReasons(1L, "Reason 1");
        CustomerCancellationReasons reason2 = new CustomerCancellationReasons(2L, "Reason 2");

        List<CustomerCancellationReasons> reasonsList = Arrays.asList(reason1, reason2);

        // Mock the repository behavior
        when(customerCancellationReasonRepository.findAll()).thenReturn(reasonsList);

        // When
        List<CustomerCancellationReasons> result = customerCancellationReasonService.findAllCancellationReasons();

        // Then
        // Verify that the repository method was called once
        verify(customerCancellationReasonRepository, times(1)).findAll();

        // Verify that the result matches the expected list
        assertEquals(reasonsList, result);
    }
}
