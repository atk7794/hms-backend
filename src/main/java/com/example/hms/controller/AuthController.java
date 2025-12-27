package com.example.hms.controller;

import org.springframework.beans.factory.annotation.Value;
import com.example.hms.dto.auth.AuthResponseDTO;
import com.example.hms.dto.auth.LoginRequestDTO;
import com.example.hms.dto.auth.RegisterRequestDTO;
import com.example.hms.dto.request.PatientRequestDTO;
import com.example.hms.dto.response.PatientResponseDTO;
import com.example.hms.model.User;
import com.example.hms.model.UserActivityLog;
import com.example.hms.security.JwtUtil;
import com.example.hms.service.UserActivityLogService;
import com.example.hms.service.AuthService;
import com.example.hms.service.EmailService;
import com.example.hms.service.EmailVerificationService;
import com.example.hms.service.PatientService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private PatientService patientService;

    @Autowired
    private JwtUtil jwtUtil;

    // 🔹 Eklenecek servisler
    @Autowired
    private EmailVerificationService emailVerificationService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private UserActivityLogService userActivityLogService;

    @Value("${app.frontend.url}")
    private String frontendUrl;

    @PostMapping("/register")
    public AuthResponseDTO register(@RequestBody RegisterRequestDTO dto) throws Exception {
        // 1) User oluştur
        User user = new User();
        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword());
        user.setRole(dto.getRole());
        user.setEmailVerified(false); // 🔹 email doğrulanmamış olarak kaydediyoruz

        User savedUser = authService.register(user);

        Long patientId = null;

        // 2) Eğer rol PATIENT ise patient ekle / güncelle ve patientId al
        if ("PATIENT".equalsIgnoreCase(savedUser.getRole())) {
            PatientRequestDTO patientDTO = new PatientRequestDTO();
            patientDTO.setUserId(savedUser.getId());
            patientDTO.setFirstName(dto.getFirstName());
            patientDTO.setLastName(dto.getLastName());
            patientDTO.setGender(dto.getGender());
            patientDTO.setBirthDate(dto.getBirthDate());

            if (patientService.existsByUserId(savedUser.getId())) {
                patientService.updatePatientByUserId(patientDTO);
                PatientResponseDTO pr = patientService.getPatientByUserId(savedUser.getId());
                patientId = pr != null ? pr.getId() : null;
            } else {
                PatientResponseDTO pr = patientService.createPatient(patientDTO);
                patientId = pr != null ? pr.getId() : null;
            }
        }

        // 🔹 Email verification token oluştur
        String verificationToken = emailVerificationService.createVerificationToken(savedUser);
        String verificationLink = frontendUrl + "/verify-email?token=" + verificationToken;

        // 🔹 HTML mail içeriği
        String htmlContent = "<!DOCTYPE html>"
                + "<html><body>"
                + "<h2>HMS E-posta Doğrulama</h2>"
                + "<p>Merhaba <strong>" + (savedUser.getPatient() != null
                ? savedUser.getPatient().getFirstName() + " " + savedUser.getPatient().getLastName()
                : savedUser.getEmail()) + "</strong>,</p>"
                + "<p>Hesabınızı aktifleştirmek için aşağıdaki butona tıklayın:</p>"
                + "<a href='" + verificationLink + "' "
                + "style='display:inline-block;padding:10px 20px;background-color:#007bff;color:white;text-decoration:none;"
                + "border-radius:5px;'>E-postayı Doğrula</a>"
                + "<p>Eğer bu isteği siz yapmadıysanız, bu mesajı yok sayabilirsiniz.</p>"
                + "<p>HMS Destek Ekibi</p>"
                + "</body></html>";

        emailService.sendEmail(savedUser.getEmail(), "HMS E-posta Doğrulama", htmlContent);

        // 🔹 JWT token üretimi (opsiyonel: login’e izin vermek istiyorsan bırakabilirsin)
        String token = jwtUtil.generateToken(savedUser.getEmail(), savedUser.getRole());

        // 🔹 Response DTO
        return new AuthResponseDTO(
                savedUser.getId(),
                savedUser.getEmail(),
                savedUser.getRole(),
                "Kayıt başarılı! Lütfen e-posta adresinizi doğrulayın.",
                patientId,
                token
        );
    }

    @PostMapping("/verify-email")
    public ResponseEntity<?> verifyEmail(@RequestBody Map<String, String> request) {
        String token = request.get("token");

        boolean valid = emailVerificationService.validateVerificationToken(token);
        if (!valid) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Geçersiz veya süresi dolmuş doğrulama bağlantısı."));
        }

        emailVerificationService.verifyUser(token);
        return ResponseEntity.ok(Map.of("message", "E-posta başarıyla doğrulandı! Artık giriş yapabilirsiniz."));
    }

    @PostMapping("/resend-verification")
    public ResponseEntity<String> resendVerification(@RequestParam String email) {
        // 1️⃣ Kullanıcıyı bul
        User user = authService.findByEmail(email);
        if (user == null) {
            return ResponseEntity.badRequest().body("Kullanıcı bulunamadı.");
        }

        // 2️⃣ Zaten doğrulandıysa
        if (user.isEmailVerified()) {
            return ResponseEntity.badRequest().body("E-posta zaten doğrulanmış.");
        }

        // 3️⃣ Yeni token oluştur
        String token = emailVerificationService.createVerificationToken(user);

        // 4️⃣ Doğrulama linki oluştur
        String link = frontendUrl + "/verify-email?token=" + token;

        // 5️⃣ HTML mail içeriği hazırla
        String html = "<!DOCTYPE html>"
                + "<html><body>"
                + "<h2>HMS E-posta Doğrulama</h2>"
                + "<p>Merhaba <strong>" + user.getEmail() + "</strong>,</p>"
                + "<p>Hesabınızı aktifleştirmek için aşağıdaki butona tıklayın:</p>"
                + "<a href='" + link + "' "
                + "style='display:inline-block;padding:10px 20px;background-color:#007bff;color:white;text-decoration:none;"
                + "border-radius:5px;'>E-postayı Doğrula</a>"
                + "<p>Eğer bu isteği siz yapmadıysanız, bu mesajı yok sayabilirsiniz.</p>"
                + "<p>HMS Destek Ekibi</p>"
                + "</body></html>";

        // 6️⃣ Mail gönder
        emailService.sendEmail(user.getEmail(), "HMS E-posta Doğrulama", html);

        // 7️⃣ Yanıt dön
        return ResponseEntity.ok("Doğrulama e-postası tekrar gönderildi. Lütfen e-postanızı kontrol edin.");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO loginDTO) {
        try {
            User user = authService.login(loginDTO.getEmail(), loginDTO.getPassword());

            // 🔹 emailVerified kontrolü
            if (!user.isEmailVerified()) {
                return ResponseEntity.status(401)
                        .body(Map.of("message", "Email doğrulanmamış. Lütfen e-postanızı kontrol edin."));
            }

            // 🔹 Oturum kontrolü: Logout olmamış oturumları kapat
            List<UserActivityLog> openSessions = userActivityLogService.getOpenSessions(user.getEmail());
            for (UserActivityLog session : openSessions) {
                session.setLogoutAt(LocalDateTime.now());
                long duration = java.time.Duration.between(session.getLoginAt(), LocalDateTime.now()).getSeconds();
                session.setDurationSeconds(duration);
                userActivityLogService.updateSession(session); // yeni bir metod veya mevcut update metodu
            }

            // 🔹 IP al
            String ipAddress = ((ServletRequestAttributes) RequestContextHolder
                    .currentRequestAttributes()).getRequest().getRemoteAddr();

            // 🔹 Log kaydet
            userActivityLogService.logLogin(user.getEmail(), ipAddress);

            Long patientId = null;
            if ("PATIENT".equalsIgnoreCase(user.getRole())) {
                try {
                    PatientResponseDTO pr = patientService.getPatientByUserId(user.getId());
                    patientId = pr != null ? pr.getId() : null;
                } catch (Exception ignored) {}
            }

            // 🔹 JWT token üret
            String token = jwtUtil.generateToken(user.getEmail(), user.getRole());

            AuthResponseDTO response = new AuthResponseDTO(
                    user.getId(),
                    user.getEmail(),
                    user.getRole(),
                    "Login successful",
                    patientId,
                    token
            );

            return ResponseEntity.ok(response);

        } catch (Exception ex) {
            return ResponseEntity.status(400)
                    .body(Map.of("message", ex.getMessage()));
        }
    }

    // 🔹 LOGOUT (Yeni eklendi)
    // ⚠️ NOTE:
    // JWT stateless olduğu için bu logout sadece LOG tutar.
    // Token backend tarafında invalidate edilmez.
    // Gerçek logout için:
    // - Token blacklist
    // - Refresh token
    // - Token versioning
    // gibi mekanizmalar gerekir.
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        try {
            String authHeader = request.getHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                String username = jwtUtil.getEmailFromToken(token);
                if (username != null) {
                    userActivityLogService.logLogout(username);
                }
            }
            return ResponseEntity.ok(Map.of("message", "Logged out successfully."));
        } catch (Exception e) {
            return ResponseEntity.status(400).body(Map.of("message", "Logout failed: " + e.getMessage()));
        }
    }
}
