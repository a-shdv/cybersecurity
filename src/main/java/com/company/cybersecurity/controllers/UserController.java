package com.company.cybersecurity.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UserController {
    @GetMapping("/login")
    public String login() {
        return "users/login";
    }

//    @GetMapping("/logout")
//    public String logout() {
//        return "logout";
//    }
}
