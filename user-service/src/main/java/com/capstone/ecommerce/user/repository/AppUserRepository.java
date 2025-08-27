package com.capstone.ecommerce.user.repository;

import com.capstone.ecommerce.user.entity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface AppUserRepository extends JpaRepository<AppUser, Long> {

    Optional<AppUser> findByEmail(String email);

    @Query("""
            SELECT u
            FROM AppUser u LEFT JOIN FETCH u.addresses
                        WHERE u.email = :email
            """)
    Optional<AppUser> findByEmailWithAddress(String email);

    Optional<AppUser> findByPhoneNumber(String phoneNumber);


}