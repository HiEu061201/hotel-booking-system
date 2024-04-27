package fu.hbs.controller;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import fu.hbs.dto.User.UserDto;
import fu.hbs.dto.AdminDTO.UpdateUserDTO;
import fu.hbs.dto.AdminDTO.ViewUserDTO;
import fu.hbs.entities.*;
import fu.hbs.service.interfaces.UserRoleService;
import fu.hbs.utils.EmailUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import fu.hbs.service.interfaces.AdminService;
import fu.hbs.service.interfaces.BookingRoomDetailsService;
import fu.hbs.service.interfaces.UserService;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Controller
public class AdminController {
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserService userService;
    @Autowired
    private AdminService adminService;
    @Autowired
    private BookingRoomDetailsService bookingRoomDetailsService;
    @Autowired
    private UserRoleService userRoleService;
    @Autowired
    private TemplateEngine templateEngine;
    @Autowired
    private JavaMailSender javaMailSender;
    private final EmailUtil emailUtil;

    public AdminController(EmailUtil emailUtil) {
        this.emailUtil = emailUtil;
    }


    @GetMapping("/admin/viewListUser")
    public String listUser(Model model, Authentication authentication,
                           @RequestParam(name = "name", required = false) String name,
                           @RequestParam(name = "status", required = false) Boolean status,
                           @RequestParam(name = "roleId", required = false) Integer roleId,
                           @RequestParam(defaultValue = "0") Integer page,
                           @RequestParam(defaultValue = "10") Integer size
    ) {


        int defaultPage = (page != null && page >= 0) ? page : 0;
        int defaultSize = (size != null && size > 0) ? size : 10;
        Page<ViewUserDTO> viewUserDTOS = null;
        if (roleId == null) {
            viewUserDTOS = userService.searchByNameAndStatus(name, status, null, defaultPage, defaultSize);
        } else {

            viewUserDTOS = userService.searchByNameAndStatus(name, status, Long.valueOf(roleId), defaultPage, defaultSize);
        }

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userService.getUserbyEmail(userDetails.getUsername());

        List<ViewUserDTO> viewUserDTOList = viewUserDTOS.getContent();

        List<ViewUserDTO> distinctUsers = viewUserDTOList.stream()
                .filter(u -> !u.getUser().getUserId().equals(user.getUserId()))
                .collect(Collectors.toList());


        model.addAttribute("currentPage", viewUserDTOS.getNumber());
        model.addAttribute("pageSize", viewUserDTOS.getSize());
        model.addAttribute("totalPages", viewUserDTOS.getTotalPages());

        model.addAttribute("name", name);
        model.addAttribute("status", status);
        model.addAttribute("roleId", roleId);

        if (viewUserDTOList.isEmpty()) {
            model.addAttribute("error", "Không tìm thấy");
            model.addAttribute("activePage", "/admin/viewListUser");
            return "admin/viewListUser";
        }
        model.addAttribute("users", distinctUsers);
        model.addAttribute("activePage", "/admin/viewListUser");


        return "admin/viewListUser";
    }

    @PostMapping("/admin/UpdateUser")
    public ResponseEntity<?> updateRoleAndStatus(@RequestBody UpdateUserDTO userProfile) {
        try {
            UserRole userRole = userRoleService.findByUserIdAndAndRoleId(userProfile.getUserId(), userProfile.getRoleId());
            User user = userService.findById(userProfile.getUserId());
            if (userRole != null && user.isStatus() == userProfile.getStatus()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "Trạng thái không thay đổi"));
            }
            userService.updateUserStatus(userProfile);
            userService.updateUserRoleByUserId(userProfile);
            String successMessage = "Cập nhật thành công";
            return ResponseEntity.ok().body(Map.of("message", successMessage));
        } catch (Exception e) {

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Lỗi khi cập nhật:", "exceptionMessage", e.getMessage()));
        }

    }

    @PostMapping("/admin/createStaffAccount")
    public ResponseEntity<?> registerUserAccount(
            @RequestParam("staffName") String staffName,
            @RequestParam("staffEmail") String staffEmail,
            @RequestParam("staffRole") Long staffRole, UserDto userDto) {


        try {
            User existingUser = userService.getUserbyEmail(staffEmail);
            if (existingUser != null) {
                String errorMail = "Email đã tồn tại!";
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", errorMail));
            }

            String randomPassword = generateRandomPassword(8);
            String encodedPassword = passwordEncoder.encode(randomPassword);

            userDto = new UserDto(staffName, staffEmail, encodedPassword);
            userDto.setStatus(true);

            User staff = new User();
            staff.setEmail(staffEmail);
            staff.setName(staffName);
            staff.setPassword(encodedPassword);
            staff.setStatus(true);

            userService.save(staff);

            UserRole userRoleEntity = new UserRole();
            userRoleEntity.setUserId(staff.getUserId());
            userRoleEntity.setRoleId(staffRole);
            userService.saveStaffRole(userRoleEntity);

            Context context = new Context();
            context.setVariable("email", staffEmail);
            context.setVariable("password", randomPassword);
            String emailContent = templateEngine.process("email/createStaffEmail", context);
            emailUtil.sendBookingEmail(staffEmail, "Cấp tài khoản", emailContent);

            String successMessage = "Tạo tài khoản thành công";
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(Map.of("message", successMessage));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Lỗi khi cập nhật:", "exceptionMessage", e.getMessage()));
        }
    }


    // Phương thức tạo mật khẩu ngẫu nhiên
    private String generateRandomPassword(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder password = new StringBuilder();

        for (int i = 0; i < length; i++) {
            int index = (int) (Math.random() * characters.length());
            password.append(characters.charAt(index));
        }

        return password.toString();
    }


}




