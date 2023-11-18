package com.company.cybersecurity.controllers;

import com.company.cybersecurity.dtos.ChangePasswordDto;
import com.company.cybersecurity.exceptions.OldPasswordIsWrongException;
import com.company.cybersecurity.models.User;
import com.company.cybersecurity.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.security.Principal;

@Controller
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String login() {
        return "users/login";
    }

    @GetMapping("/registration")
    public String registration() {
        return "users/registration";
    }

    @PostMapping("/registration")
    public String registration(User userForm, Model model) {
        if (!userForm.getPassword().equals(userForm.getConfirmPassword())) {
            model.addAttribute("passwordError", "Пароли не совпадают");
            return "users/registration";
        }
        if (!userService.saveUser(userForm)) {
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
    public String changePassword(@ModelAttribute("changePasswordDto") ChangePasswordDto dto, Principal principal, Model model) {
        User user = userService.findUserByUsername(principal.getName());
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
