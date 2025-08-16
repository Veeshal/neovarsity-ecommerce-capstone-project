package com.capstone.ecommerce.user.repository;

import com.capstone.ecommerce.user.entity.AppUser;
import com.capstone.ecommerce.user.entity.PasswordResetToken;
import com.capstone.ecommerce.user.entity.PasswordTokenStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PasswordResetTokensRepository extends JpaRepository<PasswordResetToken, Long> {

  Optional<PasswordResetToken> findByUserAndStatus(AppUser appUser, PasswordTokenStatus status);

  Optional<PasswordResetToken> findByUserAndToken(AppUser user, String tokenValue);
}