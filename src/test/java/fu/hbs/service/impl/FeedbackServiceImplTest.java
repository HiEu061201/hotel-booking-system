package fu.hbs.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import fu.hbs.dto.SaleManagerController.ViewFeedBackDTO;
import fu.hbs.entities.Contact;
import fu.hbs.entities.Feedback;
import fu.hbs.entities.User;
import fu.hbs.repository.ContactRepository;
import fu.hbs.repository.FeedbackRepository;
import fu.hbs.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class FeedbackServiceImplTest {

    @Mock
    private ContactRepository contactRepository;

    @Mock
    private FeedbackRepository feedbackRepository; // Make sure this is properly injected

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private FeedbackServiceImpl feedbackService;

    @Test
    void testSaveFeedbackExistingContact() {
        // Arrange
        Contact existingContact = new Contact();
        existingContact.setEmail("test@example.com");

        when(contactRepository.findByEmail("test@example.com")).thenReturn(existingContact);

        // Act
        feedbackService.saveFeedback(existingContact);

        // Assert
        verify(contactRepository).save(existingContact);
    }

    @Test
    void testSaveFeedbackNewContact() {
        // Arrange
        Contact newContact = new Contact();
        newContact.setEmail("new@example.com");

        when(contactRepository.findByEmail("new@example.com")).thenReturn(null);

        // Act
        feedbackService.saveFeedback(newContact);

        // Assert
        verify(contactRepository).save(newContact);
    }

    @Test
    void testSaveFeedbackWithEmptyTitle() {
        // Arrange
        Feedback feedback = new Feedback();
        feedback.setTitle("");
        feedback.setComment("Test comment");

        // Act
        ResponseEntity<?> result = feedbackService.saveFeedback(feedback);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertEquals("Tiêu đề, mô tả không được để trống!", result.getBody());
        verify(feedbackRepository, never()).save(any());
    }

    @Test
    void testSaveFeedbackWithEmptyComment() {
        // Arrange
        Feedback feedback = new Feedback();
        feedback.setTitle("Test title");
        feedback.setComment("");

        // Act
        ResponseEntity<?> result = feedbackService.saveFeedback(feedback);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertEquals("Tiêu đề, mô tả không được để trống!", result.getBody());
        verify(feedbackRepository, never()).save(any());
    }

    @Test
    void testSaveFeedbackSuccessful() {
        // Arrange
        Feedback feedback = new Feedback();
        feedback.setTitle("Test title");
        feedback.setComment("Test comment");

        // Act
        ResponseEntity<?> result = feedbackService.saveFeedback(feedback);

        // Assert
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("Lưu feedback thành công!", result.getBody());
        verify(feedbackRepository).save(feedback);
    }

    @Test
    @DisplayName("testFindAllByTitleAndStatusUTCID01")
    void testFindAllByTitleAndStatus() {
        // Arrange
        String name = "Test";
        Integer status = 1;
        int page = 0;
        int size = 10;

        PageRequest pageRequest = PageRequest.of(page, size);
        Page<Feedback> feedbackPage = new PageImpl<>(Collections.singletonList(createFeedback()));
        when(feedbackRepository.findAllByTitleAndStatus(eq(name), eq(status), eq(pageRequest)))
                .thenReturn(feedbackPage);

        // Mock user
        User user = new User();
        user.setUserId(null);
        when(userRepository.findByUserId(user.getUserId())).thenReturn(user);

        // Act
        Page<ViewFeedBackDTO> result = feedbackService.findAllByTitleAndStatus(name, status, page, size);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(feedbackRepository, times(1)).findAllByTitleAndStatus(eq(name), eq(status), eq(pageRequest));

    }

    private Feedback createFeedback() {

        Feedback feedback = new Feedback();
        feedback.setFeedbackId(1L);

        return feedback;
    }
}
