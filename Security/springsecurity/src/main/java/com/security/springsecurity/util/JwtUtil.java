package com.security.springsecurity.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtUtil {
    
    // JWT tokenlarni imzolash uchun maxfiy kalit ( uni xavfsiz saqlash muhim)
    private String SECRET_KEY = "PqUskgk6fbXP020QdKgWWzZIDsHebygVGuQmJHUQ1cc=";

    // Token ichidan foydalanuvchining nomini chiqaradi
    public String extractUsername(String token) {
        // `subject` maydonidan foydalanuvchi nomini qaytaradi
        return extractClaim(token, Claims::getSubject);
    }

    // Token ichidan muddati tugash sanasini chaqiradi  
    public Date extractExpiration(String token) {
        // `expiration` maydonidan tokenning tugash vaqtini qaytaradi
        return extractClaim(token, Claims::getExpiration);
    }

    // Token ichidagi ma'lum bir da'voni(claim) chaqiradi
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        // Tokenni parser yordamida barcha claimlarni oladi
        final Claims claims = extractAllClaims(token);
        // So'ralgan da'voni qaytaradi (masalan, username yoki muddati)
        return claimsResolver.apply(claims);
    }

    // Token ichidagi barcha ma'lumotlarni (claims) chiqaradi
    private Claims extractAllClaims(String token) {
        // JWT tokenni parser yordamida o'qiydi va ma'lumotlarni chiqaradi
        return Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody();
    }

    // Token muddati tugagan yoki tugamaganligini tekshiradi 
    private Boolean isTokenExpired(String token) {
        // Tokenning tugash sanasini hozirgi vaqt bilan solishtiradi 
        return extractExpiration(token).before(new Date());
    }

    // Yangi JWT token generatsiya  qiladi 
    public String generateToken(UserDetails userDetails) {
        // Token uchun bo'sh claimlar yaratadi 
        Map<String, Object> claims  = new HashMap<>();
        // Token yaratishi uchun yordamchi metodni chaqiradi
        return createToken(claims, userDetails.getUsername());
    }

    // Yangi token yaratadi
    public String createToken(Map<String, Object> claims, String subject) {
        // Tokenni quydagi ma'lumotlar bilan yaratadi:
        // -Da'volar(claims)
        // - Subject (foydalanuvhci nomi)
        // - yaratilgan vaqt(issuedAt)
        // - Tugash vaqt (expiritation)
        // Imzo algoritmi( HS256) va maxfiy kalit
        return Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(new Date(System.currentTimeMillis()))
                            .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10))
                            .signWith(SignatureAlgorithm.HS256, SECRET_KEY).compact(); // 10 soatlik amal qilish muddati 
    }

    // Token foydalanuvchi ma'lumotlari bilan tekshiradi 
    public Boolean validateToken(String token, UserDetails userDetails) {
        // token ichidagi foydalanuvchi nomini chiqaradi
        final String username = extractUsername(token);
        // Foydalanuvchi nomini va tokenning muddatli tugmamaganligini tekshiradi
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    
}
