package com.example.ventaComputadora.services.implement;

import com.example.ventaComputadora.domain.entity.PasswordResetToken;
import com.example.ventaComputadora.domain.entity.Usuario;
import com.example.ventaComputadora.infra.repository.PasswordResetTokenRepository;
import com.example.ventaComputadora.infra.repository.UsuarioRepository;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

/**
 * Servicio para manejar el restablecimiento de contraseñas.
 */
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

    /**
     * Genera un código de restablecimiento de contraseña.
     *
     * @return El código de restablecimiento generado.
     */
    public String generateResetCode() {
        Random random = new Random();
        int resetCode = 100000 + random.nextInt(900000);
        return String.valueOf(resetCode);
    }

    /**
     * Envía un correo electrónico con el código de restablecimiento de contraseña.
     *
     * @param email Dirección de correo del usuario.
     * @throws MessagingException Si ocurre un error al enviar el correo.
     */
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

        String subject = "Código de Restablecimiento de Contraseña";
        String text = "Tu código de restablecimiento de contraseña es: " + resetCode;
        emailService.sendEmail(email, subject, text);
    }

    /**
     * Valida el token de restablecimiento de contraseña.
     *
     * @param email Dirección de correo del usuario.
     * @param token Token de restablecimiento.
     * @return Verdadero si el token es válido, falso en caso contrario.
     */
    public boolean validateResetToken(String email, String token) {
        Optional<PasswordResetToken> resetToken = tokenRepository.findByEmail(email);
        return resetToken.isPresent() && resetToken.get().getToken().equals(token) &&
                resetToken.get().getExpiryDate().isAfter(LocalDateTime.now());
    }

    /**
     * Actualiza la contraseña del usuario.
     *
     * @param email Dirección de correo del usuario.
     * @param newPassword Nueva contraseña.
     */
    public void updatePassword(String email, String newPassword) {
        Optional<Usuario> user = usuarioRepository.findByCorreo(email);
        if (user.isPresent()) {
            Usuario usuario = user.get();
            usuario.setPassword(passwordEncoder.encode(newPassword));
            usuarioRepository.save(usuario);
        }
    }
}
