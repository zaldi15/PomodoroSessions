package com.aplikasi.controller;

import com.aplikasi.dao.UserDAO;
import com.aplikasi.model.User;

public class UserController {
    
    private final UserDAO userDAO;

    public UserController() {
        this.userDAO = new UserDAO();
    }
    
    
    public User login(String username, String password) {
        if (username == null || password == null || username.trim().isEmpty() || password.trim().isEmpty()) {
            return null;
        }
        
        User user = userDAO.loginUser(username, password);
        
        if (user != null) {
            userDAO.updateLastLogin(user.getUser_id()); 
            return user;
        }
        
        return null; 
    }
    
    
    public String register(String username, String password, String email) {
        if (username.trim().isEmpty() || password.trim().isEmpty() || email.trim().isEmpty()) {
            return "Semua field harus diisi.";
        }
        
        if (userDAO.registerUser(username, password, email)) {
            return "Registrasi berhasil! Silakan Login.";
        } else {
            return "Registrasi gagal. Username atau Email sudah terdaftar.";
        }
    }
}
