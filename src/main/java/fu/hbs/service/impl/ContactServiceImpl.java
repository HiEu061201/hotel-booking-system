package fu.hbs.service.impl;

import fu.hbs.dto.SaleManagerController.ViewContactDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import fu.hbs.entities.Contact;
import fu.hbs.repository.ContactRepository;
import fu.hbs.service.interfaces.ContactService;

@Service
public class ContactServiceImpl implements ContactService {

    @Autowired
    private ContactRepository contactRepository;

    @Override
    public Page<ViewContactDTO> findAllByTitleAndStatus(String name, Integer status, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<Contact> contactPage = contactRepository.findAllByTitleAndStatus(name, status, pageRequest);
        return contactPage.map(contact -> {
            ViewContactDTO viewContactDTO = new ViewContactDTO();
            viewContactDTO.setContact(contact);
            return viewContactDTO;
        });
    }

}
