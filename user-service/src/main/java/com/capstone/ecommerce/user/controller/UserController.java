package com.capstone.ecommerce.user.controller;

import com.capstone.ecommerce.user.dto.AppUserDto;
import com.capstone.ecommerce.user.dto.ListAppUserResponse;
import com.capstone.ecommerce.user.exceptions.InvalidUserPrincipalException;
import com.capstone.ecommerce.user.exceptions.UserDoesNotExistException;
import com.capstone.ecommerce.user.service.AppUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("v1/users")
public class UserController {

    private final AppUserService appUserService;

    @GetMapping("all")
    public ListAppUserResponse getUsers() {
        return new ListAppUserResponse(
                appUserService.getAllUsers().stream().map(AppUserDto::from)
                        .toList()
        );
    }

    @GetMapping
    public AppUserDto getUser(@AuthenticationPrincipal Jwt jwt) {

        log.info("getUser {}", jwt);

//        jwt.getHeaders().forEach((s, o) -> log.info("Headers >> {}: {}", s, o));
//        jwt.getClaims().forEach((s, o) -> log.info("Claims >> {}: {}", s, o));

        String email = (String) jwt.getClaims().get("email");

        var user = appUserService.getUserByEmail(email).orElseThrow(() ->
                new InvalidUserPrincipalException(email));


        return AppUserDto.from(user);
    }

    @PutMapping
    public AppUserDto getUser(AppUserDto appUserDto) {
        return null; // TODO: Replace with actual implementation
    }


    @PostMapping("password-reset/request")
    public AppUserDto changePasswordRequest(@RequestBody AppUserDto appUserDto) {
        // Logic to change user password
        return null; // TODO: Replace with actual implementation
    }

    @PostMapping("password-reset/confirm")
    public AppUserDto changePasswordRequestConfirm(@RequestBody AppUserDto appUserDto) {
        // Logic to change user password
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
