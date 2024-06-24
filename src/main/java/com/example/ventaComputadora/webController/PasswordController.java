package com.example.ventaComputadora.webController;

import com.example.ventaComputadora.domain.entity.email.EmailRequest;
import com.example.ventaComputadora.domain.entity.email.PasswordUpdateRequest;
import com.example.ventaComputadora.domain.entity.email.TokenValidationRequest;
import com.example.ventaComputadora.services.PasswordResetService;
import jakarta.mail.MessagingException;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/password")
@CrossOrigin("*")
public class PasswordController {

    @Autowired
    private PasswordResetService passwordResetService;

    @PostMapping("/reset")
    public ResponseEntity<String> resetPassword(@RequestBody EmailRequest request) {
        try {
            passwordResetService.sendPasswordResetEmail(request.getEmail());
            return ResponseEntity.ok("Password reset email sent successfully");
        } catch (MessagingException e) {
            return ResponseEntity.status(500).body("Failed to send password reset email: " + e.getMessage());
        }
    }

    @PostMapping("/validate")
    public ResponseEntity<Boolean> validateResetToken(@RequestBody TokenValidationRequest request) {
        boolean isValid = passwordResetService.validateResetToken(request.getEmail(), request.getToken());
        return ResponseEntity.ok(isValid);
    }

    @PostMapping("/update")
    public ResponseEntity<String> updatePassword(@RequestBody PasswordUpdateRequest request) {
        boolean isValid = passwordResetService.validateResetToken(request.getEmail(), request.getToken());
        if (isValid) {
            passwordResetService.updatePassword(request.getEmail(), request.getNewPassword());
            return ResponseEntity.ok("Password updated successfully");
        } else {
            return ResponseEntity.status(400).body("Invalid reset token");
        }
    }
}
