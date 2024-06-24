package com.example.ventaComputadora.services;

import com.example.ventaComputadora.domain.DTO.UsuarioDTO;
import com.example.ventaComputadora.domain.entity.Role;
import com.example.ventaComputadora.domain.entity.Usuario;
import com.example.ventaComputadora.infra.repository.UsuarioRepository;
import com.example.ventaComputadora.infra.security.JwtService;
import com.example.ventaComputadora.infra.security.LoginRequest;
import com.example.ventaComputadora.infra.security.TokenResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UsuarioService {
    private final UsuarioRepository usuarioRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public TokenResponse login(LoginRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        Usuario user = usuarioRepository.findByUsername(request.getUsername()).orElseThrow();
        String token = jwtService.getToken(user, user);
        return TokenResponse.builder()
                .token(token)
                .build();
    }

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

    public Optional<Usuario> getUsuarioById(Long id) {
        return usuarioRepository.findById(id);
    }

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
}
