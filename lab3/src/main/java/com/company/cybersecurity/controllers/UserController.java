package com.company.cybersecurity.controllers;

import com.company.cybersecurity.dto.RegistrationDto;
import com.company.cybersecurity.models.User;
import com.company.cybersecurity.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/registration")
    public String registration() {
        return "registration";
    }

    @PostMapping("/registration")
    public String registration(@ModelAttribute("registrationDto") RegistrationDto dto, Model model) {
        User user = (User) userService.loadUserByUsername(dto.getUsername());
        String message = "";
        if (user == null) {
            userService.saveUser(RegistrationDto.toUser(dto));
            message = "Пользователь " + dto.getUsername() + " успешно зарегистрирован!";
            model.addAttribute("successMessage", message);
        } else {
            message = "Пользователь " + dto.getUsername() + " уже существует!";
            model.addAttribute("errorMessage", message);
        }
        return "registration";
    }
}
