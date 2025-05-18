package com.capstone.ecommerce.user.entity;

import jakarta.persistence.Entity;
import org.springframework.security.core.GrantedAuthority;

@Entity
public class Role extends BaseEntity implements GrantedAuthority {

    private String name;

    @Override
    public String getAuthority() {
        return name;
    }
}
