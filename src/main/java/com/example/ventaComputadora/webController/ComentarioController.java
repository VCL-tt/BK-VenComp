package com.example.ventaComputadora.webController;

import com.example.ventaComputadora.domain.DTO.ComentarioDTO;
import com.example.ventaComputadora.domain.entity.Comentario;
import com.example.ventaComputadora.services.implement.ComentarioService;
import com.example.ventaComputadora.infra.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Controlador REST para manejar las operaciones relacionadas con los comentarios.
 */
@RestController
@RequestMapping("/comentarios")
@RequiredArgsConstructor
public class ComentarioController {
    private final ComentarioService comentarioService;
    private final JwtService jwtService;

    /**
     * Agrega un nuevo comentario a un producto.
     *
     * @param comentario Detalles del comentario a agregar.
     * @param request HttpServletRequest para obtener el token JWT.
     * @return El comentario agregado.
     */
    @PostMapping("/agregar")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ComentarioDTO> agregarComentario(@RequestBody Comentario comentario, HttpServletRequest request) {
        String token = request.getHeader("Authorization").substring(7);
        Long usuarioId = jwtService.getUserIdFromToken(token);
        Long productoId = comentario.getProducto().getId();

        Comentario nuevoComentario = comentarioService.agregarComentario(usuarioId, productoId, comentario.getContenido());

        ComentarioDTO responseDTO = new ComentarioDTO(
                nuevoComentario.getId(),
                nuevoComentario.getProducto().getNombre(),
                nuevoComentario.getContenido(),
                nuevoComentario.getUsuario().getId(),
                nuevoComentario.getUsuario().getUsername(),
                nuevoComentario.getFecha()
        );

        return ResponseEntity.ok(responseDTO);
    }

    /**
     * Lista todos los comentarios de un producto.
     *
     * @param productoId ID del producto.
     * @return Lista de comentarios del producto.
     */
    @GetMapping("/producto/{productoId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<List<ComentarioDTO>> listarComentariosPorProducto(@PathVariable Long productoId) {
        List<Comentario> comentarios = comentarioService.listarComentariosPorProducto(productoId);
        List<ComentarioDTO> response = comentarios.stream()
                .map(c -> new ComentarioDTO(
                        c.getId(),
                        c.getProducto().getNombre(),
                        c.getContenido(),
                        c.getUsuario().getId(),
                        c.getUsuario().getUsername(),
                        c.getFecha()
                ))
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    /**
     * Edita un comentario.
     *
     * @param id ID del comentario a editar.
     * @param nuevoContenidoMap Nuevo contenido del comentario.
     * @param request HttpServletRequest para obtener el token JWT.
     * @return El comentario editado.
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ComentarioDTO> editarComentario(@PathVariable Long id, @RequestBody Map<String, String> nuevoContenidoMap, HttpServletRequest request) {
        String token = request.getHeader("Authorization").substring(7);
        Long usuarioId = jwtService.getUserIdFromToken(token);

        String nuevoContenido = nuevoContenidoMap.get("contenido");

        Comentario comentarioActualizado = comentarioService.editarComentario(id, usuarioId, nuevoContenido);
        ComentarioDTO responseDTO = new ComentarioDTO(
                comentarioActualizado.getId(),
                comentarioActualizado.getProducto().getNombre(),
                comentarioActualizado.getContenido(),
                comentarioActualizado.getUsuario().getId(),
                comentarioActualizado.getUsuario().getUsername(),
                comentarioActualizado.getFecha()
        );
        return ResponseEntity.ok(responseDTO);
    }

    /**
     * Elimina un comentario.
     *
     * @param id ID del comentario a eliminar.
     * @param request HttpServletRequest para obtener el token JWT.
     * @return Respuesta vacía si la eliminación fue exitosa.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> eliminarComentario(@PathVariable Long id, HttpServletRequest request) {
        String token = request.getHeader("Authorization").substring(7);
        Long usuarioId = jwtService.getUserIdFromToken(token);

        comentarioService.eliminarComentario(id, usuarioId);
        return ResponseEntity.ok().build();
    }
}
