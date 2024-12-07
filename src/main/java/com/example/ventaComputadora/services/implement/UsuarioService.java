package com.example.ventaComputadora.services.implement;

import com.example.ventaComputadora.domain.DTO.UsuarioDTO;
import com.example.ventaComputadora.domain.entity.enums.Role;
import com.example.ventaComputadora.domain.entity.Usuario;
import com.example.ventaComputadora.infra.repository.UsuarioRepository;
import com.example.ventaComputadora.infra.security.JwtService;
import com.example.ventaComputadora.infra.security.LoginRequest;
import com.example.ventaComputadora.infra.security.TokenResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Servicio para manejar usuarios.
 */
@Service
@RequiredArgsConstructor
public class UsuarioService {
    private final UsuarioRepository usuarioRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    /**
     * Autentica un usuario y devuelve un token JWT.
     *
     * @param request Información de inicio de sesión.
     * @return Respuesta con el token JWT.
     */
    public TokenResponse login(LoginRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        Usuario user = usuarioRepository.findByUsername(request.getUsername()).orElseThrow();
        String token = jwtService.getToken(user, user);
        return TokenResponse.builder()
                .token(token)
                .build();
    }

    /**
     * Agrega un nuevo usuario y devuelve un token JWT.
     *
     * @param usuario Información del usuario.
     * @return Respuesta con el token JWT.
     */
    public TokenResponse addUsuario(Usuario usuario) {
        Usuario user = Usuario.builder()
                .username(usuario.getUsername())
                .password(passwordEncoder.encode(usuario.getPassword()))
                .nombre(usuario.getNombre())
                .apellido(usuario.getApellido())
                .telefono(usuario.getTelefono())
                .correo(usuario.getCorreo())
                .dni(usuario.getDni())
                .role(Role.USER)
                .build();

        usuarioRepository.save(user);

        String token = jwtService.getToken(user, user);
        return TokenResponse.builder()
                .token(token)
                .build();
    }

    /**
     * Obtiene un usuario por su ID.
     *
     * @param id ID del usuario.
     * @return El usuario encontrado.
     */
    public Optional<Usuario> getUsuarioById(Long id) {
        return usuarioRepository.findById(id);
    }

    /**
     * Obtiene el perfil de un usuario por su ID.
     *
     * @param id ID del usuario.
     * @return DTO del perfil del usuario.
     */
    public UsuarioDTO getPerfilUsuario(Long id) {
        Usuario usuario = usuarioRepository.findById(id).orElse(null);
        if (usuario == null) {
            return null;
        }
        return new UsuarioDTO(
                usuario.getId(),
                usuario.getNombre(),
                usuario.getApellido(),
                usuario.getCorreo(),
                usuario.getTelefono(),
                usuario.getDni(),
                usuario.getUsername(),
                usuario.getOrdenes(),
                usuario.getComentarios(),
                usuario.getFavoritos()
        );
    }

    /**
     * Actualiza la información de un usuario.
     *
     * @param usuarioDTO Nueva información del usuario.
     * @param id ID del usuario.
     * @return El usuario actualizado.
     */
    @Transactional
    public Usuario updateUsuario(UsuarioDTO usuarioDTO, Long id) {
        Usuario usuarioExistente = usuarioRepository.findById(id).orElseThrow(
                () -> new RuntimeException("Usuario no encontrado")
        );

        try {
            usuarioExistente.setNombre(usuarioDTO.getNombre());
            usuarioExistente.setApellido(usuarioDTO.getApellido());
            usuarioExistente.setCorreo(usuarioDTO.getCorreo());
            usuarioExistente.setTelefono(usuarioDTO.getTelefono());
            usuarioExistente.setDni(usuarioDTO.getDni());
            usuarioExistente.setUsername(usuarioDTO.getUsername());

            return usuarioRepository.save(usuarioExistente);
        } catch (Exception e) {
            System.err.println("Error al actualizar el usuario: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error al actualizar el usuario");
        }
    }

    /**
     * Elimina un usuario por su ID.
     *
     * @param id ID del usuario.
     */
    @Transactional
    public void deleteById(Long id) {
        Usuario usuario = usuarioRepository.findById(id).orElseThrow(
                () -> new RuntimeException("Usuario no encontrado")
        );
        usuario.getOrdenes().forEach(orden -> orden.setUsuario(null));
        usuario.getComentarios().forEach(comentario -> comentario.setUsuario(null));
        usuario.getFavoritos().forEach(favorito -> favorito.setUsuario(null));
        usuarioRepository.delete(usuario);
    }

    public Usuario getAuthenticatedUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof Usuario) {
            return (Usuario) principal;
        } else if (principal instanceof UserDetails) {
            // Si el principal es una implementación de UserDetails, obtén el nombre de usuario y busca el usuario en la base de datos
            String username = ((UserDetails) principal).getUsername();
            return usuarioRepository.findByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));
        } else if (principal instanceof String) {
            // Si el principal es un String (nombre de usuario), realiza la búsqueda en la base de datos
            String username = (String) principal;
            return usuarioRepository.findByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));
        } else {
            throw new ClassCastException("No se pudo convertir el principal a Usuario");
        }
    }


}
