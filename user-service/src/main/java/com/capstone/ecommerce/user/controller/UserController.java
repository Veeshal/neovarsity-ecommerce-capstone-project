package com.capstone.ecommerce.user.controller;

import com.capstone.ecommerce.user.dto.AppUserDto;
import com.capstone.ecommerce.user.dto.AddressDto;
import com.capstone.ecommerce.user.dto.ListAppUserResponse;
import com.capstone.ecommerce.user.dto.ResetPasswordConfirmRequest;
import com.capstone.ecommerce.user.dto.ResetPasswordRequest;
import com.capstone.ecommerce.user.service.AppUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

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

        var email = getEmailFromToken(jwt);
        var user = appUserService.getUserByEmailWithAddress(email);
        return AppUserDto.withAddress(user);
    }

    @PutMapping
    public AppUserDto updateUser(@RequestBody AppUserDto appUserDto) {
        var user = appUserService.updateUser(appUserDto);
        return AppUserDto.from(user);
    }


    @PostMapping("password-reset/request")
    public void resetPasswordRequest(@RequestBody ResetPasswordRequest request) {
        appUserService.passwordResetRequest(request.email());
    }

    @PostMapping("password-reset/confirm")
    public void resetPasswordRequestConfirm(@RequestBody ResetPasswordConfirmRequest request) {
        appUserService.confirmPasswordReset(request.email(), request.passwordResetToken(),
                request.newPassword(), request.confirmPassword());
    }

    @PostMapping("address")
    public AddressDto addUserAddress(@RequestBody AddressDto newAddress, @AuthenticationPrincipal Jwt jwt) {
        var email = getEmailFromToken(jwt);
        var address = appUserService.addAddress(newAddress, email);
        return AddressDto.from(address);
    }

    @PutMapping("address/{id}")
    public AddressDto updateUserAddress(@PathVariable("id") Long addressId, @RequestBody AddressDto updatedAddress,
                                        @AuthenticationPrincipal Jwt jwt) {
        var email = getEmailFromToken(jwt);
        var address = appUserService.updateAddress(addressId, updatedAddress, email);
        return AddressDto.from(address);
    }

    @DeleteMapping("address/{id}")
    public void deleteUserAddress(@PathVariable("id") Long addressId, @AuthenticationPrincipal Jwt jwt) {
        var email = getEmailFromToken(jwt);
        appUserService.deleteAddress(addressId, email);
    }

    private String getEmailFromToken(Jwt jwt) {
        log.info("getUser {}", jwt);

//        jwt.getHeaders().forEach((s, o) -> log.info("Headers >> {}: {}", s, o));
//        jwt.getClaims().forEach((s, o) -> log.info("Claims >> {}: {}", s, o));


        return  (String) jwt.getClaims().get("email");
    }

}
