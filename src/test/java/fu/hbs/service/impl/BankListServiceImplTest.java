package fu.hbs.service.impl;

import fu.hbs.entities.BankList;
import fu.hbs.repository.BankListRepository;
import fu.hbs.service.impl.BankListServiceImpl;
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
class BankListServiceImplTest {

    @Mock
    private BankListRepository bankListRepository;

    @InjectMocks
    private BankListServiceImpl bankListService;

    @Test
    void testGetAll() {
        // Given
        BankList bank1 = new BankList(1L, "Bank 1");
        BankList bank2 = new BankList(2L, "Bank 2");

        List<BankList> expectedBankList = Arrays.asList(bank1, bank2);

        // Mock the repository behavior
        when(bankListRepository.findAll()).thenReturn(expectedBankList);

        // When
        List<BankList> result = bankListService.getAllBank();

        // Then
        // Verify that the repository method was called
        verify(bankListRepository, times(1)).findAll();

        // Verify that the result matches the expected BankList
        assertEquals(expectedBankList, result);
    }
}
