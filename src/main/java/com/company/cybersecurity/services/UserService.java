package com.company.cybersecurity.services;

import com.company.cybersecurity.exceptions.OldPasswordIsWrongException;
import com.company.cybersecurity.exceptions.PasswordsMismatchException;
import com.company.cybersecurity.exceptions.UserAlreadyExistsException;
import com.company.cybersecurity.exceptions.UserNotFoundException;
import com.company.cybersecurity.models.Role;
import com.company.cybersecurity.models.User;
import com.company.cybersecurity.repos.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.crypto.*;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostConstruct
    private void postConstruct() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, InvalidAlgorithmParameterException, InvalidKeyException, NoSuchPaddingException {
        User user = Optional.ofNullable(userRepository.findByUsername("user")).orElse(new User("user", "user@user.com", passwordEncoder.encode("user")));
        user.setRoles(Collections.singletonList(Role.USER));
        user.setPasswordLastChanged(LocalDateTime.now());

        User admin = Optional.ofNullable(userRepository.findByUsername("admin")).orElse(new User("admin", "admin@admin.com", passwordEncoder.encode("admin")));
        admin.setRoles(Collections.singletonList(Role.ADMIN));
        admin.setPasswordLastChanged(LocalDateTime.now());

        userRepository.save(user);
        userRepository.save(admin);

        // Создание файла
//        String content = findAllUsers().stream()
//                .sorted(Comparator.comparing(User::getId))
//                .map(User::toString)
//                .collect(Collectors.joining(System.lineSeparator()));


//        Path path = Paths.get("users.txt");
//        Files.write(path, content.getBytes());
    }


    public User findUserById(Long userId) throws UserNotFoundException {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь не найден!"));
    }

    public User findUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username);
    }

    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    public boolean isAlreadyExists(User user) throws UserAlreadyExistsException {
        if (user != null) {
            throw new UserAlreadyExistsException("Пользователь с таким именем уже существует!");
        }
        return false;
    }

    public void saveUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setPasswordLastChanged(LocalDateTime.now());
        user.setAccountNonLocked(true);
        user.setRoles(Collections.singletonList(Role.USER));
        userRepository.save(user);
    }


    public boolean deleteUser(Long userId) {
        if (userRepository.findById(userId).isPresent()) {
            userRepository.deleteById(userId);
            log.info("User with id " + userId + " has been deleted");
            return true;
        }

        log.info("Cannot delete user with id " + userId);
        return false;
    }

    public boolean isPasswordsMatch(String password, String confirmPassword) throws PasswordsMismatchException {
        if (!password.equals(confirmPassword))
            throw new PasswordsMismatchException("Пароли не совпадают!");
        return true;
    }

    public boolean isOldPasswordRight(String oldPassword, User user) throws OldPasswordIsWrongException {
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new OldPasswordIsWrongException("Старый пароль не верный!");
        }
        return true;
    }

    public void changeUsername(String newUsername, User user) {
        user.setUsername(newUsername);
        userRepository.save(user);
    }

    public void changePassword(String newPassword, User user) {
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setPasswordLastChanged(LocalDateTime.now());
        userRepository.save(user);
    }

    public void lockUser(User user) {
        user.setAccountNonLocked(false);
        userRepository.save(user);
    }

    public void unlockUser(User user) {
        user.setAccountNonLocked(true);
        userRepository.save(user);
    }

    public void disableByUsername(String username) {
        User user = userRepository.findByUsername(username);
        user.setEnabled(false);
        userRepository.save(user);
    }

    public void enableByUsername(String username) {
        User user = userRepository.findByUsername(username);
        user.setEnabled(true);
        userRepository.save(user);
    }

    public User findUserByEmail(String email) throws UserNotFoundException {
        return userRepository.findByEmail(email);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return Optional.ofNullable(userRepository.findByUsername(username))
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь с именем " + username + "не найден!"));
    }
}
