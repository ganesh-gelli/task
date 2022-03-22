package com.login;

import com.login.dto.LoginRequest;
import com.login.dto.OTPValidateRequest;
import com.login.dto.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@CrossOrigin
public class AuthenticationController {

    @Autowired
    private AuthenticationServiceImpl authenticationService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse> singUp(@RequestBody LoginRequest loginRequest) {
        return authenticationService.login(loginRequest);
    }

    @PostMapping("/opt-validation")
    public ResponseEntity<ApiResponse> validateOtp(@RequestBody OTPValidateRequest validateRequest) {
        return authenticationService.otpValidation(validateRequest);
    }

    @GetMapping("/force-logout/{email}")
    public ResponseEntity<ApiResponse> forceLogout(@PathVariable String email) {
        return authenticationService.logOutUser(email);
    }

    @GetMapping("/logout")
    public ResponseEntity<ApiResponse> logout(@RequestHeader(value="Authorization") String token) {
        return authenticationService.logOut(token);
    }

}
