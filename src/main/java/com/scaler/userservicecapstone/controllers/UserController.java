package com.scaler.userservicecapstone.controllers;

import com.scaler.userservicecapstone.dtos.*;
import com.scaler.userservicecapstone.models.Token;
import com.scaler.userservicecapstone.models.User;
import com.scaler.userservicecapstone.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/signup")
    public UserDto signup(@RequestBody SignUpRequestDto signUpRequestDto) {
        User user = userService.signup(signUpRequestDto.getName(), signUpRequestDto.getEmail(),
                signUpRequestDto.getPassword());
        return UserDto.from(user);
    }

    @PostMapping("/login")
    public LoginResponseDto login(@RequestBody LoginRequestDto loginRequestDto) {
        Token token = userService.login(loginRequestDto.getEmail(), loginRequestDto.getPassword());
        LoginResponseDto loginResponseDto = new LoginResponseDto();
        loginResponseDto.setTokenValue(token.getTokenValue());
        return loginResponseDto;
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestBody LogoutRequestDto logoutRequestDto) {
        if (userService.logout(logoutRequestDto.getTokenValue())) {
            return ResponseEntity.ok().build();
        } else return ResponseEntity.internalServerError().build();
    }

    @GetMapping("/validate/{token}")
    public ResponseEntity<Boolean> validateToken(@PathVariable("token") String token) {
        User user = userService.validateToken(token);

        return user == null ? new ResponseEntity<>(false, HttpStatus.UNAUTHORIZED) :
                new ResponseEntity<>(true, HttpStatus.OK);
    }
}
