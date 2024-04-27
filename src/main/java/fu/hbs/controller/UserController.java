/*
 * Copyright (C) 2023, FPT University
 * SEP490 - SEP490_G77
 * HBS
 * Hotel Booking System
 *
 * Record of change:
 * DATE          Version    Author           DESCRIPTION
 * 04/10/2023    1.0        HieuLBM          First Deploy
 * 21/10/2023	 2.0		HieuLBM			 view, update profile
 * 25/10/2023	 3.0		HieuLBM			 change password
 */
package fu.hbs.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import fu.hbs.dto.User.UserDto;
import fu.hbs.entities.New;
import fu.hbs.entities.RoomService;
import fu.hbs.entities.User;
import fu.hbs.exceptionHandler.UserIvalidException;
import fu.hbs.exceptionHandler.UserNotFoundException;
import fu.hbs.service.interfaces.NewsService;
import fu.hbs.service.interfaces.ServiceService;
import fu.hbs.service.interfaces.UserService;
import fu.hbs.utils.StringDealer;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller

public class UserController {
    private UserService userService;
    private StringDealer stringDealer;
    private final ServiceService serviceService;
    private final NewsService newsService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public UserController(UserService userService, ServiceService serviceService, NewsService newsService) {
        this.userService = userService;
        this.stringDealer = new StringDealer();
        this.serviceService = serviceService;
        this.newsService = newsService;
        this.userService = userService;
    }

    /**
     * Prepare a new UserDto object as a model attribute for user registration.
     *
     * @return a UserDto object for registration.
     */
    @ModelAttribute("userdto")
    public UserDto userResgistrationDto() {
        return new UserDto();
    }

    /**
     * Display the user registration form.
     *
     * @return the registration form view.
     */
    @GetMapping("/registration")
    public String registerForm() {
        return "authentication/registration";
    }

    /**
     * Process user registration request and create a new user account.
     *
     * @param userDto The user data for registration.
     * @return a success or error view based on the registration result.
     */
    @PostMapping("/registration")
    public String registerUserAccount(@ModelAttribute("userdto") UserDto userDto) {
        if (userService.checkUserbyEmail(userDto.getUserEmail())) {
            return "redirect:/registration?emailexist";
        }
        if (userDto.getUserPassword().equals(userDto.getCheckPass()) == false) {
            return "redirect:/registration?checkpass";
        }
        userDto.setStatus(true);
        // userService.save(userDto);

        // Lưu thông tin người dùng
        userService.save(userDto);

        return "redirect:/registration?success";
    }

    /**
     * View the user's profile.
     *
     * @param model          The model for user profile data.
     * @param authentication The user's authentication information.
     * @return the user profile view.
     */
    @GetMapping("/viewProfile")
    public String viewUserProfile(Model model, Authentication authentication)
            throws UserNotFoundException, UserIvalidException {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userService.getUserbyEmail(userDetails.getUsername());

        boolean isAdmin = userDetails.getAuthorities().stream().anyMatch(role -> role.getAuthority().equals("ADMIN"));
        boolean isManagement = userDetails.getAuthorities().stream().anyMatch(role -> role.getAuthority().equals("MANAGEMENT"));
        boolean isCustomer = userDetails.getAuthorities().stream().anyMatch(role -> role.getAuthority().equals("CUSTOMER"));
        boolean isReceptionist = userDetails.getAuthorities().stream().anyMatch(role -> role.getAuthority().equals("RECEPTIONISTS"));
        boolean isAccountant = userDetails.getAuthorities().stream().anyMatch(role -> role.getAuthority().equals("ACCOUNTING"));
        boolean isSaleManager = userDetails.getAuthorities().stream().anyMatch(role -> role.getAuthority().equals("SALEMANAGER"));
        model.addAttribute("isAdmin", isAdmin);
        model.addAttribute("isManagement", isManagement);
        model.addAttribute("isCustomer", isCustomer);
        model.addAttribute("isReceptionist", isReceptionist);
        model.addAttribute("isAccountant", isAccountant);
        model.addAttribute("isSaleManager", isSaleManager);
        model.addAttribute("user", user);
        return "profile/viewProfileCustomer";
    }

