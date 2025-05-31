package com.scaler.userservicecapstone.services;

import com.scaler.userservicecapstone.models.Token;
import com.scaler.userservicecapstone.models.User;
import com.scaler.userservicecapstone.repositories.TokenRepository;
import com.scaler.userservicecapstone.repositories.UserRepository;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.Optional;
import java.util.Random;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public UserServiceImpl(UserRepository userRepository, TokenRepository tokenRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }


    @Override
    public User signup(String name, String email, String password) {

        if (userRepository.findByEmail(email).isPresent()) {
            return null;
        }

        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(bCryptPasswordEncoder.encode(password));
        return userRepository.save(user);

    }

    @Override
    public Token login(String email, String password) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isEmpty()) {
            return null;
        }

        User user = userOptional.get();

        if (!bCryptPasswordEncoder.matches(password, user.getPassword())) {
            return null;
        }

        Token token = new Token();
        token.setUser(user);
        token.setTokenValue(RandomStringUtils.random(
                128,
                48,
                123,
                true,
                true,
                null,
                new Random()
        ));

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, 30);
        Date date = calendar.getTime();

        token.setExpiryDate(date);

        return tokenRepository.save(token);

    }

    @Override
    public Boolean logout(String tokenValue) {
        Optional<Token> tokenOptional = tokenRepository.findByTokenValue(tokenValue);
        if (tokenOptional.isEmpty()) {
            return null;
        }

        Token token = tokenOptional.get();

        token.setExpiryDate(new Date());
        tokenRepository.save(token);

        return true;
    }

    @Override
    public User validateToken(String tokenValue) {
        Optional<Token> tokenOptional = tokenRepository.findByTokenValueAndDeletedAndExpiryDateGreaterThan(tokenValue, false, new Date());

        if (tokenOptional.isEmpty()) {
            return null;
        }

        Token token = tokenOptional.get();
        return token.getUser();
    }
}
