package com.capstone.ecommerce.user.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

@Getter
@Setter
@Entity
public class AppUser extends BaseEntity implements UserDetails {

    private String name;
    private String password;
    private String email;
    private String phoneNumber;

    @Enumerated(EnumType.ORDINAL)
    @Column(columnDefinition = "TINYINT")
    private SocialLogin socialLogin;

    @ManyToMany
    private List<Role> roles;

    @OneToMany(mappedBy = "user")
    private List<UserAddress> addresses;

    @Override
    public List<Role> getAuthorities() {
        return roles;
    }

    @Override
    public String getUsername() {
        return name;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
