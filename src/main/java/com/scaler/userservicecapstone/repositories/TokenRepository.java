package com.scaler.userservicecapstone.repositories;

import com.scaler.userservicecapstone.models.Token;
import com.scaler.userservicecapstone.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<Token, Integer> {
    // Custom query methods
    Optional<Token> findByTokenValue(String tokenValue);
    List<Token> findAllByUser(User user);
    void deleteAllByUser(User user);
    Optional<Token> findByTokenValueAndDeletedAndExpiryDateGreaterThan(String tokenValue, boolean deleted, Date expiryDate);
}