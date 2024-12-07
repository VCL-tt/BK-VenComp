package com.example.ventaComputadora.webController;

import com.example.ventaComputadora.domain.entity.email.EmailRequest;
import com.example.ventaComputadora.domain.entity.email.PasswordUpdateRequest;
import com.example.ventaComputadora.domain.entity.email.TokenValidationRequest;
import com.example.ventaComputadora.services.implement.PasswordResetService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST para manejar las operaciones relacionadas con el restablecimiento de contraseñas.
 */
@RestController
@RequestMapping("/password")
@CrossOrigin("*") // Permite solicitudes de cualquier origen
@RequiredArgsConstructor
public class PasswordController {

    private final PasswordResetService passwordResetService;

    /**
     * Envía un correo electrónico para restablecer la contraseña.
     *
     * @param request Solicitud que contiene el correo electrónico del usuario.
     * @return Respuesta indicando el estado del envío del correo.
     */
    @PostMapping("/reset")
    public ResponseEntity<String> resetPassword(@RequestBody EmailRequest request) {
        try {
            passwordResetService.sendPasswordResetEmail(request.getEmail());
            return ResponseEntity.ok("Correo de restablecimiento de contraseña enviado exitosamente");
        } catch (MessagingException e) {
            return ResponseEntity.status(500).body("Error al enviar el correo de restablecimiento de contraseña: " + e.getMessage());
        }
    }

    /**
     * Valida un token de restablecimiento de contraseña.
     *
     * @param request Solicitud que contiene el correo electrónico y el token.
     * @return Respuesta indicando si el token es válido.
     */
    @PostMapping("/validate")
    public ResponseEntity<Boolean> validateResetToken(@RequestBody TokenValidationRequest request) {
        boolean isValid = passwordResetService.validateResetToken(request.getEmail(), request.getToken());
        return ResponseEntity.ok(isValid);
    }

    /**
     * Actualiza la contraseña de un usuario.
     *
     * @param request Solicitud que contiene el correo electrónico, el token y la nueva contraseña.
     * @return Respuesta indicando el estado de la actualización de la contraseña.
     */
    @PostMapping("/update")
    public ResponseEntity<String> updatePassword(@RequestBody PasswordUpdateRequest request) {
        boolean isValid = passwordResetService.validateResetToken(request.getEmail(), request.getToken());
        if (isValid) {
            passwordResetService.updatePassword(request.getEmail(), request.getNewPassword());
            return ResponseEntity.ok("Contraseña actualizada exitosamente");
        } else {
            return ResponseEntity.status(400).body("Token de restablecimiento de contraseña inválido");
        }
    }
}
