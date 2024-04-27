/*
 * Copyright (C) 2023, FPT University
 * SEP490 - SEP490_G77
 * HBS
 * Hotel Booking System
 *
 * Record of change:
 * DATE          Version    Author           DESCRIPTION
 * 10/11/2023    1.0        HieuLBM          First Deploy
 *
 */

package fu.hbs.service.impl;


import fu.hbs.entities.CustomerCancellationReasons;
import fu.hbs.repository.CustomerCancellationReasonRepository;
import fu.hbs.service.interfaces.CustomerCancellationReasonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerCancellationReasonImpl implements CustomerCancellationReasonService {
    @Autowired
    private CustomerCancellationReasonRepository customerCancellationReasonRepository;

    @Override
    public List<CustomerCancellationReasons> findAllCancellationReasons() {
        return customerCancellationReasonRepository.findAll();
    }
}
