package com.security.springsecurity.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.security.springsecurity.model.AuthenticationRequest;
import com.security.springsecurity.model.User;
import com.security.springsecurity.repository.UserRepository;
import com.security.springsecurity.service.CustomUserDetailsService;
import com.security.springsecurity.util.JwtUtil;

@RestController
@RequestMapping("/api") // Api endpoinlari "api" orqali kiritiladi 
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager; // Foydalanuvchi autentifikatsiya qilish uchun foydalaniladi

    @Autowired
    private CustomUserDetailsService userDetailsService; // Foydalanuvchini ma'lumotlarini olish uchun servis 

    @Autowired
    private UserRepository userRepository; // Foydalanuvchilar ma'lumotlar bazasi bilan ishlash uchun reposiory

    @Autowired
    private PasswordEncoder passwordEncoder; // Parolni shirflash uchun encoder

    @Autowired
    private JwtUtil jwtUtil; // JWt tokenlarini yaratish va tekshirish uchun util klass

    // Foydalanuvchini ro'yxatdan o'tkazish uchun endpoint 
    @PostMapping("/register")
    public String registerUser(@RequestBody User user) {
        // Foydalanuvchining parolini shirflash
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        // Foydalnuvchi ma'lumotlar bazasiga saqlash
        userRepository.save(user);
        return "Foydalanuvchi muvafaqiyatlli ro'yxatdan o'tkazildi ";
    }


    // Foydalanuvchi tizimga kirishini boshqarish uchun endpoint 
    @PostMapping("/login")
    public String loginUser(@RequestBody AuthenticationRequest authenticationRequest) throws Exception {
        // Foydalanuvchini authentifikatsiya qilish
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authenticationRequest.getUsername(), // Login
                        authenticationRequest.getPassword()  // Parol
                )
        );

        // Foydalanuvchi ma'lumotlarini olish
        final UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getUsername());

        // Jwt Token yaratish
        final String jwt = jwtUtil.generateToken(userDetails);

        return jwt; // foydalanuvchiga JWT tokkeni qaytarish
    }

    // Oddiy "Hello, World" qaytaruvchi sinov uchun endpoint
    @GetMapping("/hello") 
    public String hello() {
        return "Salom Dunyo";
    }
    
}
