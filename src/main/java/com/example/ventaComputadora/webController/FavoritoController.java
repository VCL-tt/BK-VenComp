package com.example.ventaComputadora.webController;

import com.example.ventaComputadora.domain.DTO.FavoritoDTO;
import com.example.ventaComputadora.domain.entity.Favorito;
import com.example.ventaComputadora.services.implement.FavoritoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para manejar las operaciones relacionadas con los favoritos.
 */
@RestController
@RequestMapping("/favoritos")
@RequiredArgsConstructor
public class FavoritoController {
    private final FavoritoService favoritoService;

    /**
     * Agrega un producto a los favoritos de un usuario.
     *
     * @param usuarioId ID del usuario.
     * @param productoId ID del producto a agregar a favoritos.
     * @return El favorito agregado.
     */
    @PostMapping("/agregar")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Favorito> agregarAFavoritos(@RequestParam Long usuarioId, @RequestParam Long productoId) {
        Favorito nuevoFavorito = favoritoService.agregarAFavoritos(usuarioId, productoId);
        return ResponseEntity.ok(nuevoFavorito);
    }

    /**
     * Lista los favoritos de un usuario.
     *
     * @param usuarioId ID del usuario.
     * @return Lista de favoritos del usuario.
     */
    @GetMapping("/usuario/{usuarioId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<List<FavoritoDTO>> listarFavoritosPorUsuario(@PathVariable Long usuarioId) {
        List<FavoritoDTO> favoritos = favoritoService.listarFavoritosPorUsuario(usuarioId);
        return ResponseEntity.ok(favoritos);
    }

    /**
     * Elimina un producto de los favoritos de un usuario.
     *
     * @param usuarioId ID del usuario.
     * @param productoId ID del producto a eliminar de favoritos.
     * @return Respuesta vacía si la eliminación fue exitosa.
     */
    @DeleteMapping("/usuario/{usuarioId}/producto/{productoId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> eliminarDeFavoritos(@PathVariable Long usuarioId, @PathVariable Long productoId) {
        favoritoService.eliminarDeFavoritosPorUsuarioYProducto(usuarioId, productoId);
        return ResponseEntity.ok().build();
    }
}
