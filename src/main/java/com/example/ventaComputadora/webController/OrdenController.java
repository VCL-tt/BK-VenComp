package com.example.ventaComputadora.webController;

import com.example.ventaComputadora.domain.DTO.OrdenDTO;
import com.example.ventaComputadora.domain.entity.Orden;
import com.example.ventaComputadora.services.OrdenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("/ordenes")
@RequiredArgsConstructor
public class OrdenController {
    private final OrdenService ordenService;

    @GetMapping("/usuario/{usuarioId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<List<OrdenDTO>> listarOrdenesPorUsuario(@PathVariable Long usuarioId) {
        List<OrdenDTO> ordenes = ordenService.listarOrdenesPorUsuario(usuarioId);
        return ResponseEntity.ok(ordenes);
    }

    @GetMapping("/usuario/{usuarioId}/activa")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Orden> obtenerOrdenActiva(@PathVariable Long usuarioId) {
        Optional<Orden> ordenActiva = ordenService.obtenerOrdenActiva(usuarioId);
        if (ordenActiva.isPresent()) {
            return ResponseEntity.ok(ordenActiva.get());
        } else {
            return ResponseEntity.status(404).body(null); // Devolver 404 si no hay orden activa
        }
    }

    @PostMapping("/crear")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Orden> crearOrden(@RequestParam Long usuarioId, @RequestBody Set<Long> productoIds) {
        Orden nuevaOrden = ordenService.crearOrden(usuarioId, productoIds);
        return ResponseEntity.ok(nuevaOrden);
    }

    @PostMapping("/agregarProducto")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Orden> agregarProductoAlCarrito(@RequestBody Map<String, Long> datos) {
        Long usuarioId = datos.get("usuarioId");
        Long productoId = datos.get("productoId");
        Orden ordenActualizada = ordenService.agregarProductoALaOrden(usuarioId, productoId);
        return ResponseEntity.ok(ordenActualizada);
    }

    @PutMapping("/{id}/actualizar")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Orden> actualizarOrden(@PathVariable Long id, @RequestBody Orden orden) {
        Orden ordenActualizada = ordenService.actualizarOrden(id, orden);
        return ResponseEntity.ok(ordenActualizada);
    }

    @DeleteMapping("/{id}/producto/{productoId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> eliminarProductoDeLaOrden(@PathVariable Long id, @PathVariable Long productoId) {
        try {
            Orden ordenActualizada = ordenService.eliminarProductoDeLaOrden(id, productoId);
            OrdenDTO ordenDTO = ordenService.convertirADTO(ordenActualizada);
            return ResponseEntity.ok(ordenDTO);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> eliminarOrden(@PathVariable Long id) {
        ordenService.eliminarOrden(id);
        return ResponseEntity.ok().build();
    }
}
