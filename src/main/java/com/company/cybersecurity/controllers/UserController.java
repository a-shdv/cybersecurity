package com.company.cybersecurity.controllers;

import com.company.cybersecurity.dtos.ChangePasswordDto;
import com.company.cybersecurity.dtos.RegistrationDto;
import com.company.cybersecurity.exceptions.OldPasswordIsWrongException;
import com.company.cybersecurity.exceptions.PasswordsMismatch;
import com.company.cybersecurity.exceptions.UserAlreadyExistsException;
import com.company.cybersecurity.models.User;
import com.company.cybersecurity.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.web.WebAttributes;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Controller
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

//    @GetMapping("/login")
//    public String login() {
//        return "users/login";
//    }

    @GetMapping("/login")
    public String login(HttpServletRequest request, Model model) {
        HttpSession session = request.getSession(false);
        String errorMessage = null;
        if (session != null) {
            AuthenticationException ex = (AuthenticationException) session
                    .getAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
            if (ex != null) {
                errorMessage = ex.getLocalizedMessage();
            }
        }
        model.addAttribute("errorMessage", errorMessage);
        return "users/login";
    }

    @GetMapping("/registration")
    public String registration() {
        return "users/registration";
    }

    @PostMapping("/registration")
    public String registration(@ModelAttribute("registrationDto") RegistrationDto dto, Model model) {
        User userFromDb = userService.findUserByUsername(dto.getUsername());
        String message = null;
        try {
            boolean isPasswordsMatch = userService.isPasswordsMatch(dto.getPassword(), dto.getConfirmPassword());
            boolean isAlreadyExists = userService.isAlreadyExists(userFromDb);
            if (isPasswordsMatch && !isAlreadyExists) {
                userService.saveUser(RegistrationDto.toUser(dto));
            }
            message = "Пользователь " + dto.getUsername() + "успешно создан!";
        } catch (PasswordsMismatch | UserAlreadyExistsException e) {
            message = e.getLocalizedMessage();
            model.addAttribute("message", message);
            return "users/registration";
        }
        model.addAttribute("message", message);
        return "home";
    }

    @GetMapping("/change-password")
    public String changePassword() {
        return "users/change-password";
    }

    @PostMapping("/change-password")
    public String changePassword(@ModelAttribute("changePasswordDto") ChangePasswordDto dto, @AuthenticationPrincipal User user, Model model) {
        String message = null;
        try {
            boolean isPasswordsMatch = userService.isPasswordsMatch(dto.getNewPassword(), dto.getConfirmPassword());
            boolean isOldPasswordRight = userService.isOldPasswordRight(dto.getNewPassword(), dto.getOldPassword());
            if (isPasswordsMatch && isOldPasswordRight) {
                userService.changePassword(user, dto.getNewPassword());
                message = "Пароль изменен успешно!";
            }
        } catch (OldPasswordIsWrongException | PasswordsMismatch e) {
            message = e.getLocalizedMessage();
        }
        model.addAttribute("message", message);
        return "users/change-password";
    }


}
