package com.scaler.userservicecapstone.services;

import com.scaler.userservicecapstone.models.Token;
import com.scaler.userservicecapstone.models.User;

public interface UserService {
    User signup(String name, String email, String password);
    Token login(String email, String password);
    Boolean logout(String token);
    User validateToken(String tokenValue);
}
