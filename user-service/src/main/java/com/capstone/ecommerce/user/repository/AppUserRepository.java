package com.capstone.ecommerce.user.repository;

import com.capstone.ecommerce.user.entity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AppUserRepository extends JpaRepository<AppUser, Long> {

    Optional<AppUser> findByEmail(String email);

    Optional<AppUser> findByPhoneNumber(String phoneNumber);

}