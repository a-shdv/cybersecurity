package com.company.cybersecurity.services;

import com.company.cybersecurity.exceptions.OldPasswordIsWrongException;
import com.company.cybersecurity.exceptions.PasswordsMismatch;
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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @PostConstruct
    private void postConstruct() {
        User user = Optional.ofNullable(userRepository.findByUsername("user")).orElse(new User("user", bCryptPasswordEncoder.encode("user")));
        user.setRoles(Collections.singletonList(Role.USER));
        user.setPasswordLastChanged(LocalDateTime.now());

        User admin = Optional.ofNullable(userRepository.findByUsername("admin")).orElse(new User("admin", bCryptPasswordEncoder.encode("admin")));
        admin.setRoles(Collections.singletonList(Role.ADMIN));
        admin.setPasswordLastChanged(LocalDateTime.now());
        userRepository.save(user);
        userRepository.save(admin);
    }


    public User findUserById(Long userId) throws UserNotFoundException {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь не найден!"));
    }

    public User findUserByUsername(String username) {
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
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
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

    public boolean isPasswordsMatch(String password, String confirmPassword) throws PasswordsMismatch {
        if (!password.equals(confirmPassword))
            throw new PasswordsMismatch("Пароли не совпадают!");
        return true;
    }

    public boolean isOldPasswordRight(String password, String oldPassword) throws OldPasswordIsWrongException {
        if (!bCryptPasswordEncoder.matches(password, oldPassword)) {
            throw new OldPasswordIsWrongException("Старый пароль не верный!");
        }
        return true;
    }

    public void changePassword(User user, String newPassword) {
        user.setPassword(bCryptPasswordEncoder.encode(newPassword));
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

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return Optional.ofNullable(userRepository.findByUsername(username))
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь с именем " + username + "не найден!"));
    }
}
