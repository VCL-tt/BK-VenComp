package com.example.ventaComputadora.services;

import com.example.ventaComputadora.domain.entity.Comentario;
import com.example.ventaComputadora.domain.entity.Producto;
import com.example.ventaComputadora.domain.entity.Usuario;
import com.example.ventaComputadora.infra.repository.ComentarioRepository;
import com.example.ventaComputadora.infra.repository.ProductoRepository;
import com.example.ventaComputadora.infra.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ComentarioService {
    private final ComentarioRepository comentarioRepository;
    private final ProductoRepository productoRepository;
    private final UsuarioRepository usuarioRepository;

    @Transactional
    public Comentario agregarComentario(Long usuarioId, Long productoId, String contenido) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        Producto producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        Comentario comentario = Comentario.builder()
                .usuario(usuario)
                .producto(producto)
                .contenido(contenido)
                .fecha(LocalDateTime.now())
                .build();

        return comentarioRepository.save(comentario);
    }

    @Transactional(readOnly = true)
    public List<Comentario> listarComentariosPorProducto(Long productoId) {
        Producto producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        return comentarioRepository.findAllByProducto(producto);
    }

    @Transactional
    public Comentario editarComentario(Long comentarioId, Long usuarioId, String nuevoContenido) {
        Comentario comentario = comentarioRepository.findById(comentarioId)
                .orElseThrow(() -> new RuntimeException("Comentario no encontrado"));

        if (!comentario.getUsuario().getId().equals(usuarioId)) {
            throw new RuntimeException("No tienes permiso para editar este comentario");
        }

        comentario.setContenido(nuevoContenido);
        comentario.setFecha(LocalDateTime.now());
        return comentarioRepository.save(comentario);
    }

    @Transactional
    public void eliminarComentario(Long comentarioId, Long usuarioId) {
        Comentario comentario = comentarioRepository.findById(comentarioId)
                .orElseThrow(() -> new RuntimeException("Comentario no encontrado"));

        if (!comentario.getUsuario().getId().equals(usuarioId)) {
            throw new RuntimeException("No tienes permiso para eliminar este comentario");
        }

        comentarioRepository.deleteById(comentarioId);
    }
}
