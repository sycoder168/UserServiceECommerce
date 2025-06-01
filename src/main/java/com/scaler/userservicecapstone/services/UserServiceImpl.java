package com.scaler.userservicecapstone.services;

import com.scaler.userservicecapstone.models.Token;
import com.scaler.userservicecapstone.models.User;
import com.scaler.userservicecapstone.repositories.TokenRepository;
import com.scaler.userservicecapstone.repositories.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
public class UserServiceImpl implements UserService {

//    private static final SecretKey SECRET_KEY = Jwts.SIG.HS256.key().build();

    private static final long EXPIRATION_TIME_IN_MS = 1000 * 60 * 60 * 10;

    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final SecretKey secretKey;

    public UserServiceImpl(UserRepository userRepository, TokenRepository tokenRepository, BCryptPasswordEncoder bCryptPasswordEncoder,
                           SecretKey secretKey) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.secretKey = secretKey;
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

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + EXPIRATION_TIME_IN_MS);

        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getId());
        claims.put("email", user.getEmail());

        String jsonString = Jwts.builder()
                .claims(claims)
                .subject(user.getEmail())
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(secretKey)
                .compact();

        Token token = new Token();
        token.setUser(user);

//        token.setTokenValue(RandomStringUtils.random(
//                128,
//                48,
//                123,
//                true,
//                true,
//                null,
//                new Random()
//        ));

        token.setTokenValue(jsonString);

//        Calendar calendar = Calendar.getInstance();
//        calendar.add(Calendar.DATE, 30);
//        Date date = calendar.getTime();

        token.setExpiryDate(expiryDate);

        return token;

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
        if (tokenValue == null || tokenValue.isBlank()) {
            return null;
        }

        Claims claims;

        try {
            claims = Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(tokenValue)
                    .getPayload();
        } catch (JwtException e) {
            System.out.println("Token Validation failed. Invalid JWT token: " + e.getMessage());
            return null;
        } catch (IllegalArgumentException e) {
            System.out.println("Token Validation failed. Token value is null or empty or only whitespace: " + e.getMessage());
            return null;
        }

        String email = claims.getSubject();
        if (email == null || email.isBlank()) {
            System.out.println("Token Validation failed. Email is null or blank");
            return null;
        }

        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isEmpty() || userOptional.get().isDeleted()) {
            System.out.println("Token Validation failed. User not found or deleted");
            return null;
        }

        return userOptional.get();
    }


    private User validateNonJwtTokenInDB(String tokenValue) {
        Optional<Token> tokenOptional = tokenRepository.findByTokenValueAndDeletedAndExpiryDateGreaterThan(tokenValue, false, new Date());

        if (tokenOptional.isEmpty()) {
            return null;
        }

        Token token = tokenOptional.get();
        return token.getUser();
    }
}
