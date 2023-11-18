package com.company.cybersecurity.controllers;

import com.company.cybersecurity.exceptions.UserNotFoundException;
import com.company.cybersecurity.models.Role;
import com.company.cybersecurity.models.User;
import com.company.cybersecurity.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("admin")
public class AdminController {
    private final UserService userService;

    @Autowired
    public AdminController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String findAllUsers(Model model) {
        List<User> users = userService.findAllUsers()
                .stream()
                .filter(role -> role.getRoles().get(0).equals(Role.USER))
                .collect(Collectors.toList());
        model.addAttribute("users", users);
        return "admins/user-list";
    }

    @GetMapping("/users/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String findUserById(@PathVariable Long id, Model model) throws UserNotFoundException {
        User userFromDb = userService.findUserById(id);
        if (userFromDb != null) {
            model.addAttribute("user", userFromDb);
        } else {
            throw new UserNotFoundException("User with id " + id + " was not found");
        }
        return "admins/user-id";
    }

    @GetMapping("/lock")
    public String lock(@RequestParam("userId") Long userId, Model model) {
        try {
            User userFromDb = userService.findUserById(userId);
            userService.lockUser(userFromDb);
            model.addAttribute("user", userFromDb);
        } catch (UserNotFoundException e) {
            e.getLocalizedMessage();
        }
        return "admins/user-blocked";
    }

    @GetMapping("/unlock")
    public String unlock(@RequestParam("userId") Long userId, Model model) {
        try {
            User userFromDb = userService.findUserById(userId);
            userService.unlockUser(userFromDb);
            model.addAttribute("user", userFromDb);
        } catch (UserNotFoundException e) {
            e.getLocalizedMessage();
        }
        return "admins/user-unblocked";
    }

}
