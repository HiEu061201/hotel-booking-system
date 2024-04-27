/*
 * Copyright (C) 2023, FPT University
 * SEP490 - SEP490_G77
 * HBS
 * Hotel Booking System
 *
 * Record of change:
 * DATE          Version    Author           DESCRIPTION
 * 08/11/2023    1.0        HieuLBM          First Deploy
 *
 */
package fu.hbs.service.impl;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

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
import fu.hbs.service.interfaces.TransactionsService;

@Service
public class TransactionsServiceImpl implements TransactionsService {
    @Autowired
    private TransactionsRepository transactionsRepository;
    @Autowired
    private PaymentTypeRepository paymentTypeRepository;
    @Autowired
    private HotelBookingRepository hotelBookingRepository;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TransactionsTyPeRepository transactionsTyPeRepository;

    @Override
    public Transactions saveTransactions(Transactions vnpayTransactions) {
        return transactionsRepository.save(vnpayTransactions);
    }

    @Override
    public Page<ViewPaymentDTO> findByCreateDateAndPaymentId(LocalDate createDate, Long paymentId, int page, int size) {

        PageRequest pageRequest = PageRequest.of(page, size);

        Page<Transactions> transactionsPage = null;
        if (paymentId == null && createDate != null) {
            transactionsPage = transactionsRepository.findTransactionsCreatedDateAndPaymentId(createDate, null,
                    pageRequest);
        } else if (createDate == null && paymentId != null) {
            transactionsPage = transactionsRepository.findTransactionsByPaymentId(paymentId, pageRequest);
        } else {
            transactionsPage = transactionsRepository.findAll(pageRequest);
        }

        return transactionsPage.map(vnpayTransactions -> {
            PaymentType paymentType = paymentTypeRepository.findByPaymentId(vnpayTransactions.getPaymentId());
            HotelBooking hotelBooking = hotelBookingRepository
                    .findByHotelBookingId(vnpayTransactions.getHotelBookingId());
            User user = userRepository.findByUserId(hotelBooking.getUserId());
            TransactionsType transactionsType = transactionsTyPeRepository
                    .findByTransactionsTypeId(vnpayTransactions.getTransactionsTypeId());
            ViewPaymentDTO viewPaymentDTO = new ViewPaymentDTO();
            viewPaymentDTO.setPaymentType(paymentType);
            viewPaymentDTO.setTransactions(vnpayTransactions);
            viewPaymentDTO.setHotelBooking(hotelBooking);
            viewPaymentDTO.setUser(user);
            viewPaymentDTO.setTransactionsType(transactionsType);
            return viewPaymentDTO;
        });
    }

    @Override
    public Page<ViewPaymentDTO> findByCreateDateAndPaymentIdAndTypeId(LocalDate createDate, Long paymentId, Long typeId,
                                                                      int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);

        Page<Transactions> transactionsPage = null;
        if (paymentId == null) {

            if (createDate != null && typeId == null) {
                transactionsPage = transactionsRepository.findTransactionsCreatedDateAndPaymentIdAndTypeId(createDate,
                        null, null, pageRequest);
            } else if (createDate == null && typeId != null) {
                transactionsPage = transactionsRepository.findTransactionsByTransactionsTypeId(typeId, pageRequest);
            } else if (createDate != null && typeId != null) {
                transactionsPage = transactionsRepository.findTransactionsCreatedDateAndPaymentIdAndTypeId(createDate,
                        null, typeId, pageRequest);
            }
        }
        if (typeId == null) {

            if (createDate != null && paymentId == null) {
                transactionsPage = transactionsRepository.findTransactionsCreatedDateAndPaymentIdAndTypeId(createDate,
                        null, null, pageRequest);
            } else if (createDate == null && paymentId != null) {
                transactionsPage = transactionsRepository.findTransactionsByPaymentId(paymentId, pageRequest);
            } else if (createDate != null && paymentId != null) {
                transactionsPage = transactionsRepository.findTransactionsCreatedDateAndPaymentIdAndTypeId(createDate,
                        paymentId, null, pageRequest);
            }
        }
        if (paymentId == null && typeId == null && createDate != null) {
            transactionsPage = transactionsRepository.findTransactionsCreatedDateAndPaymentIdAndTypeId(createDate, null,
                    null, pageRequest);
        }
        if (paymentId == null && typeId == null && createDate == null) {
            transactionsPage = transactionsRepository.findAll(pageRequest);
        }
        if (paymentId != null && typeId != null && createDate == null) {
            transactionsPage = transactionsRepository.findTransactionsByTransactionsTypeIdAndPaymentId(typeId,
                    paymentId, pageRequest);
        }
        if (createDate != null && paymentId != null && typeId != null) {
            transactionsPage = transactionsRepository.findTransactionsCreatedDateAndPaymentIdAndTypeId(createDate,
                    paymentId, typeId, pageRequest);
        }

        return transactionsPage.map(vnpayTransactions -> {
            PaymentType paymentType = paymentTypeRepository.findByPaymentId(vnpayTransactions.getPaymentId());
            HotelBooking hotelBooking = hotelBookingRepository
                    .findByHotelBookingId(vnpayTransactions.getHotelBookingId());
            User user = userRepository.findByUserId(hotelBooking.getUserId());
            TransactionsType transactionsType = transactionsTyPeRepository
                    .findByTransactionsTypeId(vnpayTransactions.getTransactionsTypeId());
            ViewPaymentDTO viewPaymentDTO = new ViewPaymentDTO();
            viewPaymentDTO.setPaymentType(paymentType);
            viewPaymentDTO.setTransactions(vnpayTransactions);
            viewPaymentDTO.setHotelBooking(hotelBooking);
            viewPaymentDTO.setUser(user);
            viewPaymentDTO.setTransactionsType(transactionsType);
            return viewPaymentDTO;
        });
    }

    @Override
    public Transactions findFirstTransactionOfHotelBooking(Long hotelBookingId) {
        Optional<Transactions> payTransaction = transactionsRepository
                .findByHotelBookingIdAndTransactionsTypeId(hotelBookingId, 2L);
        if (payTransaction.isPresent()) {
            return payTransaction.get();
        } else {
            payTransaction = transactionsRepository.findByHotelBookingIdAndTransactionsTypeId(hotelBookingId, 1L);
            return payTransaction.orElse(null);
        }
    }

}
