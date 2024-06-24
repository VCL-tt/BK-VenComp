package com.example.ventaComputadora.webController;

import com.example.ventaComputadora.domain.DTO.FavoritoDTO;
import com.example.ventaComputadora.domain.entity.Favorito;
import com.example.ventaComputadora.services.FavoritoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/favoritos")
@RequiredArgsConstructor
public class FavoritoController {
    private final FavoritoService favoritoService;

    @PostMapping("/agregar")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Favorito> agregarAFavoritos(@RequestParam Long usuarioId, @RequestParam Long productoId) {
        Favorito nuevoFavorito = favoritoService.agregarAFavoritos(usuarioId, productoId);
        return ResponseEntity.ok(nuevoFavorito);
    }

    @GetMapping("/usuario/{usuarioId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<List<FavoritoDTO>> listarFavoritosPorUsuario(@PathVariable Long usuarioId) {
        List<FavoritoDTO> favoritos = favoritoService.listarFavoritosPorUsuario(usuarioId);
        return ResponseEntity.ok(favoritos);
    }

    @DeleteMapping("/usuario/{usuarioId}/producto/{productoId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> eliminarDeFavoritos(@PathVariable Long usuarioId, @PathVariable Long productoId) {
        favoritoService.eliminarDeFavoritosPorUsuarioYProducto(usuarioId, productoId);
        return ResponseEntity.ok().build();
    }
}
