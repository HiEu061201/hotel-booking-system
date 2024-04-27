package fu.hbs.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import fu.hbs.dto.SaleManagerController.ViewFeedBackDTO;
import fu.hbs.entities.Contact;
import fu.hbs.entities.Feedback;
import fu.hbs.entities.User;
import fu.hbs.repository.ContactRepository;
import fu.hbs.repository.FeedbackRepository;
import fu.hbs.repository.UserRepository;
import fu.hbs.service.interfaces.FeedbackService;
import fu.hbs.utils.StringDealer;

@Service
public class FeedbackServiceImpl implements FeedbackService {
    private StringDealer stringDealer = new StringDealer();
    @Autowired
    private FeedbackRepository feedbackRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    ContactRepository contactRepository;

    @Override
    public void saveFeedback(Contact feedback) {
        // Tìm kiếm liên hệ bằng địa chỉ email
        Contact existingContact = contactRepository.findByEmail(feedback.getEmail());

        if (existingContact != null) {
            // Nếu email đã tồn tại, cập nhật trường "user_id"
            // existingContact.setUserId(feedback.getUserId());
            contactRepository.save(existingContact);
        } else {
            feedback.setStatus(false);
            contactRepository.save(feedback);
        }
    }

    @Override
    public ResponseEntity<?> saveFeedback(Feedback feedback) {
        if (stringDealer.trimMax(feedback.getTitle()).isEmpty()
                || stringDealer.trimMax(feedback.getComment()).isEmpty()) {
            return new ResponseEntity<>("Tiêu đề, mô tả không được để trống!", HttpStatus.BAD_REQUEST);
        }
        feedbackRepository.save(feedback);
        return new ResponseEntity<>("Lưu feedback thành công!", HttpStatus.OK);
    }

    @Override
    public Page<ViewFeedBackDTO> findAllByTitleAndStatus(String name, Integer status, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);

        Page<Feedback> feedbackPage = feedbackRepository.findAllByTitleAndStatus(name, status, pageRequest);

        return feedbackPage.map(feedback -> {
            ViewFeedBackDTO viewFeedBackDTO = new ViewFeedBackDTO();
            User user = userRepository.findByUserId(feedback.getUserId());
            viewFeedBackDTO.setUser(user);
            viewFeedBackDTO.setFeedback(feedback);
            return viewFeedBackDTO;

        });
    }

}
