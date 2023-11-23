package com.company.cybersecurity.models;

import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


@Entity
@Table(name = "users")
@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode(exclude = {"password", "isAccountNonLocked", "roles"})
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Size(min = 0, max = 255, message = "Имя пользователя не может превышать 255 символов!")
    private String username;

    @Size(min = 0, max = 255, message = "Электронная не может превышать 255 символов!")
    private String email;

    @Size(min = 0, max = 255, message = "Имя пользователя не может превышать 255 символов!")
    private String password;

    private boolean isAccountNonLocked = true;

    private boolean isEnabled = true;

    private LocalDateTime passwordLastChanged;

    private boolean isPasswordNotRestricted = true;

    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "roles", joinColumns = @JoinColumn(name = "user_id"))
    @ElementCollection(targetClass = Role.class, fetch = FetchType.EAGER)
    private List<Role> roles = new ArrayList<>();

    public User(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }

    public boolean getEnabled() {
        return this.isEnabled;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return getRoles();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return isAccountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        if (passwordLastChanged != null) {
            LocalDateTime now = LocalDateTime.now();
            long daysSinceLastChange = ChronoUnit.DAYS.between(passwordLastChanged, now);
            return daysSinceLastChange <= 30;
        }
        return true;
    }

    @Override
    public boolean isEnabled() {
        if (!isEnabled || !isPasswordNotRestricted)
            return false;
        return true;
    }



    public void setPasswordNotRestricted(boolean passwordNotRestricted) {
        this.isPasswordNotRestricted = passwordNotRestricted;
    }
}
