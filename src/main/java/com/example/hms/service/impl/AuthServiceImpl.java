package com.example.hms.service.impl;

import com.example.hms.model.Patient;
import com.example.hms.model.User;
import com.example.hms.repository.PatientRepository;
import com.example.hms.repository.UserRepository;
import com.example.hms.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public User register(User user) throws Exception {
        Optional<User> existing = userRepository.findByEmail(user.getEmail());
        if (existing.isPresent()) {
            throw new Exception("Bu email zaten kayıtlı.");
        }

        // ✅ Şifreyi hashle
        // String hashedPassword = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt());
        // user.setPassword(hashedPassword);

        // ✅ TEK VE STANDART ŞİFRELEME
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // User kaydet
        User savedUser = userRepository.save(user);

        // Patient oluştur ve user ile bağla
        Patient patient = new Patient();
        patient.setUser(savedUser);
        savedUser.setPatient(patient);

        // Patient kaydet (cascade varsa userRepository.save yeterli, yoksa ekle)
        patientRepository.save(patient);

        return savedUser;
    }

    @Override
    public User login(String email, String password) throws Exception {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            throw new Exception("Email bulunamadı.");
        }
        User user = userOpt.get();

        // ✅ Hash karşılaştırması
        // if (!BCrypt.checkpw(password, user.getPassword())) {
        //    throw new Exception("Şifre yanlış.");
        // }

        // ✅ STANDART KARŞILAŞTIRMA
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new Exception("Şifre yanlış.");
        }

        return user;
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

}
