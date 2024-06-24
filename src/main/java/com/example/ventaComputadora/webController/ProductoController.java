package com.example.ventaComputadora.webController;

import com.example.ventaComputadora.domain.DTO.ProductoDTO;
import com.example.ventaComputadora.domain.DTO.ProductoSimplificadoDTO;
import com.example.ventaComputadora.domain.DTO.PersonalizacionDTO;
import com.example.ventaComputadora.domain.entity.Producto;
import com.example.ventaComputadora.services.ProductoService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/productos")
@RequiredArgsConstructor
public class ProductoController {
    private final ProductoService productoService;

    @GetMapping("/listar")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ProductoSimplificadoDTO>> listarProductos() {
        List<ProductoSimplificadoDTO> productos = productoService.listarProductosSimplificados();
        return ResponseEntity.ok(productos);
    }

    @PostMapping("/registrar")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Producto> registrarProducto(@RequestBody ProductoDTO productoDTO) {
        Producto nuevoProducto = new Producto(
                null,
                productoDTO.getNombre(),
                productoDTO.getPrecio(),
                productoDTO.getDescripcion(),
                productoDTO.getImagen(),
                productoDTO.getStock(),
                new HashSet<>(), // especificacionesDisponibles
                new HashSet<>(), // comentarios
                new HashSet<>()  // favoritos
        );
        Producto productoRegistrado = productoService.agregarProducto(nuevoProducto, productoDTO.getEspecificacionIds());
        return ResponseEntity.ok(productoRegistrado);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> eliminarProducto(@PathVariable Long id) {
        productoService.eliminarProducto(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/catalogo")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<ProductoSimplificadoDTO>> obtenerCatalogoCompleto() {
        List<ProductoSimplificadoDTO> productos = productoService.listarProductosSimplificados();
        return ResponseEntity.ok(productos);
    }

    @GetMapping("/{id}/detalles")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<ProductoSimplificadoDTO> obtenerDetallesDelProducto(@PathVariable Long id) {
        Producto producto = productoService.obtenerProductoPorId(id);
        ProductoSimplificadoDTO productoSimplificadoDTO = productoService.convertirASimplificadoDTO(producto);
        return ResponseEntity.ok(productoSimplificadoDTO);
    }

    @PutMapping("/{id}/especificaciones")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ProductoSimplificadoDTO> actualizarEspecificaciones(@PathVariable Long id, @RequestBody PersonalizacionDTO personalizacionDTO) {
        Producto productoActualizado = productoService.modificarEspecificaciones(id, personalizacionDTO.getEspecificacionIds());
        ProductoSimplificadoDTO productoSimplificadoDTO = productoService.convertirASimplificadoDTO(productoActualizado);
        return ResponseEntity.ok(productoSimplificadoDTO);
    }

    @PutMapping("/editar/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductoSimplificadoDTO> editarProducto(@PathVariable Long id, @RequestBody ProductoDTO productoDTO) {
        try {
            Producto productoActualizado = productoService.editarProducto(
                    id,
                    productoDTO.getNombre(),
                    productoDTO.getDescripcion(),
                    productoDTO.getPrecio(),
                    productoDTO.getStock(),
                    productoDTO.getImagen()
            );
            ProductoSimplificadoDTO productoSimplificadoDTO = productoService.convertirASimplificadoDTO(productoActualizado);
            return ResponseEntity.ok(productoSimplificadoDTO);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<ProductoSimplificadoDTO>> buscarProductos(@RequestParam String nombre) {
        List<ProductoSimplificadoDTO> productos = productoService.buscarProductosPorNombre(nombre)
                .stream()
                .map(productoService::convertirASimplificadoDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(productos);
    }

    @PostMapping("/registrar-con-especificaciones")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductoSimplificadoDTO> registrarProductoConEspecificaciones(@RequestBody ProductoDTO productoDTO) {
        Producto nuevoProducto = new Producto(
                null,
                productoDTO.getNombre(),
                productoDTO.getPrecio(),
                productoDTO.getDescripcion(),
                productoDTO.getImagen(),
                productoDTO.getStock(),
                new HashSet<>(), // especificacionesDisponibles
                new HashSet<>(), // comentarios
                new HashSet<>()  // favoritos
        );
        Producto productoRegistrado = productoService.agregarProducto(nuevoProducto, productoDTO.getEspecificacionIds());
        ProductoSimplificadoDTO productoSimplificadoDTO = productoService.convertirASimplificadoDTO(productoRegistrado);
        return ResponseEntity.ok(productoSimplificadoDTO);
    }

    @PutMapping("/{productoId}/agregarEspecificacion/{especificacionId}")
    public ResponseEntity<Producto> agregarEspecificacion(
            @PathVariable Long productoId,
            @PathVariable Long especificacionId,
            @RequestBody Map<String, Integer> cantidadMap) {
        int cantidad = cantidadMap.get("cantidad");
        Producto productoActualizado = productoService.agregarEspecificacion(productoId, especificacionId, cantidad);
        return ResponseEntity.ok(productoActualizado);
    }

    @PutMapping("/{productoId}/eliminarEspecificacion/{especificacionId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductoSimplificadoDTO> eliminarEspecificacion(@PathVariable Long productoId, @PathVariable Long especificacionId) {
        Producto productoActualizado = productoService.eliminarEspecificacion(productoId, especificacionId);
        ProductoSimplificadoDTO productoSimplificadoDTO = productoService.convertirASimplificadoDTO(productoActualizado);
        return ResponseEntity.ok(productoSimplificadoDTO);
    }

    @PostMapping("/filtrar")
    public ResponseEntity<List<ProductoSimplificadoDTO>> filtrarProductos(@RequestBody Map<String, Object> filtros) {
        Set<String> ram = filtros.containsKey("ram") ? new HashSet<>((List<String>) filtros.get("ram")) : null;
        Set<String> procesador = filtros.containsKey("procesador") ? new HashSet<>((List<String>) filtros.get("procesador")) : null;
        Set<String> tarjetaGrafica = filtros.containsKey("tarjetaGrafica") ? new HashSet<>((List<String>) filtros.get("tarjetaGrafica")) : null;
        Double precioMin = filtros.containsKey("precioMin") ? (Double) filtros.get("precioMin") : null;
        Double precioMax = filtros.containsKey("precioMax") ? (Double) filtros.get("precioMax") : null;
        Boolean enStock = filtros.containsKey("enStock") ? (Boolean) filtros.get("enStock") : null;

        List<ProductoSimplificadoDTO> productosFiltrados = productoService.filtrarProductos(ram, procesador, tarjetaGrafica, precioMin, precioMax, enStock);
        return ResponseEntity.ok(productosFiltrados);
    }

}
