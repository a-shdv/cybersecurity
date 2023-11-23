package com.company.cybersecurity.controllers;

import com.company.cybersecurity.dtos.SaveUserDto;
import com.company.cybersecurity.exceptions.PasswordsMismatchException;
import com.company.cybersecurity.exceptions.UserAlreadyExistsException;
import com.company.cybersecurity.exceptions.UserNotFoundException;
import com.company.cybersecurity.exceptions.WrongPasswordFormatException;
import com.company.cybersecurity.models.Role;
import com.company.cybersecurity.models.User;
import com.company.cybersecurity.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
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

    @GetMapping("/user-list-blocked")
    public String findAllUsersBlocked(Model model) {
        List<User> users = userService.findAllUsers()
                .stream()
                .sorted(Comparator.comparingLong(User::getId))
                .filter(role -> role.getRoles().get(0).equals(Role.USER))
                .collect(Collectors.toList());
        model.addAttribute("users", users);
        return "admins/user-list-blocked";
    }

    @GetMapping("/user-list-disabled")
    public String findAllUsersDisabled(Model model) {
        List<User> users = userService.findAllUsers()
                .stream()
                .sorted(Comparator.comparingLong(User::getId))
                .filter(role -> role.getRoles().get(0).equals(Role.USER))
                .collect(Collectors.toList());
        model.addAttribute("users", users);
        return "admins/user-list-disabled";
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
        } catch (PasswordsMismatchException | UserAlreadyExistsException e) {
            message = e.getLocalizedMessage();
        } catch (WrongPasswordFormatException e) {
            message = "Пароль должен содержать строчные, прописные буквы, а также знаки арифметических операций, и должен быть > 3-х символов!";
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
        return "redirect:/admin/user-list-blocked";
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
        return "redirect:/admin/user-list-blocked";
    }

    @GetMapping("/enable-by-username")
    public String enableByUsername(@RequestParam("username") String username, Model model) {
        try {
            User userFromDb = userService.findUserByUsername(username);
            userService.enableByUsername(userFromDb.getUsername());
            model.addAttribute("user", userFromDb);
        } catch (UsernameNotFoundException e) {
            e.getLocalizedMessage();
        }
        return "redirect:/admin/user-list-disabled";
    }

    @GetMapping("/disable-by-username")
    public String disableByUsername(@RequestParam("username") String username, Model model) {
        try {
            User userFromDb = userService.findUserByUsername(username);
            userService.disableByUsername(userFromDb.getUsername());
            model.addAttribute("user", userFromDb);
        } catch (UsernameNotFoundException e) {
            e.getLocalizedMessage();
        }
        return "redirect:/admin/user-list-disabled";
    }

    @GetMapping("/restrict-password-characters")
    public String restrictPasswordCharacters(@RequestParam("id") Long id, Model model) throws UserNotFoundException, WrongPasswordFormatException {
        try {
            User user = userService.findUserById(id);
            userService.restrictPasswordCharacters(user);
            model.addAttribute("user", user);
        } catch (UserNotFoundException e) {
            throw new UserNotFoundException("Username with id " + id + " was not found!");
        } catch (WrongPasswordFormatException e) {
            throw new WrongPasswordFormatException("Пароль должен содержать строчные, прописные буквы, а также знаки арифметических операций, и должен быть > 3-х символов!");
        }
        return "redirect:/admin/user-list-disabled";
    }

    @GetMapping("/unrestrict-password-characters")
    public String unrestrictPasswordCharacters(@RequestParam("id") Long id, Model model) throws UserNotFoundException {
        try {
            User user = userService.findUserById(id);
            userService.unrestrictPasswordCharacters(user);
            model.addAttribute("user", user);
        } catch (UserNotFoundException e) {
            throw new UserNotFoundException("Username with id " + id + " was not found!");
        }
        return "redirect:/admin/user-list-disabled";

    }

    @GetMapping("/exit")
    public void exit() {
        System.exit(0);
    }
}
