package com.example.ventaComputadora.services.implement;

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

/**
 * Servicio para manejar los comentarios.
 */
@Service
@RequiredArgsConstructor
public class ComentarioService {
    private final ComentarioRepository comentarioRepository;
    private final ProductoRepository productoRepository;
    private final UsuarioRepository usuarioRepository;

    /**
     * Agrega un nuevo comentario a un producto por un usuario.
     *
     * @param usuarioId ID del usuario que agrega el comentario.
     * @param productoId ID del producto al que se agrega el comentario.
     * @param contenido Contenido del comentario.
     * @return El comentario agregado.
     */
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

    /**
     * Lista todos los comentarios de un producto.
     *
     * @param productoId ID del producto.
     * @return Lista de comentarios del producto.
     */
    @Transactional(readOnly = true)
    public List<Comentario> listarComentariosPorProducto(Long productoId) {
        Producto producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        return comentarioRepository.findAllByProducto(producto);
    }

    /**
     * Edita el contenido de un comentario.
     *
     * @param comentarioId ID del comentario a editar.
     * @param usuarioId ID del usuario que edita el comentario.
     * @param nuevoContenido Nuevo contenido del comentario.
     * @return El comentario editado.
     */
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

    /**
     * Elimina un comentario.
     *
     * @param comentarioId ID del comentario a eliminar.
     * @param usuarioId ID del usuario que elimina el comentario.
     */
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
