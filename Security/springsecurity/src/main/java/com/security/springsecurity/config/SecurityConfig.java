package com.security.springsecurity.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.security.springsecurity.service.CustomUserDetailsService;
import com.security.springsecurity.util.JwtUtil;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true) // Metod darajasidagi xafvsizlikni yoqadi, masalan, @PreAuthorize annotatsiyasini ishlatish uchun 
public class SecurityConfig {

    @Autowired
    private CustomUserDetailsService customUserDetailsService; // Foydalanuvchiga oid ma'lumotlarni yuklash uchun maxsus servis

    @Autowired
    private JwtUtil jwtUtil;  // JWT bilan ishlash uchun util class

    @Bean
    public PasswordEncoder passwordEncoder() {
        // Parollarni xavfsiz shifrlash uchun BCrypt ishlatiladi
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        // Foydalanuvchini autentifikatsiya qilish uchun AuthenticationManager ta'minlanadi
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf().disable() // CSRF himoyasini o'chiradi, chunki biz JWT asosidagi stateless autentifikatsiyasini foydalanamiz
            .authorizeRequests()
                    .requestMatchers("/api/register", "/api/login").permitAll() // "register" va "login" enpointlariga ummumiy kirish ruxsatini beradi
                    .anyRequest().authenticated() // Boshqa barcha endpointlar autentifikatsiyasini talab qiladi 
                .and()
                .sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS); // Sessiyalarni yaratmaslikni ta'minlaydi (stateless API)

            // JWT tokenlarni tekshirirsh uchun custom filerni UsernamePassowrdAuthenticationFilter - dan oldin qo'shiladi
            http.addFilterBefore(jwtRequestFilter(), UsernamePasswordAuthenticationFilter.class);
            return http.build();
    }

    @Bean
    public JwtRequestFilter jwtRequestFilter() {
        // JWt tokenlarni qayta ishlash va tekshirish uchun maxsus filter
        return new JwtRequestFilter(jwtUtil, customUserDetailsService);
    }

    
}
