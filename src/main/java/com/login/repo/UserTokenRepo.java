package com.login.repo;

import com.login.entity.AppUser;
import com.login.entity.Token;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserTokenRepo extends MongoRepository<Token, String> {
    List<Token> findByUserAndIsActiveTrue(AppUser email);

    List<Token> findByJwtAndIsActiveTrue(String token);
}
