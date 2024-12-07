package com.example.ventaComputadora.webController;

import com.example.ventaComputadora.services.implement.EmailService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST para manejar el envío de correos electrónicos.
 */
@RestController
@RequestMapping("/email")
@CrossOrigin("*") // Permite solicitudes de cualquier origen
@RequiredArgsConstructor
public class EmailController {

    private final EmailService emailService;

    /**
     * Envía un correo electrónico.
     *
     * @param to Dirección de correo del destinatario.
     * @param subject Asunto del correo.
     * @param text Cuerpo del correo.
     * @return Respuesta indicando el estado del envío.
     */
    @PostMapping("/send")
    public ResponseEntity<String> sendEmail(@RequestParam String to, @RequestParam String subject, @RequestParam String text) {
        try {
            emailService.sendEmail(to, subject, text);
            return ResponseEntity.ok("Email enviado exitosamente");
        } catch (MessagingException e) {
            return ResponseEntity.status(500).body("Error al enviar el email: " + e.getMessage());
        }
    }
}
