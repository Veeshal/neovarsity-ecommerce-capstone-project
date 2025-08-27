package com.capstone.ecommerce.user.repository;

import com.capstone.ecommerce.user.entity.AppUser;
import com.capstone.ecommerce.user.entity.UserAddress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserAddressRepository extends JpaRepository<UserAddress, Long> {

    Optional<UserAddress> findByUserAndId(AppUser user, Long addressId);

    long deleteByUserAndId(AppUser user, Long id);
}