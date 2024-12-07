package com.example.ventaComputadora.webController;

import com.example.ventaComputadora.domain.DTO.ProductoCrearDTO;
import com.example.ventaComputadora.domain.DTO.ProductoSimplificadoDTO;
import com.example.ventaComputadora.domain.entity.Producto;
import com.example.ventaComputadora.domain.entity.Usuario;
import com.example.ventaComputadora.domain.entity.enums.CategoriaProducto;
import com.example.ventaComputadora.domain.entity.enums.TipoProducto;
import com.example.ventaComputadora.services.ProductoService;
import com.example.ventaComputadora.services.implement.UsuarioService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;
import java.util.List;

/**
 * Controlador para manejar las operaciones relacionadas con productos.
 */
@RestController
@RequestMapping("/api/productos")
@RequiredArgsConstructor
public class ProductoController {

    private final ProductoService productoService;
    private final UsuarioService usuarioService;
    // Endpoint para listar todos los productos con el path "listar"
    @GetMapping("/listar")
    public ResponseEntity<List<ProductoSimplificadoDTO>> listarProductos() {
        Usuario usuarioAutenticado = usuarioService.getAuthenticatedUser();
        List<ProductoSimplificadoDTO> productos = productoService.listarProductos();
        return ResponseEntity.ok(productos);
    }

    // Endpoint para filtrar productos por categoría
    @GetMapping("/categoria/{categoria}")
    public ResponseEntity<List<ProductoSimplificadoDTO>> listarProductosPorCategoria(@PathVariable CategoriaProducto categoria) {
        Usuario usuarioAutenticado = usuarioService.getAuthenticatedUser();
        List<ProductoSimplificadoDTO> productos = productoService.listarProductosPorCategoria(categoria);
        return ResponseEntity.ok(productos);
    }

    // Endpoint para crear un producto con imagen en base64
    @PostMapping("/crear")
    public ResponseEntity<Producto> crearProducto(
            @RequestParam("nombre") String nombre,
            @RequestParam("descripcion") String descripcion,
            @RequestParam("precio") double precio,
            @RequestParam("stock") int stock,
            @RequestParam("categoria") CategoriaProducto categoria,
            @RequestParam("tipo") String tipoString, // Recibe el tipo como String
            @RequestParam("imagen") MultipartFile imagen) {

        Usuario usuarioAutenticado = usuarioService.getAuthenticatedUser();

        // Convertir la imagen a Base64
        String imagenBase64;
        try {
            imagenBase64 = Base64.getEncoder().encodeToString(imagen.getBytes());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }

        // Convertir el tipo de String a enum TipoProducto
        TipoProducto tipo = TipoProducto.valueOf(tipoString.toUpperCase());

        // Crear el DTO para la creación del producto
        ProductoCrearDTO productoCrearDTO = new ProductoCrearDTO();
        productoCrearDTO.setNombre(nombre);
        productoCrearDTO.setDescripcion(descripcion);
        productoCrearDTO.setPrecio(precio);
        productoCrearDTO.setStock(stock);
        productoCrearDTO.setCategoria(categoria);
        productoCrearDTO.setTipo(tipo);  // Asignar el tipo convertido a enum
        productoCrearDTO.setImagen(imagenBase64);  // Guardar la imagen en formato base64

        // Crear el producto usando el servicio
        Producto nuevoProducto = productoService.crearProducto(productoCrearDTO);

        return new ResponseEntity<>(nuevoProducto, HttpStatus.CREATED);
    }
    @GetMapping("/{id}/detalles")
    public ResponseEntity<ProductoSimplificadoDTO> obtenerDetallesDelProducto(@PathVariable Long id) {
        Producto producto = productoService.obtenerProductoPorId(id);
        ProductoSimplificadoDTO productoSimplificadoDTO = productoService.convertirASimplificadoDTO(producto);
        return ResponseEntity.ok(productoSimplificadoDTO);
    }
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<String> handleEntityNotFoundException(EntityNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }
}
