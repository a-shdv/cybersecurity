package com.company.cybersecurity.controllers;

import com.company.cybersecurity.exceptions.UserNotFoundException;
import com.company.cybersecurity.models.Role;
import com.company.cybersecurity.models.User;
import com.company.cybersecurity.services.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@Slf4j
public class AdminController {
    private final UserService userService;

    @Autowired
    public AdminController(UserService userService) {
        this.userService = userService;
    }



    @GetMapping("/users")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String findAllUsers(Model model) {
        model.addAttribute("users", userService.findAllUsers());
        return "admins/user-list";
    }

    @GetMapping("/users/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String findUserById(@PathVariable("id") Long id, Model model) throws UserNotFoundException {
        User userFromDb = userService.findUserById(id);
        if (userFromDb != null) {
            model.addAttribute("users", userService.findUserById(id));
        } else {
            throw new UserNotFoundException("User with id " + id + " was not found");
        }

        return "admins/user-id";
    }
}
