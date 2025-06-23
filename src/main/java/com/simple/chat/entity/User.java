package com.simple.chat.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column()
    private String name;

    @Column()
    private String password;

    @Column(name = "login_at")
    private LocalDateTime loginAt;

    @CreatedDate
    @Column(name = "created_at", nullable = false)
    protected LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    protected LocalDateTime updatedAt;

    @Builder
    public User(Long id, String email, String name, String password, LocalDateTime loginAt) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.password = password;
        this.loginAt = loginAt;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("user"));
    }

    @Override
    public String getUsername() {
        return name;
    }
}
