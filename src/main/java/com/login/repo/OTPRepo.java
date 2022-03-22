package com.login.repo;

import com.login.entity.AppUser;
import com.login.entity.OTP;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OTPRepo extends MongoRepository<OTP, String> {
    Optional<OTP> findByUserAndOtpAndIsActiveTrue(AppUser email, String otp);
}
