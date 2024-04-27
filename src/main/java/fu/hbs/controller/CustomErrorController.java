package fu.hbs.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.http.HttpStatus;

@Controller
public class CustomErrorController implements ErrorController {


    @RequestMapping("/error")
    String error(HttpServletRequest request, Model model) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        System.out.println("Status" + status);
        if (status != null) {
            int statusCode = Integer.parseInt(status.toString());

            if (statusCode == HttpStatus.BAD_REQUEST.value()) {
                String message = "Vui lòng thử lại!";
                model.addAttribute("message", message);
                return "errorPage";
            }
            if (statusCode == HttpStatus.FORBIDDEN.value()) {
                return "accessDenied";
            }
            if (statusCode == HttpStatus.NOT_FOUND.value()) {
                String message = "Không tìm thấy!";
                model.addAttribute("message", message);
                return "errorPage";
            }
            if (statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
                String message = "Some internal server error happend! Please try again!";
                model.addAttribute("message", message);
                return "500";
            }
        }
//        } else {
//            return "redirect:/login";
//        }
        return "500";
    }

    public String getErrorPath() {
        return "/error";
    }

}
