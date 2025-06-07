package com.capstone.ecommerce.user.controller;

import com.capstone.ecommerce.user.dto.AppUserDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("profile")
public class UserController {


    @GetMapping
    public AppUserDto getUser(Authentication authentication) {

        log.info("getUser {}", authentication);

        return null;
    }

    @PutMapping
    public AppUserDto getUser(AppUserDto appUserDto) {
        return null; // TODO: Replace with actual implementation
    }


    @PostMapping("address")
    public AppUserDto addUserAddress() {
        return null; // TODO: Replace with actual implementation
    }

    @PutMapping("address/{id}")
    public AppUserDto updateUserAddress(@PathVariable("id") Long addressId) {
        return null; // TODO: Replace with actual implementation
    }

    @DeleteMapping("address/{id}")
    public AppUserDto deleteUserAddress(@PathVariable("id") Long addressId) {
        // Logic to delete user address by ID
        return null; // TODO: Replace with actual implementation
    }

}
