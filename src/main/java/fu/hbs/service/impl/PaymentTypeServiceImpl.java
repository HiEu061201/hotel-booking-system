package fu.hbs.service.impl;

import fu.hbs.entities.PaymentType;
import fu.hbs.repository.PaymentTypeRepository;
import fu.hbs.service.interfaces.PaymentTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PaymentTypeServiceImpl implements PaymentTypeService {
    @Autowired
    private PaymentTypeRepository paymentTypeRepository;


    @Override
    public PaymentType getPaymentTypeById(Long id) {
        Optional<PaymentType> paymentType = paymentTypeRepository.findById(id);
        return paymentType.orElse(null);
    }
}