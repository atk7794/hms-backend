package com.example.hms.service;

import com.example.hms.model.User;

public interface AuthService {
    User register(User user) throws Exception;   // Yeni kullanıcı kaydı
    User login(String email, String password) throws Exception; // Giriş

    User findByEmail(String email);

}




