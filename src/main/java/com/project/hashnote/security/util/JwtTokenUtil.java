package com.project.hashnote.security.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.function.Function;

@Component
public class JwtTokenUtil {
    @Value("${jwt.expire}")
    private int TIME_TO_EXPIRE;
    @Value("${jwt.secret}")
    private String base64Secret;

    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    public boolean isExpired(String token) {
        Date now = new Date();
        return getExpirationDateFromToken(token).before(now);
    }

    private <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        Claims claims = getClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    private Claims getClaimsFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(base64Secret)
                .parseClaimsJws(token)
                .getBody();
    }

    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + TIME_TO_EXPIRE))
                .signWith(SignatureAlgorithm.HS512, base64Secret)
                .compact();
    }
}
