package com.company.cybersecurity.controllers;

import com.company.cybersecurity.dto.RegistrationDto;
import com.company.cybersecurity.models.User;
import com.company.cybersecurity.services.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.Buffer;
import java.nio.file.Paths;

@Controller
@Slf4j
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

    @GetMapping("/login-file")
    public String loginFile() {
        return "login-file";
    }

    @PostMapping("/login-file")
    public String loginFile(@RequestParam("file") MultipartFile file, Model model) throws IOException {
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            String line = null;
            String username, password;
            while ((line = bufferedReader.readLine()) != null) {
                int usernameIdx = line.indexOf("username: ");
                username = line.substring(usernameIdx + 10);
                line = bufferedReader.readLine();
                int passwordIndex = line.indexOf("password: ");
                password = line.substring(passwordIndex + 10);
                model.addAttribute("username", username);
                model.addAttribute("password", password);
                break;
            }
        } catch (IOException e) {
            log.info(e.getMessage());
        }
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
