package com.security.springsecurity.service;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.security.springsecurity.model.User;
import com.security.springsecurity.repository.UserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    // UserRepository orqali ma'lumotlarni olish uchun autowired qilish
    @Autowired
    private UserRepository userRepository;

    // Username orqali foydalanuvchi ma'lumotlarini olish
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // UserRepository orqali foydalanuvchini username bo'yicha qidirish
        User user = userRepository.findByUsername(username);
        
        // Agar foydalanuvchi topilmasa, UsernameNotFoundException ni tashlaymiz
        if(user == null) {
            throw new UsernameNotFoundException("User not found");
        }

        // Foydalanuvchi topilgan bo'lsa, UserDetails qaytariladi.
        // user.getUsername() va user.getPassword() orqali foydalanuvchining username va parolini o'zgartirish
        // new ArrayList<>() – bu foydalanuvchining ruxsatnomalari (authorities) bo'lishi mumkin, hozircha bo'sh ro'yxat.
        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), new ArrayList<>());
    }
    
    
}
