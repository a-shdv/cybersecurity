package com.company.cybersecurity.controllers;

import com.company.cybersecurity.exceptions.UserAlreadyExistsException;
import com.company.cybersecurity.models.User;
import com.company.cybersecurity.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
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
        return "users/login";
    }

    @GetMapping("/registration")
    public String registration() {
        return "users/registration";
    }


    @PostMapping("/registration")
    public String addUser(User userForm, Model model) throws UserAlreadyExistsException {
        if (!userForm.getPassword().equals(userForm.getPasswordConfirm())){
            model.addAttribute("passwordError", "Пароли не совпадают");
            return "users/registration";
        }
        if (!userService.saveUser(userForm)){
            model.addAttribute("usernameError", "Пользователь с таким именем уже существует");
            return "users/registration";
        }

        return "redirect:/";
    }
}
