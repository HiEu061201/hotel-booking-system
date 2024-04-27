package fu.hbs.service.interfaces;

import fu.hbs.dto.SaleManagerController.ViewFeedBackDTO;
import fu.hbs.entities.Feedback;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import fu.hbs.entities.Contact;

@Service
public interface FeedbackService {
    void saveFeedback(Contact feedback);

    ResponseEntity<?> saveFeedback(Feedback feedback);

    Page<ViewFeedBackDTO> findAllByTitleAndStatus(String name, Integer status, int defaultPage, int defaultSize);
}