    /**
     * Display the user profile update form.
     *
     * @param model          The model for user profile data.
     * @param authentication The user's authentication information.
     * @return the profile update form view.
     */
    @GetMapping("/updateprofile")
    public String viewEditUserProfile(Model model, Authentication authentication) throws UserNotFoundException, UserIvalidException {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userService.getUserbyEmail(userDetails.getUsername());
        User user1 = userService.findById(user.getUserId());
        boolean isAdmin = userDetails.getAuthorities().stream().anyMatch(role -> role.getAuthority().equals("ADMIN"));
        boolean isManagement = userDetails.getAuthorities().stream().anyMatch(role -> role.getAuthority().equals("MANAGEMENT"));
        boolean isCustomer = userDetails.getAuthorities().stream().anyMatch(role -> role.getAuthority().equals("CUSTOMER"));
        boolean isReceptionist = userDetails.getAuthorities().stream().anyMatch(role -> role.getAuthority().equals("RECEPTIONISTS"));
        boolean isAccountant = userDetails.getAuthorities().stream().anyMatch(role -> role.getAuthority().equals("ACCOUNTING"));
        boolean isSaleManager = userDetails.getAuthorities().stream().anyMatch(role -> role.getAuthority().equals("SALEMANAGER"));
        model.addAttribute("isAdmin", isAdmin);
        model.addAttribute("isManagement", isManagement);
        model.addAttribute("isCustomer", isCustomer);
        model.addAttribute("isReceptionist", isReceptionist);
        model.addAttribute("isAccountant", isAccountant);
        model.addAttribute("isSaleManager", isSaleManager);
        model.addAttribute("user", user);
        return "profile/updateProfileCustomer";
    }

    /**
     * Process user profile update request and update the user's information.
     *
     * @param user          The updated user data.
     * @param bindingResult The result of data binding and validation.
     * @param model         The model for the user profile view.
     * @param image         The user's profile image.
     * @return a success or error view based on the update result.
     */
    @PostMapping("/updateprofile")
    public String updateProfile(@Valid @ModelAttribute("user") User user, BindingResult bindingResult, Model model,
                                @RequestParam(value = "file", required = false) MultipartFile image, Authentication authentication) throws UserNotFoundException, UserIvalidException, IOException {

        try {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            boolean isAdmin = userDetails.getAuthorities().stream().anyMatch(role -> role.getAuthority().equals("ADMIN"));
            boolean isManagement = userDetails.getAuthorities().stream().anyMatch(role -> role.getAuthority().equals("MANAGEMENT"));
            boolean isCustomer = userDetails.getAuthorities().stream().anyMatch(role -> role.getAuthority().equals("CUSTOMER"));
            boolean isReceptionist = userDetails.getAuthorities().stream().anyMatch(role -> role.getAuthority().equals("RECEPTIONISTS"));
            boolean isAccountant = userDetails.getAuthorities().stream().anyMatch(role -> role.getAuthority().equals("ACCOUNTING"));
            boolean isSaleManager = userDetails.getAuthorities().stream().anyMatch(role -> role.getAuthority().equals("SALEMANAGER"));
            model.addAttribute("isAdmin", isAdmin);
            model.addAttribute("isManagement", isManagement);
            model.addAttribute("isCustomer", isCustomer);
            model.addAttribute("isReceptionist", isReceptionist);
            model.addAttribute("isAccountant", isAccountant);
            model.addAttribute("isSaleManager", isSaleManager);


            if (image == null || image.isEmpty()) {
                user.setImage(null);
            } else {
                String uploadDirectory = "src/main/resources/static/assets/img/";
                stringDealer.uploadFile(image, uploadDirectory);
                user.setImage(image.getOriginalFilename());
            }


            if (user.getName() != null && user.getName().length() > 32) {
                bindingResult.rejectValue("name", "name", "Tên không thể dài hơn 32 kí tự");
                model.addAttribute("user", user);
                return "profile/updateProfileCustomer";

            }


            // Kiểm tra số điện thoại
            if (user.getPhone() != null && !user.getPhone().matches("^(0?)(3[2-9]|5[6|8|9]|7[0|6-9]|8[0-6|8|9]|9[0-4|6-9])[0-9]{7}$")) {
                bindingResult.rejectValue("phone", "phone", "Điện thoại không đúng định dạng");
                model.addAttribute("user", user);
                return "profile/updateProfileCustomer";
            }
            if (user.getAddress() != null && user.getAddress().length() > 100) {
                bindingResult.rejectValue("address", "address", "Địa chỉ không được lớn hơn 100 kí tự ");
                model.addAttribute("user", user);
                return "profile/updateProfileCustomer";
            }


            User user1 = userService.findById(user.getUserId());
            user1.setName(user.getName());
            user1.setPhone(user.getPhone());
            user1.setAddress(user.getAddress());
            user1.setDob(user.getDob());
            user1.setGender(user.getGender());
            user1.setImage(user.getImage());
            userService.update(user1);


            model.addAttribute("sucsses", "Cập nhật thành công");
            return "profile/updateProfileCustomer";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("false", "Cập nhật thất bại");
            return "profile/updateProfileCustomer";
        }

    }

