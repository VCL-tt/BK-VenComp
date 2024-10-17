package com.example.ventaComputadora.webController;

import com.example.ventaComputadora.domain.DTO.UsuarioDTO;
import com.example.ventaComputadora.domain.entity.Usuario;
import com.example.ventaComputadora.infra.security.LoginRequest;
import com.example.ventaComputadora.infra.security.TokenResponse;
import com.example.ventaComputadora.services.UsuarioService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST para manejar las operaciones relacionadas con los usuarios.
 */
@RestController
@RequestMapping("/usuario")
@CrossOrigin("*") // Permite solicitudes de cualquier origen
@RequiredArgsConstructor
public class UsuarioController {
    private final UsuarioService usuarioService;

    /**
     * Autentica a un usuario y devuelve un token JWT.
     *hfghfhfhfhf
     * @param request Solicitud de inicio de sesión.
     * @return Respuesta con el token JWT.
     */
    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@RequestBody LoginRequest request) {
        TokenResponse token = usuarioService.login(request);
        return ResponseEntity.ok(token);
    }

    /**
     * Registra un nuevo usuario y devuelve un token JWT.
     *
     * @param usuario Información del usuario.
     * @return Respuesta con el token JWT.
     */
    @PostMapping(value = "/registrar", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    public ResponseEntity<TokenResponse> registrar(@RequestBody Usuario usuario) {
        TokenResponse token = usuarioService.addUsuario(usuario);
        return ResponseEntity.ok(token);
    }

    /**
     * Obtiene un usuario por su ID.
     *
     * @param id ID del usuario.
     * @return El usuario encontrado.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Usuario> getUsuarioById(@PathVariable Long id) {
        Usuario usuario = usuarioService.getUsuarioById(id).orElse(null);
        if (usuario == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(usuario);
    }

    /**
     * Obtiene el perfil de un usuario por su ID.
     *
     * @param id ID del usuario.
     * @return DTO del perfil del usuario.
     */
    @GetMapping("/perfil/{id}")
    public ResponseEntity<UsuarioDTO> getPerfilUsuario(@PathVariable Long id) {
        UsuarioDTO usuarioDTO = usuarioService.getPerfilUsuario(id);
        if (usuarioDTO == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(usuarioDTO);
    }

    /**
     * Actualiza la información de un usuario.
     *
     * @param usuarioDTO Nueva información del usuario.
     * @param id ID del usuario.
     * @return El usuario actualizado.
     */
    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<Usuario> updateUsuario(@RequestBody UsuarioDTO usuarioDTO, @PathVariable Long id) {
        try {
            Usuario updatedUsuario = usuarioService.updateUsuario(usuarioDTO, id);
            return ResponseEntity.ok(updatedUsuario);
        } catch (Exception e) {
            System.err.println("Error en la solicitud de actualización de usuario: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * Elimina un usuario por su ID.
     *
     * @param id ID del usuario.
     * @return Respuesta vacía si la eliminación fue exitosa.
     */
    @DeleteMapping("/eliminar/{id}")
    @Transactional
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        usuarioService.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
