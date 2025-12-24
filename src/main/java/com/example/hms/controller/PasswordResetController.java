package com.example.hms.controller;

import com.example.hms.model.User;
import com.example.hms.repository.UserRepository;
import com.example.hms.service.PasswordResetService;
import com.example.hms.service.EmailService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class PasswordResetController {

    private final UserRepository userRepository;
    private final PasswordResetService passwordResetService;
    private final EmailService emailService;

    public PasswordResetController(UserRepository userRepository,
                                   PasswordResetService passwordResetService,
                                   EmailService emailService) {
        this.userRepository = userRepository;
        this.passwordResetService = passwordResetService;
        this.emailService = emailService;
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> payload) {
        String email = payload.get("email");
        Optional<User> userOpt = userRepository.findByEmail(email);

        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Kullanıcı bulunamadı"));
        }

        User user = userOpt.get();
        String token = passwordResetService.createPasswordResetToken(user);
        String resetLink = "http://localhost:4200/reset-password?token=" + token;

        // 🔹 İsim bulma (role’e göre)
        String name = "Kullanıcı";
        if (user.getPatient() != null) {
            name = user.getPatient().getFirstName() + " " + user.getPatient().getLastName();
        } else if (user.getDoctor() != null) {
            name = "Dr. " + user.getDoctor().getFirstName() + " " + user.getDoctor().getLastName();
        }

        // 🔹 HTML mail içeriği
        String htmlContent = "<!DOCTYPE html>"
                + "<html><body>"
                + "<h2>HMS Şifre Sıfırlama</h2>"
                + "<p>Merhaba <strong>" + name + "</strong>,</p>"
                + "<p>Şifreni sıfırlamak için aşağıdaki butona tıklayabilirsin:</p>"
                + "<a href='" + resetLink + "' "
                + "style='display:inline-block;padding:10px 20px;background-color:#007bff;color:white;text-decoration:none;"
                + "border-radius:5px;'>Şifreyi Sıfırla</a>"
                + "<p>Eğer bu isteği sen yapmadıysan, bu mesajı yok sayabilirsin.</p>"
                + "<p>HMS Destek Ekibi</p>"
                + "</body></html>";

        emailService.sendEmail(email, "HMS Şifre Sıfırlama Bağlantısı", htmlContent);

        return ResponseEntity.ok(Map.of("message", "Şifre sıfırlama bağlantısı e-posta ile gönderildi."));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> payload) {
        String token = payload.get("token");
        String newPassword = payload.get("newPassword");

        if (!passwordResetService.validatePasswordResetToken(token)) {
            return ResponseEntity.badRequest().body(Map.of("message", "Token geçersiz veya süresi dolmuş"));
        }

        passwordResetService.resetPassword(token, newPassword);
        return ResponseEntity.ok(Map.of("message", "Şifre başarıyla güncellendi"));
    }
}
