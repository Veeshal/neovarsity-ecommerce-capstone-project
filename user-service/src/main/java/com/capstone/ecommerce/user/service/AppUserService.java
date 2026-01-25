package com.capstone.ecommerce.user.service;

import com.capstone.ecommerce.user.dto.AddressDto;
import com.capstone.ecommerce.user.dto.AppUserDto;
import com.capstone.ecommerce.user.entity.AppUser;
import com.capstone.ecommerce.user.entity.PasswordResetToken;
import com.capstone.ecommerce.user.entity.PasswordTokenStatus;
import com.capstone.ecommerce.user.entity.UserAddress;
import com.capstone.ecommerce.user.exceptions.InvalidUserAddressException;
import com.capstone.ecommerce.user.repository.AppUserRepository;
import com.capstone.ecommerce.user.repository.PasswordResetTokensRepository;
import com.capstone.ecommerce.user.repository.UserAddressRepository;
import com.capstone.ecommerce.user.stream.producer.PasswordResetProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class AppUserService implements UserDetailsService {

    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final PasswordResetTokensRepository passwordResetTokensRepository;
    private final PasswordResetProducer passwordResetProducer;
    private final UserAddressRepository userAddressRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return appUserRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
    }

    public Optional<AppUser> getUserByEmail(String email) {
        return appUserRepository.findByEmail(email);
    }

    public AppUser getUserByEmailWithAddress(String email) {
        return appUserRepository.findByEmailWithAddress(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
    }

    public List<AppUser> getAllUsers() {
        return appUserRepository.findAll();
    }

    @Transactional
    public AppUser registerUser(String email, String password, String name) {
        var user = getUserByEmail(email);

        if (user.isPresent()) {
            throw new IllegalArgumentException("User already exists with email: " + email);
        }

        var newUser = new AppUser();
        newUser.setEmail(email);
        newUser.setPassword(passwordEncoder.encode(password));
        newUser.setName(name);
        appUserRepository.save(newUser);
        return newUser;
    }

    @Transactional
    public AppUser registerSocialUser(AppUser newUser) {
        var userOpt = appUserRepository.findByEmail(newUser.getEmail());

        if (userOpt.isPresent()) {
            AppUser existingUser = userOpt.get();
            // Only allow login if the social provider matches
            if (existingUser.getSocialLogin() != null && existingUser.getSocialLogin().equals(newUser.getSocialLogin())) {
                // Optionally update name if changed
                if (!existingUser.getName().equals(newUser.getName())) {
                    existingUser.setName(newUser.getName());
                    appUserRepository.save(existingUser);
                }
                return existingUser;
            } else {
                throw new IllegalArgumentException("User already exists with this email using a different login method.");
            }
        }

        appUserRepository.save(newUser);
        return newUser;
    }

    @Transactional
    public AppUser updateUser(AppUserDto updateUser) {

        var user = getUserByEmail(updateUser.email())
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + updateUser.email()));

        user.setName(updateUser.name());
        user.setPhoneNumber(updateUser.phone());
        return appUserRepository.save(user);

    }

    /**
     * Logic to handle password reset request
     * This could involve sending an email with a reset link
     *
     * @param email String email of the user requesting password reset
     * @throws UsernameNotFoundException if the user with the given email does not exist
     *
     */
    @Transactional
    public void passwordResetRequest(String email) {
        log.info("Password reset requested for user: {}", email);

        AppUser user = appUserRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        var passwordToken = passwordResetTokensRepository.findByUserAndStatus(user, PasswordTokenStatus.ACTIVE);

        if (passwordToken.isPresent()) {
            var token = passwordToken.get();
            log.info("Password reset token already exists for user: {} which expiresAt: {}", email, token.getExpiresAt());

            if (token.getExpiresAt().isAfter(LocalDateTime.now())) {
                log.info("Existing token is still valid. Not generating a new one.");
                return;
            } else {
                token.setStatus(PasswordTokenStatus.EXPIRED);
                passwordResetTokensRepository.save(token);
            }
        }


        var tokenValue = UUID.randomUUID().toString();

        PasswordResetToken token = new PasswordResetToken();
        token.setUser(user);
        token.setToken(tokenValue);
        token.setExpiresAt(LocalDateTime.now().plusMinutes(10)); // Token valid for 1 hour
        token.setStatus(PasswordTokenStatus.ACTIVE);
        passwordResetTokensRepository.save(token);

        // use kafka to send event to notification service for sending email
        passwordResetProducer.sendPasswordResetEvent(token);

    }

    @Transactional
    public void confirmPasswordReset(String email, String tokenValue, String newPassword, String confirmPassword) {
        if (!newPassword.equals(confirmPassword)) {
            throw new IllegalArgumentException("New password and confirmation do not match");
        }

        log.info("Confirming password reset for user: {}", email);

        AppUser user = appUserRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        PasswordResetToken token = passwordResetTokensRepository.findByUserAndToken(user, tokenValue)
                .orElseThrow(() -> new IllegalArgumentException("Invalid password reset token"));

        if (token.getStatus() != PasswordTokenStatus.ACTIVE) {
            throw new IllegalArgumentException("Token is not active or has already been used");
        }

        if (token.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Token has expired");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        appUserRepository.save(user);

        // Mark the token as consumed
        token.setStatus(PasswordTokenStatus.CONSUMED);
        passwordResetTokensRepository.save(token);
    }

    @Transactional
    public UserAddress addAddress(AddressDto newAddress, String email) {


        AppUser user = appUserRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));


        UserAddress address = new UserAddress();
        address.setUser(user);
        address.setCreatedAt(LocalDateTime.now());

        updateUserAddress(newAddress, address);
        return userAddressRepository.save(address);

    }


    @Transactional
    public UserAddress updateAddress(Long addressId, AddressDto updatedAddress, String email) {

        AppUser user = appUserRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        var address = userAddressRepository.findByUserAndId(user, addressId)
                .orElseThrow(() -> new InvalidUserAddressException(addressId));
        address.setUpdatedAt(LocalDateTime.now());

        updateUserAddress(updatedAddress, address);
        return userAddressRepository.save(address);

    }

    @Transactional
    public void deleteAddress(Long addressId, String email) {

        AppUser user = appUserRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        var address = userAddressRepository.findByUserAndId(user, addressId)
                .orElseThrow(() -> new InvalidUserAddressException(addressId));

        userAddressRepository.delete(address);
    }

    private void updateUserAddress(AddressDto updatedAddress, UserAddress address) {
        address.setAddressLine1(updatedAddress.addressLine1());
        address.setAddressLine2(updatedAddress.addressLine2());
        address.setAddressName(updatedAddress.addressName());
        address.setCity(updatedAddress.city());
        address.setState(updatedAddress.state());
        address.setCountry(updatedAddress.country());
        address.setPostalCode(updatedAddress.postalCode());
        address.setPhoneNumber(updatedAddress.phoneNumber());
        address.setLatitude(updatedAddress.latitude());
        address.setLongitude(updatedAddress.longitude());
        address.setDefault(updatedAddress.isDefault());
    }

}