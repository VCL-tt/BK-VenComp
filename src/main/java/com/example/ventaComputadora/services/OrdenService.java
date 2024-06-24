package com.example.ventaComputadora.services;

import com.example.ventaComputadora.domain.DTO.OrdenDTO;
import com.example.ventaComputadora.domain.DTO.ProductoDTO;
import com.example.ventaComputadora.domain.DTO.UsuarioDTO;
import com.example.ventaComputadora.domain.entity.EstadoOrden;
import com.example.ventaComputadora.domain.entity.Orden;
import com.example.ventaComputadora.domain.entity.Producto;
import com.example.ventaComputadora.domain.entity.Usuario;
import com.example.ventaComputadora.infra.repository.OrdenRepository;
import com.example.ventaComputadora.infra.repository.ProductoRepository;
import com.example.ventaComputadora.infra.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrdenService {
    private final OrdenRepository ordenRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProductoRepository productoRepository;

    @Transactional
    public Orden crearOrden(Long usuarioId, Set<Long> productoIds) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        Set<Producto> productos = new HashSet<>(productoRepository.findAllById(productoIds));

        Orden orden = Orden.builder()
                .usuario(usuario)
                .productos(productos)
                .fechaCreacion(LocalDateTime.now())
                .estado(EstadoOrden.CARRITO)
                .build();

        return ordenRepository.save(orden);
    }

    @Transactional
    public Orden agregarProductoALaOrden(Long usuarioId, Long productoId) {
        Optional<Orden> ordenActiva = obtenerOrdenActiva(usuarioId);
        Orden orden;
        if (ordenActiva.isEmpty()) {
            orden = crearOrden(usuarioId, new HashSet<>());
        } else {
            orden = ordenActiva.get();
        }

        Producto producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        orden.getProductos().add(producto);
        return ordenRepository.save(orden);
    }

    @Transactional
    public Orden eliminarProductoDeLaOrden(Long ordenId, Long productoId) {
        Orden orden = ordenRepository.findById(ordenId)
                .orElseThrow(() -> new RuntimeException("Orden no encontrada"));

        if (EstadoOrden.PAGADO.equals(orden.getEstado())) {
            throw new IllegalStateException("No se puede eliminar un producto de una orden pagada");
        }

        Producto producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        orden.getProductos().remove(producto);
        return ordenRepository.save(orden);
    }

    @Transactional(readOnly = true)
    public List<OrdenDTO> listarOrdenesPorUsuario(Long usuarioId) {
        List<Orden> ordenes = ordenRepository.findByUsuarioId(usuarioId);
        ordenes.forEach(orden -> Hibernate.initialize(orden.getProductos())); // Forzar la carga de los productos
        return ordenes.stream().map(this::convertirADTO).collect(Collectors.toList());
    }

    public OrdenDTO convertirADTO(Orden orden) {
        double montoTotal = orden.getProductos().stream()
                .mapToDouble(Producto::getPrecio)
                .sum();

        Set<ProductoDTO> productosDTO = orden.getProductos().stream()
                .map(this::convertirProductoADTO)
                .collect(Collectors.toSet());

        return new OrdenDTO(
                orden.getId(),
                convertirUsuarioADTO(orden.getUsuario()),
                orden.getFechaCreacion(),
                orden.getEstado().name(),
                productosDTO,
                montoTotal
        );
    }

    private UsuarioDTO convertirUsuarioADTO(Usuario usuario) {
        return new UsuarioDTO(
                usuario.getId(),
                usuario.getNombre(),
                usuario.getApellido(),
                usuario.getCorreo()
        );
    }

    private ProductoDTO convertirProductoADTO(Producto producto) {
        ProductoDTO productoDTO = new ProductoDTO();
        productoDTO.setId(producto.getId());
        productoDTO.setNombre(producto.getNombre());
        productoDTO.setDescripcion(producto.getDescripcion());
        productoDTO.setPrecio(producto.getPrecio());
        productoDTO.setStock(producto.getStock());
        productoDTO.setImagen(producto.getImagen());
        return productoDTO;
    }

    @Transactional(readOnly = true)
    public Optional<Orden> obtenerOrdenActiva(Long usuarioId) {
        return ordenRepository.findByUsuarioIdAndEstado(usuarioId, EstadoOrden.CARRITO)
                .stream().findFirst();
    }

    @Transactional
    public Orden procesarOrden(Long ordenId) {
        Orden orden = ordenRepository.findById(ordenId)
                .orElseThrow(() -> new RuntimeException("Orden no encontrada"));
        orden.setEstado(EstadoOrden.PAGADO);
        return ordenRepository.save(orden);
    }

    @Transactional
    public Orden actualizarOrden(Long id, Orden nuevaOrden) {
        Orden orden = ordenRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Orden no encontrada"));
        orden.setProductos(nuevaOrden.getProductos());
        orden.setEstado(nuevaOrden.getEstado());
        return ordenRepository.save(orden);
    }

    @Transactional
    public void eliminarOrden(Long id) {
        ordenRepository.deleteById(id);
    }
}
