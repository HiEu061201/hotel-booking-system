package fu.hbs.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import fu.hbs.entities.Contact;
import fu.hbs.service.interfaces.FeedbackService;

@Controller
@RequestMapping("/feedback")
public class FeedbackController {

    @Autowired
    private FeedbackService feedbackService;

    @PostMapping("/save")
    public ResponseEntity<String> saveFeedback(@RequestBody Contact feedback) {
        try {

            feedbackService.saveFeedback(feedback);
            return ResponseEntity.ok("Phản hồi đã được lưu thành công.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi khi lưu phản hồi.");
        }
    }
}


