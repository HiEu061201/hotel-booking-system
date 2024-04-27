package fu.hbs.service.impl;

import fu.hbs.entities.BankList;
import fu.hbs.repository.BankListRepository;
import fu.hbs.service.interfaces.BankListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BankListServiceImpl implements BankListService {
    @Autowired
    private BankListRepository bankListRepository;

    @Override
    public List<BankList> getAllBank() {
        return bankListRepository.findAll();
    }
}
