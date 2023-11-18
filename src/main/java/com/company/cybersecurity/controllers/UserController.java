package com.company.cybersecurity.controllers;

import com.company.cybersecurity.dtos.ChangePasswordDto;
import com.company.cybersecurity.dtos.RegistrationDto;
import com.company.cybersecurity.exceptions.OldPasswordIsWrongException;
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
        if (!dto.getPassword().equals(dto.getConfirmPassword())) {
            model.addAttribute("passwordError", "Пароли не совпадают");
            return "users/registration";
        }
        if (!userService.saveUser(RegistrationDto.toUser(dto))) {
            model.addAttribute("usernameError", "Пользователь с таким именем уже существует");
            return "users/registration";
        }

        return "redirect:/";
    }

    @GetMapping("/change-password")
    public String changePassword() {
        return "users/change-password";
    }

    @PostMapping("/change-password")
    public String changePassword(@ModelAttribute("changePasswordDto") ChangePasswordDto dto, @AuthenticationPrincipal User user, Model model) {
        String message = null;
        try {
            message = userService.changePassword(dto.getOldPassword(), dto.getNewPassword(), user)
                    ? "Пароль был успешно изменен!"
                    : "Старый пароль неверный!";
        } catch (OldPasswordIsWrongException e) {
            e.getLocalizedMessage();
        }
        model.addAttribute("message", message);
        return "users/change-password";
    }


}
