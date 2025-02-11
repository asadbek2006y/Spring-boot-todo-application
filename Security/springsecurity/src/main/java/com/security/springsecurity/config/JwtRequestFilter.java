package com.security.springsecurity.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import com.security.springsecurity.service.CustomUserDetailsService;
import com.security.springsecurity.util.JwtUtil;

@Component // Spring komponenti sifatida belgilangan (avtomatik skan qilish va inject qilish uchun)
@AllArgsConstructor // Lombok tomonidan barcha argumentlarga ega konstruktorni yaratadi
@RequiredArgsConstructor // Lombok tomonidan faqat kerakli (final) o'zgaruvchilar uchun konstruktor yaratadi
public class JwtRequestFilter extends OncePerRequestFilter { // Har bir so'rov uchun bitta marta ishlovchi filterni kengaytiradi

    @Autowired
    private JwtUtil jwtUtil; // JWT bilan ishlash uchun yordamchi klass

    @Autowired
    private CustomUserDetailsService customUserDetailsService; // Foydalanuvchi ma'lumotlarini olish uchun servis

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        // HTTP so'rovining "Authorization" sarlavhasini olish
        final String authorizationHeader = request.getHeader("Authorization");

        String username = null; // JWT dan foydalanuvchi nomini saqlash uchun
        String jwt = null; // JWT tokenni saqlash uchun

        // Agar Authorization header mavjud bo'lsa va "Bearer " bilan boshlansa
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7); // "Bearer " so'zini kesib tashlaymiz
            username = jwtUtil.extractUsername(jwt); // JWT tokenidan foydalanuvchi nomini chiqaramiz
        }

        // Agar foydalanuvchi nomi mavjud bo'lsa va hali autentifikatsiya qilinmagan bo'lsa
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // Foydalanuvchi ma'lumotlarini yuklab olamiz (databazadan yoki xotiradan)
            UserDetails userDetails = this.customUserDetailsService.loadUserByUsername(username);

            // Tokenni tekshiramiz va uning foydalanuvchiga mosligini tasdiqlaymiz
            if (jwtUtil.validateToken(jwt, userDetails)) {

                // Foydalanuvchi autentifikatsiyasini o'rnatamiz
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities()); // Parolni ko'rsatmaymiz
                usernamePasswordAuthenticationToken
                        .setDetails(new WebAuthenticationDetailsSource().buildDetails(request)); // So'rov ma'lumotlarini biriktiramiz
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken); // Kontekstga o'rnatamiz
            }
        }
        // Keyingi filterga so'rovni o'tkazib yuboramiz
        chain.doFilter(request, response);
    }
}
