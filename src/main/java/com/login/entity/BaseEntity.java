package com.login.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.*;
import org.springframework.data.mongodb.core.mapping.Document;

import java.sql.Timestamp;
import java.util.Date;

@Document
@Getter
@Setter
public class BaseEntity {

    @Id
    private String id;

    @CreatedDate
    private Date createdDate = new Timestamp(new Date().getTime());

    @LastModifiedDate
    private Date lastUpdate = new Timestamp(new Date().getTime());

}
