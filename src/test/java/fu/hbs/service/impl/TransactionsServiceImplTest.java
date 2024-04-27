package fu.hbs.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import fu.hbs.dto.VnpayDTO.ViewPaymentDTO;
import fu.hbs.entities.HotelBooking;
import fu.hbs.entities.PaymentType;
import fu.hbs.entities.Transactions;
import fu.hbs.entities.TransactionsType;
import fu.hbs.entities.User;
import fu.hbs.repository.HotelBookingRepository;
import fu.hbs.repository.PaymentTypeRepository;
import fu.hbs.repository.TransactionsRepository;
import fu.hbs.repository.TransactionsTyPeRepository;
import fu.hbs.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class TransactionsServiceImplTest {

    @Mock
    private TransactionsRepository transactionsRepository;

    @Mock
    private PaymentTypeRepository paymentTypeRepository;

    @Mock
    private HotelBookingRepository hotelBookingRepository;

    @Mock
    private UserRepository userRepository;
    @Mock
    private TransactionsTyPeRepository transactionsTyPeRepository;
    @InjectMocks
    private TransactionsServiceImpl transactionsService;

    @Test
    @DisplayName("testSave")
    void testSave() {
        Transactions transactions = new Transactions();
        when(transactionsRepository.save(transactions)).thenReturn(transactions);

        Transactions savedTransactions = transactionsService.saveTransactions(transactions);

        assertEquals(transactions, savedTransactions);
        verify(transactionsRepository, times(1)).save(transactions);
    }

    @Test
    @DisplayName("testFindByCreateDateAndPaymentIdUTCID04")
    public void testFindByCreateDateAndPaymentIdWithCreateDateNotNull() {
        // Mock data
        LocalDate createDate = LocalDate.now();
        Long paymentId = null;
        int page = 0;
        int size = 10;

        PageRequest pageRequest = PageRequest.of(page, size);
        Page<Transactions> transactionsPage = new PageImpl<>(Collections.emptyList());

        // Mock repository responses
        when(transactionsRepository.findTransactionsCreatedDateAndPaymentId(createDate, null, pageRequest))
                .thenReturn(transactionsPage);

        // Call the method
        Page<ViewPaymentDTO> result = transactionsService.findByCreateDateAndPaymentId(createDate, paymentId, page,
                size);

        // Verify the result
        assertEquals(0, result.getContent().size());

        // Ensure that the repository is called with the correct parameters
        verify(transactionsRepository, times(1)).findTransactionsCreatedDateAndPaymentId(createDate, null, pageRequest);
    }

    @Test
    @DisplayName("testFindByCreateDateAndPaymentIdUTCID05")
    public void testFindByCreateDateAndPaymentIdWithPaymentIdNotNull() {
        // Mock data
        LocalDate createDate = null;
        Long paymentId = 123L;
        int page = 0;
        int size = 10;

        PageRequest pageRequest = PageRequest.of(page, size);
        Page<Transactions> transactionsPage = new PageImpl<>(Collections.emptyList());

        // Mock repository responses
        when(transactionsRepository.findTransactionsByPaymentId(paymentId, pageRequest)).thenReturn(transactionsPage);

        // Call the method
        Page<ViewPaymentDTO> result = transactionsService.findByCreateDateAndPaymentId(createDate, paymentId, page,
                size);

        // Verify the result
        assertEquals(0, result.getContent().size());

        // Ensure that the repository is called with the correct parameters
        verify(transactionsRepository, times(1)).findTransactionsByPaymentId(paymentId, pageRequest);
    }

    @Test
    @DisplayName("testFindByCreateDateAndPaymentIdUTCID06")
    public void testFindByCreateDateAndPaymentIdWithBothNull() {
        // Mock data
        LocalDate createDate = null;
        Long paymentId = null;
        int page = 0;
        int size = 10;

        PageRequest pageRequest = PageRequest.of(page, size);
        Page<Transactions> transactionsPage = new PageImpl<>(Collections.emptyList());

        // Mock repository responses
        when(transactionsRepository.findAll(pageRequest)).thenReturn(transactionsPage);

        // Call the method
        Page<ViewPaymentDTO> result = transactionsService.findByCreateDateAndPaymentId(createDate, paymentId, page,
                size);

        // Verify the result
        assertEquals(0, result.getContent().size());

        // Ensure that the repository is called with the correct parameters
        verify(transactionsRepository, times(1)).findAll(pageRequest);
    }

    @Test
    @DisplayName("testFindByCreateDateAndPaymentIdUTCID02")
    public void testFindByCreateDateAndPaymentId() {
        // Mock data
        Transactions transactions = new Transactions();
        transactions.setPaymentId(1L);
        transactions.setHotelBookingId(101L);
        transactions.setTransactionsTypeId(1L); // Set the TransactionsTypeId

        PaymentType paymentType = new PaymentType();
        paymentType.setPaymentId(1L);

        HotelBooking hotelBooking = new HotelBooking();
        hotelBooking.setHotelBookingId(101L);
        hotelBooking.setUserId(201L);

        User user = new User();
        user.setUserId(201L);

        int page = 0;
        int size = 10;
        PageRequest pageRequest = PageRequest.of(page, size);

        // Mock repository responses
        Page<Transactions> transactionsPage = new PageImpl<>(Collections.singletonList(transactions));
        Mockito.when(transactionsRepository.findAll(pageRequest)).thenReturn(transactionsPage);

        Mockito.when(paymentTypeRepository.findByPaymentId(1L)).thenReturn(paymentType);
        Mockito.when(hotelBookingRepository.findByHotelBookingId(101L)).thenReturn(hotelBooking);
        Mockito.when(userRepository.findByUserId(201L)).thenReturn(user);

        // Mock TransactionsType repository response
        TransactionsType transactionsType = new TransactionsType();
        transactionsType.setTransactionsTypeId(1L);

        // Adjust the stubbing to use anyLong() or eq() if necessary
        Mockito.when(transactionsTyPeRepository.findByTransactionsTypeId(anyLong())).thenReturn(transactionsType);

        // Call the method
        Page<ViewPaymentDTO> result = transactionsService.findByCreateDateAndPaymentId(null, null, 0, 10);

        // Verify the result
        assertEquals(1, result.getContent().size());
        ViewPaymentDTO viewPaymentDTO = result.getContent().get(0);
        assertEquals(1L, viewPaymentDTO.getTransactions().getPaymentId());
        assertEquals(101L, viewPaymentDTO.getHotelBooking().getHotelBookingId());
        assertEquals(201L, viewPaymentDTO.getUser().getUserId());
        assertEquals(1L, viewPaymentDTO.getPaymentType().getPaymentId());
        assertEquals(1L, viewPaymentDTO.getTransactionsType().getTransactionsTypeId());
    }

    @Test
    @DisplayName("testFindByCreateDateAndPaymentIdAndTypeIdWithCreateDateNotNull")
    public void testFindByCreateDateAndPaymentIdAndTypeIdWithCreateDateNotNull() {
        // Mock data
        LocalDate createDate = LocalDate.now();
        Long paymentId = null;
        Long typeId = 1L;
        int page = 0;
        int size = 10;

        PageRequest pageRequest = PageRequest.of(page, size);
        Page<Transactions> transactionsPage = new PageImpl<>(Collections.emptyList());

        // Mock repository responses
        when(transactionsRepository.findTransactionsCreatedDateAndPaymentIdAndTypeId(createDate, null, typeId,
                pageRequest)).thenReturn(transactionsPage);

        // Call the method
        Page<ViewPaymentDTO> result = transactionsService.findByCreateDateAndPaymentIdAndTypeId(createDate, paymentId,
                typeId, page, size);

        // Verify the result
        assertEquals(0, result.getContent().size());

        // Ensure that the repository is called with the correct parameters
        verify(transactionsRepository, times(1)).findTransactionsCreatedDateAndPaymentIdAndTypeId(createDate, null,
                typeId, pageRequest);
    }

    @Test
    @DisplayName("testFindByCreateDateAndPaymentIdAndTypeIdWithPaymentIdNotNull")
    public void testFindByCreateDateAndPaymentIdAndTypeIdWithPaymentIdNotNull() {
        // Mock data
        LocalDate createDate = null;
        Long paymentId = 123L;
        Long typeId = 1L;
        int page = 0;
        int size = 10;

        PageRequest pageRequest = PageRequest.of(page, size);
        Page<Transactions> transactionsPage = new PageImpl<>(Collections.emptyList());

        // Mock repository responses
        when(transactionsRepository.findTransactionsByTransactionsTypeIdAndPaymentId(typeId, paymentId, pageRequest))
                .thenReturn(transactionsPage);

        // Call the method
        Page<ViewPaymentDTO> result = transactionsService.findByCreateDateAndPaymentIdAndTypeId(createDate, paymentId,
                typeId, page, size);

        // Verify the result
        assertEquals(0, result.getContent().size());

        // Ensure that the repository is called with the correct parameters
        verify(transactionsRepository, times(1)).findTransactionsByTransactionsTypeIdAndPaymentId(typeId, paymentId,
                pageRequest);
    }

    @Test
    @DisplayName("testFindByCreateDateAndPaymentIdAndTypeIdWithBothNull")
    public void testFindByCreateDateAndPaymentIdAndTypeIdWithBothNull() {
        // Mock data
        LocalDate createDate = null;
        Long paymentId = null;
        Long typeId = null;
        int page = 0;
        int size = 10;

        PageRequest pageRequest = PageRequest.of(page, size);
        Page<Transactions> transactionsPage = new PageImpl<>(Collections.emptyList());

        // Mock repository responses
        when(transactionsRepository.findAll(pageRequest)).thenReturn(transactionsPage);

        // Call the method
        Page<ViewPaymentDTO> result = transactionsService.findByCreateDateAndPaymentIdAndTypeId(createDate, paymentId,
                typeId, page, size);

        // Verify the result
        assertEquals(0, result.getContent().size());

        // Ensure that the repository is called with the correct parameters
        verify(transactionsRepository, times(1)).findAll(pageRequest);
    }

    @Test
    @DisplayName("testFindByCreateDateAndPaymentIdAndTypeId")
    public void testFindByCreateDateAndPaymentIdAndTypeId() {
        // Mock data
        LocalDate createDate = LocalDate.now();
        Long paymentId = 1L;
        Long typeId = 2L;
        int page = 0;
        int size = 10;

        Transactions transactions = new Transactions();
        transactions.setPaymentId(paymentId);
        transactions.setHotelBookingId(101L);
        transactions.setTransactionsTypeId(typeId);

        PaymentType paymentType = new PaymentType();
        paymentType.setPaymentId(paymentId);

        HotelBooking hotelBooking = new HotelBooking();
        hotelBooking.setHotelBookingId(101L);
        hotelBooking.setUserId(201L);

        User user = new User();
        user.setUserId(201L);

        TransactionsType transactionsType = new TransactionsType();
        transactionsType.setTransactionsTypeId(typeId);

        int expectedSize = 1;

        // Mock repository responses
        Page<Transactions> transactionsPage = new PageImpl<>(Collections.singletonList(transactions));
        Mockito.when(transactionsRepository.findTransactionsCreatedDateAndPaymentIdAndTypeId(createDate, paymentId,
                typeId, PageRequest.of(page, size))).thenReturn(transactionsPage);

        Mockito.when(paymentTypeRepository.findByPaymentId(paymentId)).thenReturn(paymentType);
        Mockito.when(hotelBookingRepository.findByHotelBookingId(transactions.getHotelBookingId()))
                .thenReturn(hotelBooking);
        Mockito.when(userRepository.findByUserId(hotelBooking.getUserId())).thenReturn(user);
        Mockito.when(transactionsTyPeRepository.findByTransactionsTypeId(typeId)).thenReturn(transactionsType);

        // Call the method
        Page<ViewPaymentDTO> result = transactionsService.findByCreateDateAndPaymentIdAndTypeId(createDate, paymentId,
                typeId, page, size);

        // Verify the result
        assertEquals(expectedSize, result.getContent().size());
        ViewPaymentDTO viewPaymentDTO = result.getContent().get(0);
        assertEquals(paymentId, viewPaymentDTO.getTransactions().getPaymentId());
        assertEquals(101L, viewPaymentDTO.getHotelBooking().getHotelBookingId());
        assertEquals(201L, viewPaymentDTO.getUser().getUserId());
        assertEquals(paymentId, viewPaymentDTO.getPaymentType().getPaymentId());
        assertEquals(typeId, viewPaymentDTO.getTransactionsType().getTransactionsTypeId());
    }

    @Test
    @DisplayName("testFindByCreateDateAndPaymentIdAndTypeId_TypeIdNull")
    public void testFindByCreateDateAndPaymentIdAndTypeId_TypeIdNull() {
        LocalDate createDate = LocalDate.now();
        Long paymentId = 1L;
        Long typeId = null; // typeId is null
        int page = 0;
        int size = 10;

        // Mock repository responses
        Page<Transactions> transactionsPage = new PageImpl<>(Collections.emptyList());
        Mockito.when(transactionsRepository.findTransactionsCreatedDateAndPaymentIdAndTypeId(eq(createDate),
                eq(paymentId), ArgumentMatchers.isNull(), // Match null typeId
                eq(PageRequest.of(page, size)))).thenReturn(transactionsPage);

        // Call the method
        Page<ViewPaymentDTO> result = transactionsService.findByCreateDateAndPaymentIdAndTypeId(createDate, paymentId,
                typeId, page, size);

        // Verify the result
        assertEquals(0, result.getContent().size());
    }

    @Test
    @DisplayName("testFindByCreateDateAndPaymentIdAndTypeId_PaymentIdNull")
    public void testFindByCreateDateAndPaymentIdAndTypeId_PaymentIdNull() {
        LocalDate createDate = LocalDate.now();
        Long paymentId = null; // paymentId is null
        Long typeId = 2L;
        int page = 0;
        int size = 10;

        // Mock repository responses
        Page<Transactions> transactionsPage = new PageImpl<>(Collections.emptyList());
        Mockito.when(transactionsRepository.findTransactionsCreatedDateAndPaymentIdAndTypeId(createDate, null, typeId,
                PageRequest.of(page, size))).thenReturn(transactionsPage);

        // Call the method
        Page<ViewPaymentDTO> result = transactionsService.findByCreateDateAndPaymentIdAndTypeId(createDate, paymentId,
                typeId, page, size);

        // Verify the result
        assertEquals(0, result.getContent().size());
    }

    @Test
    @DisplayName("testFindByCreateDateAndPaymentIdAndTypeId_AllNull")
    public void testFindByCreateDateAndPaymentIdAndTypeId_AllNull() {
        LocalDate createDate = null; // createDate is null
        Long paymentId = null; // paymentId is null
        Long typeId = null; // typeId is null
        int page = 0;
        int size = 10;

        // Mock repository responses
        Page<Transactions> transactionsPage = new PageImpl<>(Collections.emptyList());
        Mockito.when(transactionsRepository.findAll(PageRequest.of(page, size))).thenReturn(transactionsPage);

        // Call the method
        Page<ViewPaymentDTO> result = transactionsService.findByCreateDateAndPaymentIdAndTypeId(createDate, paymentId,
                typeId, page, size);

        // Verify the result
        assertEquals(0, result.getContent().size());
    }

    @Test
    @DisplayName("testFindByCreateDateAndPaymentIdAndTypeId_CreateDateNotNull_TypeIdNull")
    public void testFindByCreateDateAndPaymentIdAndTypeId_CreateDateNotNull_TypeIdNull() {
        LocalDate createDate = LocalDate.now();
        Long paymentId = null; // paymentId is null
        Long typeId = null; // typeId is null
        int page = 0;
        int size = 10;

        // Mock repository responses
        Page<Transactions> transactionsPage = new PageImpl<>(Collections.emptyList());
        Mockito.when(transactionsRepository.findTransactionsCreatedDateAndPaymentIdAndTypeId(eq(createDate),
                        ArgumentMatchers.isNull(), ArgumentMatchers.isNull(), eq(PageRequest.of(page, size))))
                .thenReturn(transactionsPage);

        // Call the method
        Page<ViewPaymentDTO> result = transactionsService.findByCreateDateAndPaymentIdAndTypeId(createDate, paymentId,
                typeId, page, size);

        // Verify the result
        assertEquals(0, result.getContent().size());
    }

    @Test
    @DisplayName("Test findFirstTransactionOfHotelBooking with existing payTransaction")
    public void testFindFirstTransactionOfHotelBooking_ExistingPayTransaction() {
        // Arrange
        Long hotelBookingId = 123L;
        Transactions payTransaction = new Transactions(); // Mock a payTransaction
        payTransaction.setTransactionsTypeId(2L);

        // Mock the repository response
        when(transactionsRepository.findByHotelBookingIdAndTransactionsTypeId(hotelBookingId, 2L))
                .thenReturn(Optional.of(payTransaction));

        // Act
        Transactions result = transactionsService.findFirstTransactionOfHotelBooking(hotelBookingId);

        // Assert
        assertEquals(payTransaction, result);
    }

    @Test
    @DisplayName("Test findFirstTransactionOfHotelBooking with non-existing payTransaction")
    public void testFindFirstTransactionOfHotelBooking_NonExistingPayTransaction() {
        // Arrange
        Long hotelBookingId = 123L;

        // Mock the repository response for the first call
        when(transactionsRepository.findByHotelBookingIdAndTransactionsTypeId(hotelBookingId, 2L))
                .thenReturn(Optional.empty());

        // Mock the repository response for the second call
        Transactions payTransaction = new Transactions(); // Mock a payTransaction
        payTransaction.setTransactionsTypeId(1L);
        when(transactionsRepository.findByHotelBookingIdAndTransactionsTypeId(hotelBookingId, 1L))
                .thenReturn(Optional.of(payTransaction));

        // Act
        Transactions result = transactionsService.findFirstTransactionOfHotelBooking(hotelBookingId);

        // Assert
        assertEquals(payTransaction, result);
    }

}
