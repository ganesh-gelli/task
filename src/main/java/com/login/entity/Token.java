package com.login.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Document
public class Token extends BaseEntity {

    private String jwt;
    @DBRef
    @Indexed
    private AppUser user;
    private Boolean isActive = Boolean.TRUE;
}
