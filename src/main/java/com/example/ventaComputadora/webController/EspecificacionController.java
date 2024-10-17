package com.example.ventaComputadora.webController;

import com.example.ventaComputadora.domain.DTO.EspecificacionDTO;
import com.example.ventaComputadora.domain.entity.Especificacion;
import com.example.ventaComputadora.services.EspecificacionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para manejar las operaciones relacionadas con las especificaciones.
 */
@RestController
@RequestMapping("/especificaciones")
@RequiredArgsConstructor
public class EspecificacionController {
    private final EspecificacionService especificacionService;

    /**
     * Registra una nueva especificación.
     *
     * @param especificacion Detalles de la especificación a registrar.
     * @return La especificación registrada.
     */
    @PostMapping("/registrar")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Especificacion> registrarEspecificacion(@RequestBody Especificacion especificacion) {
        Especificacion nuevaEspecificacion = especificacionService.registrarEspecificacion(
                especificacion.getNombre(),
                especificacion.getDescripcion(),
                especificacion.getPrecioAdicional(),
                especificacion.getMarca(),
                especificacion.getTipo()
        );
        return ResponseEntity.ok(nuevaEspecificacion);
    }

    /**
     * Elimina una especificación por su ID.
     *
     * @param id ID de la especificación a eliminar.
     * @return Respuesta vacía si la eliminación fue exitosa.
     */
    @DeleteMapping("/eliminar/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> eliminarEspecificacion(@PathVariable Long id) {
        try {
            especificacionService.eliminarEspecificacion(id);
            return ResponseEntity.ok().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Actualiza una especificación.
     *
     * @param id ID de la especificación a actualizar.
     * @param especificacion Nueva información de la especificación.
     * @return La especificación actualizada.
     */
    @PutMapping("/actualizar/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Especificacion> actualizarEspecificacion(@PathVariable Long id, @RequestBody Especificacion especificacion) {
        Especificacion especificacionActualizada = especificacionService.actualizarEspecificacion(id, especificacion);
        return ResponseEntity.ok(especificacionActualizada);
    }

    /**
     * Busca especificaciones por nombre.
     *
     * @param nombre Nombre de la especificación.
     * @return Lista de especificaciones que coinciden con el nombre.
     */
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    @GetMapping("/buscar")
    public ResponseEntity<List<EspecificacionDTO>> buscarEspecificacionesPorNombre(@RequestParam String nombre) {
        List<EspecificacionDTO> especificaciones = especificacionService.buscarEspecificacionesPorNombre(nombre);
        return ResponseEntity.ok(especificaciones);
    }

    /**
     * Busca especificaciones por marca y tipo.
     *
     * @param marca Marca de la especificación.
     * @param tipo Tipo de la especificación.
     * @return Lista de especificaciones que coinciden con la marca y tipo.
     */
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    @GetMapping("/filtrar")
    public ResponseEntity<List<EspecificacionDTO>> buscarEspecificacionesPorMarcaYTipo(
            @RequestParam(required = false) String marca,
            @RequestParam(required = false) String tipo) {
        List<EspecificacionDTO> especificaciones = especificacionService.buscarEspecificacionesPorMarcaYTipo(
                marca != null ? marca : "",
                tipo != null ? tipo : ""
        );
        return ResponseEntity.ok(especificaciones);
    }

    /**
     * Lista todas las especificaciones.
     *
     * @return Lista de todas las especificaciones.
     */
    @GetMapping("/listar")
    public ResponseEntity<List<EspecificacionDTO>> listarEspecificaciones() {
        List<EspecificacionDTO> especificaciones = especificacionService.listarEspecificaciones();
        return ResponseEntity.ok(especificaciones);
    }
}