    /**
     * Display the form for changing the user's password.
     *
     * @param model          The model for user profile data.
     * @param authentication The user's authentication information.
     * @return the change password form view.
     */
    @GetMapping("/changepass")
    public String viewChangePassword(Model model, Authentication authentication) throws UserNotFoundException {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userService.getUserbyEmail(userDetails.getUsername());
        boolean isAdmin = userDetails.getAuthorities().stream().anyMatch(role -> role.getAuthority().equals("ADMIN"));
        boolean isManagement = userDetails.getAuthorities().stream().anyMatch(role -> role.getAuthority().equals("MANAGEMENT"));
        boolean isCustomer = userDetails.getAuthorities().stream().anyMatch(role -> role.getAuthority().equals("CUSTOMER"));
        boolean isReceptionist = userDetails.getAuthorities().stream().anyMatch(role -> role.getAuthority().equals("RECEPTIONISTS"));
        boolean isAccountant = userDetails.getAuthorities().stream().anyMatch(role -> role.getAuthority().equals("ACCOUNTING"));
        boolean isSaleManager = userDetails.getAuthorities().stream().anyMatch(role -> role.getAuthority().equals("SALEMANAGER"));
        model.addAttribute("isAdmin", isAdmin);
        model.addAttribute("isManagement", isManagement);
        model.addAttribute("isCustomer", isCustomer);
        model.addAttribute("isReceptionist", isReceptionist);
        model.addAttribute("isAccountant", isAccountant);
        model.addAttribute("isSaleManager", isSaleManager);
        model.addAttribute("user", user);
        return "profile/changePasswordCustomer";
    }

    /**
     * Process the user's password change request and update the user's password.
     *
     * @param oldpassword        The user's old password.
     * @param newpassword        The new password.
     * @param confirmpassword    The confirmation of the new password.
     * @param model              The model for the change password view.
     * @param redirectAttributes Redirect attributes for success or error messages.
     * @param session            The user's session data.
     * @return a success or error view based on the password change result.
     */
    @PostMapping("/changepass")
    public String UserChangePassword(@RequestParam("oldpassword") String oldpassword,
                                     @RequestParam("newpassword") String newpassword, @RequestParam("confirmpassword") String
                                             confirmpassword,
                                     Model model, Authentication authentication, final RedirectAttributes redirectAttributes, HttpSession session) {
        try {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            boolean isAdmin = userDetails.getAuthorities().stream().anyMatch(role -> role.getAuthority().equals("ADMIN"));
            boolean isManagement = userDetails.getAuthorities().stream().anyMatch(role -> role.getAuthority().equals("MANAGEMENT"));
            boolean isCustomer = userDetails.getAuthorities().stream().anyMatch(role -> role.getAuthority().equals("CUSTOMER"));
            boolean isReceptionist = userDetails.getAuthorities().stream().anyMatch(role -> role.getAuthority().equals("RECEPTIONISTS"));
            boolean isAccountant = userDetails.getAuthorities().stream().anyMatch(role -> role.getAuthority().equals("ACCOUNTING"));
            boolean isSaleManager = userDetails.getAuthorities().stream().anyMatch(role -> role.getAuthority().equals("SALEMANAGER"));
            model.addAttribute("isAdmin", isAdmin);
            model.addAttribute("isManagement", isManagement);
            model.addAttribute("isCustomer", isCustomer);
            model.addAttribute("isReceptionist", isReceptionist);
            model.addAttribute("isAccountant", isAccountant);
            model.addAttribute("isSaleManager", isSaleManager);


            String email = (String) session.getAttribute("accountDetail");
            User user1 = userService.getUserbyEmail(email);
            System.out.println(user1);

            if (!passwordEncoder.matches(oldpassword, user1.getPassword())) {
                model.addAttribute("pass", "Mật khẩu cũ không đúng");
                model.addAttribute("oldpassword", oldpassword);
                return "profile/changePasswordCustomer";
            }
            model.addAttribute("oldpassword", oldpassword);

            if (!stringDealer.checkPasswordRegex(newpassword)) { /* Password is not valid */
                model.addAttribute("pass1", "Mật khẩu mới không hợp lệ");
                model.addAttribute("newpassword", newpassword);
                return "profile/changePasswordCustomer";
            }
            model.addAttribute("newpassword", newpassword);

            if (passwordEncoder.matches(newpassword, user1.getPassword())) {// oldpassword != newPassword
                model.addAttribute("pass4", "Mật khẩu nhập mới không được trùng với mật khẩu cũ");
                model.addAttribute("oldpassword", oldpassword);
                return "profile/changePasswordCustomer";

            }
            model.addAttribute("oldpassword", oldpassword);

            if (!stringDealer.checkPasswordRegex(confirmpassword)) {
                model.addAttribute("pass3", "Mật khẩu nhập lại không hợp lệ");

                model.addAttribute("confirmpassword", confirmpassword);
                return "profile/changePasswordCustomer";
            }

            model.addAttribute("confirmpassword", confirmpassword);

            if (!newpassword.trim().equals(confirmpassword.trim())) { /* Password not match */
                model.addAttribute("pass2", "Mật khẩu không khớp");
                model.addAttribute("newpassword", newpassword);
                model.addAttribute("confirmpassword", confirmpassword);
                return "profile/changePasswordCustomer";
            }

            // Password match

            String encodedPassword = passwordEncoder.encode(confirmpassword);
            user1.setPassword(encodedPassword);
            userService.save(user1);

            model.addAttribute("sucsses", "Đổi mật khẩu thành công");
            return "profile/changePasswordCustomer";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("false", "Đổi mật khẩu thất bại");
            return "profile/changePasswordCustomer";
        }


    }

