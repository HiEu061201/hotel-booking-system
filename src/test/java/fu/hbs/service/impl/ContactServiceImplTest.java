package fu.hbs.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import fu.hbs.dto.SaleManagerController.ViewContactDTO;
import fu.hbs.entities.Contact;
import fu.hbs.repository.ContactRepository;

import java.util.Collections;

@ExtendWith(MockitoExtension.class)
class ContactServiceImplTest {

    @Mock
    private ContactRepository contactRepository;

    @InjectMocks
    private ContactServiceImpl contactService;

    @Test
    @DisplayName("testFindAllByTitleAndStatusUTCID01")
    void testFindAllByTitleAndStatus() {
        // Given
        String name = "Example Name";
        Integer status = 1;
        int page = 0;
        int size = 10;

        Contact contact = new Contact();
        contact.setContactId(1L);
        contact.setTitle("Test Contat");

        PageRequest pageRequest = PageRequest.of(page, size);


        // Mock the repository behavior
        Page<Contact> mockedContactPage = new PageImpl<>(Collections.singletonList(contact));
        when(contactRepository.findAllByTitleAndStatus(eq(name), eq(status), eq(pageRequest)))
                .thenReturn(mockedContactPage);

        // When
        Page<ViewContactDTO> result = contactService.findAllByTitleAndStatus(name, status, page, size);


        // Verify that the result is not null
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(contactRepository, times(1)).findAllByTitleAndStatus(eq(name), eq(status), eq(pageRequest));

    }
}
