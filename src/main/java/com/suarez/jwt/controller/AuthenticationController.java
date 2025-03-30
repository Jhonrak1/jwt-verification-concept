package com.suarez.jwt.controller;

import com.suarez.jwt.dto.LoginUserDto;
import com.suarez.jwt.dto.RegisterUserDto;
import com.suarez.jwt.dto.VerifyUserDto;
import com.suarez.jwt.model.User;
import com.suarez.jwt.responses.LoginResponse;
import com.suarez.jwt.service.AuthenticationService;
import com.suarez.jwt.service.JwtService;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/auth")
@RestController
public class AuthenticationController {

    private final JwtService jwtService;
    private final AuthenticationService authenticationService;

    public AuthenticationController(JwtService jwtService, AuthenticationService authenticationService) {
        this.jwtService = jwtService;
        this.authenticationService = authenticationService;
    }

    @PostMapping("/signup")
    public ResponseEntity<User> register(@RequestBody RegisterUserDto registerUserDto){
        User registeredUser = authenticationService.signUp(registerUserDto);
        return ResponseEntity.ok(registeredUser);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> authenticate(@RequestBody LoginUserDto loginUserDto) {
        User authenticatedUser = authenticationService.userAuthenticate(loginUserDto);
        String jwtToken = jwtService.generateToken(authenticatedUser);
        LoginResponse loginResponse = new LoginResponse(jwtToken, jwtService.getExpirationTime());
        return ResponseEntity.ok(loginResponse);
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verifyUser(@RequestBody VerifyUserDto verifyUserDto){
        try {
            authenticationService.verifyUser(verifyUserDto);
            return ResponseEntity.ok("Account verified successfully");
        } catch (RuntimeException exception){
            return ResponseEntity.badRequest().body(exception.getLocalizedMessage());
        }
    }

    @PostMapping("/resend")
    public ResponseEntity<?> resendVerification(@RequestParam String email){
        try{
            authenticationService.resendVerificationCode(email);
            return ResponseEntity.ok("Verification code has been sent");
        } catch (RuntimeException exception){
            return ResponseEntity.badRequest().body(exception.getLocalizedMessage());
        }

    }

}
