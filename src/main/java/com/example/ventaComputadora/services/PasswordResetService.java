package com.example.ventaComputadora.services;

import com.example.ventaComputadora.domain.entity.PasswordResetToken;
import com.example.ventaComputadora.domain.entity.Usuario;
import com.example.ventaComputadora.infra.repository.PasswordResetTokenRepository;
import com.example.ventaComputadora.infra.repository.UsuarioRepository;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

import org.springframework.transaction.annotation.Transactional;

@Service
public class PasswordResetService {

    @Autowired
    private EmailService emailService;

    @Autowired
    private PasswordResetTokenRepository tokenRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public String generateResetCode() {
        Random random = new Random();
        int resetCode = 100000 + random.nextInt(900000);
        return String.valueOf(resetCode);
    }

    @Transactional
    public void sendPasswordResetEmail(String email) throws MessagingException {
        String resetCode = generateResetCode();

        // Elimina tokens antiguos
        tokenRepository.deleteByEmail(email);

        PasswordResetToken token = new PasswordResetToken();
        token.setEmail(email);
        token.setToken(resetCode);
        token.setExpiryDate(LocalDateTime.now().plusHours(1)); // Token válido por 1 hora
        tokenRepository.save(token);

        String subject = "Password Reset Code";
        String text = "Your password reset code is: " + resetCode;
        emailService.sendEmail(email, subject, text);
    }

    public boolean validateResetToken(String email, String token) {
        Optional<PasswordResetToken> resetToken = tokenRepository.findByEmail(email);
        return resetToken.isPresent() && resetToken.get().getToken().equals(token) &&
                resetToken.get().getExpiryDate().isAfter(LocalDateTime.now());
    }

    public void updatePassword(String email, String newPassword) {
        Optional<Usuario> user = usuarioRepository.findByCorreo(email);
        if (user.isPresent()) {
            Usuario usuario = user.get();
            usuario.setPassword(passwordEncoder.encode(newPassword));
            usuarioRepository.save(usuario);
        }
    }
}