    @GetMapping("/services")
    public String getAllServices(Model model, @RequestParam(required = false) String serviceName,
                                 @PageableDefault(size = 4) Pageable pageable) {
        Page<RoomService> services;

        if (serviceName != null && !serviceName.isEmpty()) {
            services = serviceService.searchByName(serviceName, pageable);
        } else {
            services = serviceService.getAllServices(pageable);
        }

        model.addAttribute("services", services.getContent());
        model.addAttribute("serviceName", serviceName);
        model.addAttribute("currentPage", services.getNumber());
        model.addAttribute("totalPages", services.getTotalPages());

        return "services/listServiceCustomer";
    }

    @GetMapping("/service-details")
    public String getServiceDetail(@RequestParam("serviceId") Long serviceId, Model model) {
        // Truy vấn dịch vụ dựa trên serviceId
        RoomService service = serviceService.findById(serviceId);

        // Lấy danh sách dịch vụ từ serviceService
//		List<RoomService> allServices = serviceService.getAllServices();

        List<RoomService> distinctRoomservices = serviceService.getAllServices().stream()
                .filter(roomService -> roomService.getServiceId() != serviceId).collect(Collectors.toList());
        // Đưa dịch vụ vào model để hiển thị trong trang service-detail.html
        model.addAttribute("service", service);

        // Đưa danh sách dịch vụ vào model
//		model.addAttribute("allServices", allServices);
        model.addAttribute("distinctRoomservices", distinctRoomservices);
        return "services/serviceDetailCustomer";
    }

    @GetMapping("/news")
    public String getAllNews(Model model, @RequestParam(required = false) String
            newsTitle, @PageableDefault(size = 6) Pageable pageable) throws UserNotFoundException {
        Page<New> news;
        List<User> users = new ArrayList<>();
        User user = new User();
        if (newsTitle != null && !newsTitle.isEmpty()) {
            news = newsService.searchByTitle(newsTitle, pageable);
        } else {
            news = newsService.getAllNews(pageable);
        }

        for (int i = 0; i < news.getContent().size(); i++) {
            user = userService.findById(news.getContent().get(i).getUserId());

        }
        model.addAttribute("user", user);
        model.addAttribute("news", news.getContent());
        model.addAttribute("newsTitle", newsTitle);
        model.addAttribute("currentPage", news.getNumber());
        model.addAttribute("totalPages", news.getTotalPages());

        return "news/listNewsCustomer";
    }

    @GetMapping("/news-details")
    public String getNewsDetail(@RequestParam("newId") Long newsId, Model model) {
        // Truy vấn tin tức dựa trên newsId
        New news = newsService.findById(newsId);
        List<New> distinctNews = newsService.getAllNews().stream()
                .filter(newsService -> newsService.getNewId() != newsId).collect(Collectors.toList());

        // Đưa tin tức vào model để hiển thị trong trang news_detail.html
        model.addAttribute("news", news);
        model.addAttribute("distinctNews", distinctNews);
        return "news/newsDetailCustomer";
    }
}
