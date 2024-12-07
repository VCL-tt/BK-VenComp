package com.example.ventaComputadora.services.implement;

import com.example.ventaComputadora.domain.DTO.ComentarioDTO;
import com.example.ventaComputadora.domain.DTO.ProductoCrearDTO;
import com.example.ventaComputadora.domain.DTO.ProductoSimplificadoDTO;
import com.example.ventaComputadora.domain.entity.Producto;
import com.example.ventaComputadora.domain.entity.enums.CategoriaProducto;
import com.example.ventaComputadora.infra.repository.ProductoRepository;
import com.example.ventaComputadora.services.ProductoService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementación del servicio de productos.
 */
@Service
@RequiredArgsConstructor
public class ProductoServiceImpl implements ProductoService {

    private final ProductoRepository productoRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ProductoSimplificadoDTO> listarProductos() {
        return productoRepository.findAll()
                .stream()
                .map(this::convertirAProductoSimplificadoDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductoSimplificadoDTO> listarProductosPorCategoria(CategoriaProducto categoria) {
        return productoRepository.findByCategoria(categoria)  // Usamos el método corregido
                .stream()
                .map(this::convertirAProductoSimplificadoDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Producto crearProducto(ProductoCrearDTO productoCrearDTO) {
        // Crear un nuevo producto a partir del DTO
        Producto nuevoProducto = Producto.builder()
                .nombre(productoCrearDTO.getNombre())
                .descripcion(productoCrearDTO.getDescripcion())
                .precio(productoCrearDTO.getPrecio())
                .stock(productoCrearDTO.getStock())
                .imagen(productoCrearDTO.getImagen())
                .categoria(productoCrearDTO.getCategoria())
                .tipo(productoCrearDTO.getTipo())
                .build();

        // Guardar el nuevo producto en la base de datos
        return productoRepository.save(nuevoProducto);
    }
    private ProductoSimplificadoDTO convertirAProductoSimplificadoDTO(Producto producto) {
        return new ProductoSimplificadoDTO(
                producto.getId(),
                producto.getNombre(),
                producto.getPrecio(),
                producto.getDescripcion(),
                producto.getImagen(),
                producto.getStock(),
                producto.getComentarios().stream()
                        .map(comentario -> new ComentarioDTO(/* propiedades del comentario */))
                        .collect(Collectors.toSet()), // Mapeo de comentarios a DTO
                producto.getFavoritos().stream()
                        .map(favorito -> favorito.getUsuario().getNombre()) // o algún campo relevante
                        .collect(Collectors.toSet()), // Mapeo de favoritos
                producto.getCategoria(), // Mapeo de categoría
                producto.getTipo()       // Mapeo de tipo
        );
    }
    @Transactional(readOnly = true)
    public Producto obtenerProductoPorId(Long id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Producto no encontrado"));
    }

    @Override
    public ProductoSimplificadoDTO convertirASimplificadoDTO(Producto producto) {
        if (producto == null) {
            return null;
        }

        ProductoSimplificadoDTO dto = new ProductoSimplificadoDTO();
        dto.setId(producto.getId());
        dto.setNombre(producto.getNombre());
        dto.setDescripcion(producto.getDescripcion());
        dto.setPrecio(producto.getPrecio());
        dto.setStock(producto.getStock());
        dto.setCategoria(producto.getCategoria());
        dto.setTipo(producto.getTipo());
        dto.setImagen(producto.getImagen()); // Si la imagen está en base64, pasa la cadena

        return dto;
    }
}

