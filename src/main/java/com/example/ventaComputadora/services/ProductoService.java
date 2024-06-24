package com.example.ventaComputadora.services;

import com.example.ventaComputadora.domain.DTO.ComentarioDTO;
import com.example.ventaComputadora.domain.DTO.EspecificacionSimplificadaDTO;
import com.example.ventaComputadora.domain.DTO.ProductoSimplificadoDTO;
import com.example.ventaComputadora.domain.entity.Especificacion;
import com.example.ventaComputadora.domain.entity.Producto;
import com.example.ventaComputadora.domain.entity.ProductoEspecificacion;
import com.example.ventaComputadora.domain.entity.ProductoEspecificacionId;
import com.example.ventaComputadora.infra.repository.EspecificacionRepository;
import com.example.ventaComputadora.infra.repository.ProductoEspecificacionRepository;
import com.example.ventaComputadora.infra.repository.ProductoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ProductoService {
    private final ProductoRepository productoRepository;
    private final EspecificacionRepository especificacionRepository;
    private final ProductoEspecificacionRepository productoEspecificacionRepository;


    private static final Set<String> RAM_TYPES = Set.of("RAM", "Memoria RAM");
    private static final Set<String> PROCESSOR_TYPES = Set.of("Procesador", "CPU");
    private static final Set<String> GRAPHICS_CARD_TYPES = Set.of("Tarjeta Gráfica", "GPU");

    @Autowired
    public ProductoService(ProductoRepository productoRepository, EspecificacionRepository especificacionRepository, ProductoEspecificacionRepository productoEspecificacionRepository) {
        this.productoRepository = productoRepository;
        this.especificacionRepository = especificacionRepository;
        this.productoEspecificacionRepository = productoEspecificacionRepository;
    }

    @Transactional(readOnly = true)
    public List<ProductoSimplificadoDTO> listarProductosSimplificados() {
        return productoRepository.findAllByOrderByNombreAsc().stream()
                .map(this::convertirASimplificadoDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public Producto agregarProducto(Producto producto, Set<Long> especificacionIds) {
        if (productoRepository.findByNombreIgnoreCase(producto.getNombre()).isPresent()) {
            throw new DataIntegrityViolationException("Ya existe un producto con el nombre proporcionado");
        }

        if (producto.getEspecificacionesDisponibles() == null) {
            producto.setEspecificacionesDisponibles(new HashSet<>());
        }

        List<Especificacion> especificaciones = especificacionRepository.findAllById(especificacionIds);
        double precioTotal = producto.getPrecio();
        for (Especificacion especificacion : especificaciones) {
            ProductoEspecificacion productoEspecificacion = new ProductoEspecificacion(producto, especificacion, 1);
            producto.getEspecificacionesDisponibles().add(productoEspecificacion);
            precioTotal += especificacion.getPrecioAdicional();
        }
        producto.setPrecio(precioTotal);

        return productoRepository.save(producto);
    }

    @Transactional
    public void eliminarProducto(Long productoId) {
        productoRepository.deleteById(productoId);
    }

    @Transactional(readOnly = true)
    public Producto obtenerProductoPorId(Long id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Producto no encontrado"));
    }

    @Transactional
    public Producto modificarEspecificaciones(Long productoId, Set<Long> especificacionIds) {
        Producto producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new EntityNotFoundException("Producto no encontrado"));

        if (producto.getEspecificacionesDisponibles() == null) {
            producto.setEspecificacionesDisponibles(new HashSet<>());
        } else {
            producto.getEspecificacionesDisponibles().clear();
        }

        List<Especificacion> especificaciones = especificacionRepository.findAllById(especificacionIds);
        double precioTotal = producto.getPrecio();
        for (Especificacion especificacion : especificaciones) {
            ProductoEspecificacion productoEspecificacion = new ProductoEspecificacion(producto, especificacion, 1);
            producto.getEspecificacionesDisponibles().add(productoEspecificacion);
            precioTotal += especificacion.getPrecioAdicional();
        }
        producto.setPrecio(precioTotal);

        return productoRepository.save(producto);
    }

    @Transactional(readOnly = true)
    public List<Producto> buscarProductosPorNombre(String nombre) {
        return productoRepository.findByNombreContainingIgnoreCaseOrDescripcionContainingIgnoreCase(nombre, nombre);
    }

    @Transactional
    public Producto editarProducto(Long id, String nombre, String descripcion, double precio, int stock, String imagen) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Producto no encontrado"));
        producto.setNombre(nombre);
        producto.setDescripcion(descripcion);
        producto.setPrecio(precio);
        producto.setStock(stock);
        producto.setImagen(imagen);

        return productoRepository.save(producto);
    }

    public ProductoSimplificadoDTO convertirASimplificadoDTO(Producto producto) {
        return new ProductoSimplificadoDTO(
                producto.getId(),
                producto.getNombre(),
                producto.getPrecio(),
                producto.getDescripcion(),
                producto.getImagen(),
                producto.getStock(),
                producto.getEspecificacionesDisponibles().stream()
                        .map(productoEspecificacion -> new EspecificacionSimplificadaDTO(
                                productoEspecificacion.getEspecificacion().getId(),
                                productoEspecificacion.getEspecificacion().getNombre(),
                                productoEspecificacion.getEspecificacion().getPrecioAdicional(),
                                productoEspecificacion.getCantidad() // Asegúrate de incluir la cantidad aquí
                        ))
                        .collect(Collectors.toSet()),
                producto.getComentarios().stream()
                        .map(comentario -> new ComentarioDTO(
                                comentario.getId(),
                                producto.getNombre(),
                                comentario.getContenido(),
                                comentario.getUsuario().getId(),
                                comentario.getUsuario().getNombre() + " " + comentario.getUsuario().getApellido(),
                                comentario.getFecha()
                        ))
                        .collect(Collectors.toSet()),
                producto.getFavoritos().stream()
                        .map(favorito -> favorito.getUsuario().getUsername())
                        .collect(Collectors.toSet())
        );
    }


    @Transactional
    public Producto agregarEspecificacion(Long productoId, Long especificacionId, int cantidad) {
        Producto producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new EntityNotFoundException("Producto no encontrado"));

        Especificacion especificacion = especificacionRepository.findById(especificacionId)
                .orElseThrow(() -> new EntityNotFoundException("Especificacion no encontrada"));

        ProductoEspecificacionId productoEspecificacionId = new ProductoEspecificacionId(productoId, especificacionId);

        Optional<ProductoEspecificacion> existingProductoEspecificacion = productoEspecificacionRepository.findById(productoEspecificacionId);

        if (existingProductoEspecificacion.isPresent()) {
            ProductoEspecificacion productoEspecificacion = existingProductoEspecificacion.get();
            productoEspecificacion.setCantidad(productoEspecificacion.getCantidad() + cantidad);
            productoEspecificacionRepository.save(productoEspecificacion);
        } else {
            ProductoEspecificacion productoEspecificacion = new ProductoEspecificacion(producto, especificacion, cantidad);
            productoEspecificacionRepository.save(productoEspecificacion);
        }

        // Actualizar el precio del producto
        double precioTotal = producto.getPrecio();
        precioTotal += especificacion.getPrecioAdicional() * cantidad;
        producto.setPrecio(precioTotal);

        return productoRepository.save(producto);
    }

    @Transactional
    public Producto eliminarEspecificacion(Long productoId, Long especificacionId) {
        Producto producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new EntityNotFoundException("Producto no encontrado"));

        ProductoEspecificacionId productoEspecificacionId = new ProductoEspecificacionId(productoId, especificacionId);
        ProductoEspecificacion productoEspecificacion = productoEspecificacionRepository.findById(productoEspecificacionId)
                .orElseThrow(() -> new EntityNotFoundException("ProductoEspecificacion no encontrada"));

        // Actualizar el precio del producto antes de eliminar la especificación
        double precioTotal = producto.getPrecio();
        precioTotal -= productoEspecificacion.getEspecificacion().getPrecioAdicional() * productoEspecificacion.getCantidad();
        producto.setPrecio(precioTotal);

        productoEspecificacionRepository.deleteById(productoEspecificacionId);

        // Actualiza el producto después de la eliminación
        producto.getEspecificacionesDisponibles().removeIf(pe -> pe.getEspecificacion().getId().equals(especificacionId));
        return productoRepository.save(producto);
    }

    /////////////////////////////
    @Transactional(readOnly = true)
    public List<ProductoSimplificadoDTO> filtrarProductos(Set<String> ram, Set<String> procesador, Set<String> tarjetaGrafica, Double precioMin, Double precioMax, Boolean enStock) {
        List<Producto> productos = productoRepository.findAll();
        System.out.println("Filtrando productos con RAM: " + ram + ", Procesador: " + procesador + ", Tarjeta Gráfica: " + tarjetaGrafica + ", PrecioMin: " + precioMin + ", PrecioMax: " + precioMax + ", EnStock: " + enStock);

        return productos.stream()
                .filter(producto -> filtrarPorEspecificaciones(producto, ram, procesador, tarjetaGrafica))
                .filter(producto -> precioMin == null || producto.getPrecio() >= precioMin)
                .filter(producto -> precioMax == null || producto.getPrecio() <= precioMax)
                .filter(producto -> enStock == null || (enStock && producto.getStock() > 0))
                .map(this::convertirASimplificadoDTO)
                .collect(Collectors.toList());
    }

    private boolean filtrarPorEspecificaciones(Producto producto, Set<String> ram, Set<String> procesador, Set<String> tarjetaGrafica) {
        boolean coincide = true;

        if (!CollectionUtils.isEmpty(ram)) {
            boolean coincideRam = producto.getEspecificacionesDisponibles().stream()
                    .map(ProductoEspecificacion::getEspecificacion)
                    .anyMatch(e -> RAM_TYPES.contains(e.getTipo()) && ram.stream().anyMatch(r -> e.getNombre().toLowerCase().contains(r.toLowerCase())));
            if (!coincideRam) {
                coincide = false;
            }
        }

        if (!CollectionUtils.isEmpty(procesador)) {
            boolean coincideProcesador = producto.getEspecificacionesDisponibles().stream()
                    .map(ProductoEspecificacion::getEspecificacion)
                    .anyMatch(e -> PROCESSOR_TYPES.contains(e.getTipo()) && procesador.stream().anyMatch(p -> e.getNombre().toLowerCase().contains(p.toLowerCase())));
            if (!coincideProcesador) {
                coincide = false;
            }
        }

        if (!CollectionUtils.isEmpty(tarjetaGrafica)) {
            boolean coincideTarjetaGrafica = producto.getEspecificacionesDisponibles().stream()
                    .map(ProductoEspecificacion::getEspecificacion)
                    .anyMatch(e -> GRAPHICS_CARD_TYPES.contains(e.getTipo()) && tarjetaGrafica.stream().anyMatch(t -> e.getNombre().toLowerCase().contains(t.toLowerCase())));
            if (!coincideTarjetaGrafica) {
                coincide = false;
            }
        }

        return coincide;
    }
}
