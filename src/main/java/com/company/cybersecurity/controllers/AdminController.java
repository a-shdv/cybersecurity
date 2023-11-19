package com.company.cybersecurity.controllers;

import com.company.cybersecurity.dtos.RegistrationDto;
import com.company.cybersecurity.dtos.SaveUserDto;
import com.company.cybersecurity.exceptions.PasswordsMismatch;
import com.company.cybersecurity.exceptions.UserAlreadyExistsException;
import com.company.cybersecurity.exceptions.UserNotFoundException;
import com.company.cybersecurity.models.Role;
import com.company.cybersecurity.models.User;
import com.company.cybersecurity.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("admin")
@PreAuthorize("hasAuthority('ADMIN')")
public class AdminController {
    private final UserService userService;

    @Autowired
    public AdminController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users")
    public String findAllUsers(Model model) {
        List<User> users = userService.findAllUsers()
                .stream()
                .filter(role -> role.getRoles().get(0).equals(Role.USER))
                .collect(Collectors.toList());
        model.addAttribute("users", users);
        return "admins/user-list";
    }

    @GetMapping("/users/{id}")
    public String findUserById(@PathVariable Long id, Model model) throws UserNotFoundException {
        User userFromDb = userService.findUserById(id);
        if (userFromDb != null) {
            model.addAttribute("user", userFromDb);
        } else {
            throw new UserNotFoundException("User with id " + id + " was not found");
        }
        return "admins/user-id";
    }

    @GetMapping("/users/save")
    public String saveUser() {
        return "admins/user-save";
    }

    @PostMapping("/users/save")
    public String saveUser(@ModelAttribute("saveUserDto") SaveUserDto dto, Model model) {
        User userFromDb = userService.findUserByUsername(dto.getUsername());
        String message = null;
        try {
            boolean isPasswordsMatch = userService.isPasswordsMatch(dto.getPassword(), dto.getConfirmPassword());
            boolean isAlreadyExists = userService.isAlreadyExists(userFromDb);
            if (isPasswordsMatch && !isAlreadyExists) {
                userService.saveUser(SaveUserDto.toUser(dto));
            }
            message = "Пользователь " + dto.getUsername() + " успешно создан!";
        } catch (PasswordsMismatch | UserAlreadyExistsException e) {
            message = e.getLocalizedMessage();
        }
        model.addAttribute("message", message);

        return "admins/user-save";
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

    @GetMapping("/lock-name")
    public String lockName(@RequestParam("userId") Long userId, Model model) {
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
