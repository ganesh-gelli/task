package com.login;

import com.login.dto.LoginRequest;
import com.login.dto.LoginResponse;
import com.login.dto.OTPValidateRequest;
import com.login.dto.ApiResponse;
import com.login.entity.AppUser;
import com.login.entity.OTP;
import com.login.entity.Token;
import com.login.repo.AppUserRepo;
import com.login.repo.OTPRepo;
import com.login.repo.UserTokenRepo;
import com.login.util.CommonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.mail.MessagingException;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Optional;

@Service
public class AuthenticationServiceImpl  {
    @Autowired
    private AppUserRepo appUserRepo;
    @Autowired
    private UserTokenRepo userTokenRepo;
    @Autowired
    private OTPRepo otpRepo;
    @Value("${email.from}")
    private String from;

    
    public ResponseEntity<ApiResponse> login(LoginRequest loginRequest) {
        ResponseEntity<ApiResponse> response = null;
        response = validateLoginRequest(loginRequest);
        if (response != null) {
            return response;
        }
        Optional<AppUser> existingUser = appUserRepo.findByEmail(loginRequest.getEmail());
        if(!existingUser.isPresent()){
            AppUser appUser = new AppUser();
            appUser.setEmail(loginRequest.getEmail());
            appUserRepo.save(appUser);
        }
        Optional<AppUser> user = appUserRepo.findByEmail(loginRequest.getEmail());
        if (user.isPresent()) {
            AppUser appUser = user.get();
            List<Token> token = userTokenRepo.findByUserAndIsActiveTrue(appUser);
            if (!token.isEmpty()) {
                ApiResponse apiResponse = new ApiResponse(HttpStatus.FORBIDDEN, "a user is logged in with credentials");
                return new ResponseEntity<>(apiResponse, apiResponse.getStatus());
            }
            String otp = CommonUtil.genearteOTP(4);
            String message = otp + " is your otp for login ";
            try {
                CommonUtil.sendEmail(appUser.getEmail(), message, "OTP:Login", from);
            } catch (UnknownHostException | MessagingException e) {
                ApiResponse apiResponse = new ApiResponse(HttpStatus.INTERNAL_SERVER_ERROR, "failed to send OTP",e.getMessage());
                return new ResponseEntity<>(apiResponse, apiResponse.getStatus());
            }
            OTP userOtp = new OTP();
            userOtp.setOtp(otp);
            userOtp.setUser(appUser);
            userOtp.setIsActive(Boolean.TRUE);
            otpRepo.save(userOtp);
            ApiResponse apiResponse = new ApiResponse(HttpStatus.OK, "OTP has been sent to your email");
            return new ResponseEntity<>(apiResponse, apiResponse.getStatus());
        } else {
            ApiResponse apiResponse = new ApiResponse(HttpStatus.BAD_REQUEST, "email and password is incorrect");
            return new ResponseEntity<>(apiResponse, apiResponse.getStatus());
        }
    }

    
    public ResponseEntity<ApiResponse> otpValidation(OTPValidateRequest validateRequest) {
        ResponseEntity<ApiResponse> response = null;
        response = validateOTPRequest(validateRequest);
        if (response != null) {
            return response;
        }
        Optional<AppUser> optionalUser = appUserRepo.findByEmail(validateRequest.getEmail());
        AppUser appUser = null;
        appUser = optionalUser.orElseGet(AppUser::new);

        Optional<OTP> otp = otpRepo.findByUserAndOtpAndIsActiveTrue(appUser, validateRequest.getOtp());
        if (otp.isPresent()) {
            String token = CommonUtil.generateRandomToken();
            LoginResponse loginResponse = new LoginResponse();
            loginResponse.setUserEmail(appUser.getUserEmail());
            loginResponse.setToken(token);
            OTP updateOTP = otp.get();
            updateOTP.setIsActive(Boolean.FALSE);
            Token userToken = new Token();
            userToken.setIsActive(Boolean.TRUE);
            userToken.setJwt(token);
            userToken.setUser(appUser);
            userTokenRepo.save(userToken);
            otpRepo.save(updateOTP);
            ApiResponse apiResponse = new ApiResponse(HttpStatus.OK, "otp verified", loginResponse);
            return new ResponseEntity<>(apiResponse, apiResponse.getStatus());
        } else {
            ApiResponse apiResponse = new ApiResponse(HttpStatus.BAD_REQUEST, "in-valid otp");
            return new ResponseEntity<>(apiResponse, apiResponse.getStatus());
        }
    }

    private ResponseEntity<ApiResponse> validateLoginRequest(LoginRequest loginRequest) {
        if (!StringUtils.hasLength(loginRequest.getEmail())) {
            ApiResponse apiResponse = new ApiResponse(HttpStatus.BAD_REQUEST, "email is required");
            return new ResponseEntity<>(apiResponse, apiResponse.getStatus());
        }
        return null;
    }

    private ResponseEntity<ApiResponse> validateOTPRequest(OTPValidateRequest loginRequest) {
        if (!StringUtils.hasLength(loginRequest.getEmail()) || !StringUtils.hasLength(loginRequest.getOtp())) {
            ApiResponse apiResponse = new ApiResponse(HttpStatus.BAD_REQUEST, "OTP and email is required");
            return new ResponseEntity<>(apiResponse, apiResponse.getStatus());
        }
        return null;
    }

    
    public ResponseEntity<ApiResponse> logOutUser(String email) {
        ResponseEntity<ApiResponse> response = null;
        Optional<AppUser> user = appUserRepo.findByEmail(email);
        if (user.isPresent()) {
            AppUser appUser = user.get();
            List<Token> userToken = userTokenRepo.findByUserAndIsActiveTrue(appUser);
            if (!userToken.isEmpty()) {
                userToken.forEach(token -> token.setIsActive(Boolean.FALSE));
                userTokenRepo.saveAll(userToken);
            }
            ApiResponse apiResponse = new ApiResponse(HttpStatus.OK, "User Logged out successfully");
            return new ResponseEntity<>(apiResponse, apiResponse.getStatus());

        } else {
            ApiResponse apiResponse = new ApiResponse(HttpStatus.BAD_REQUEST, "No User Found");
            return new ResponseEntity<>(apiResponse, apiResponse.getStatus());
        }
    }

    
    public ResponseEntity<ApiResponse> logOut(String loginToken) {
        List<Token> userToken = userTokenRepo.findByJwtAndIsActiveTrue(loginToken);
        if (!userToken.isEmpty()) {
            userToken.forEach(token -> token.setIsActive(Boolean.FALSE));
            userTokenRepo.saveAll(userToken);
            ApiResponse apiResponse = new ApiResponse(HttpStatus.OK, "User Logged out successfully");
            return new ResponseEntity<>(apiResponse, apiResponse.getStatus());
        } else {
            ApiResponse apiResponse = new ApiResponse(HttpStatus.BAD_REQUEST, "User has already been logged out");
            return new ResponseEntity<>(apiResponse, apiResponse.getStatus());
        }
    }


}
