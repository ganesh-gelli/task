package com.login.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
@Document
public class AppUser extends BaseEntity {
    private String email;
    private Boolean isActive = Boolean.TRUE;
    @DBRef
    private List<OTP> userOtp = new ArrayList<>();

    public String getUserEmail() {
        return this.email;
    }
}
