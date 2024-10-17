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
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Servicio para manejar los productos.
 */
@Service
@RequiredArgsConstructor
public class ProductoService {
    private final ProductoRepository productoRepository;
    private final EspecificacionRepository especificacionRepository;
    private final ProductoEspecificacionRepository productoEspecificacionRepository;

    private static final Set<String> RAM_TYPES = Set.of("RAM", "Memoria RAM");
    private static final Set<String> PROCESSOR_TYPES = Set.of("Procesador", "CPU");
    private static final Set<String> GRAPHICS_CARD_TYPES = Set.of("Tarjeta Gráfica", "GPU");

    /**
     * Lista los productos simplificados.
     *
     * @return Lista de productos simplificados.
     */
    @Transactional(readOnly = true)
    public List<ProductoSimplificadoDTO> listarProductosSimplificados() {
        return productoRepository.findAllByOrderByNombreAsc().stream()
                .map(this::convertirASimplificadoDTO)
                .collect(Collectors.toList());
    }

    /**
     * Agrega un nuevo producto.
     *
     * @param producto Producto a agregar.
     * @param especificacionIds IDs de las especificaciones del producto.
     * @return El producto agregado.
     */
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

    /**
     * Elimina un producto.
     *
     * @param productoId ID del producto a eliminar.
     */
    @Transactional
    public void eliminarProducto(Long productoId) {
        productoRepository.deleteById(productoId);
    }

    /**
     * Obtiene un producto por su ID.
     *
     * @param id ID del producto.
     * @return El producto encontrado.
     */
    @Transactional(readOnly = true)
    public Producto obtenerProductoPorId(Long id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Producto no encontrado"));
    }

    /**
     * Modifica las especificaciones de un producto.
     *
     * @param productoId ID del producto.
     * @param especificacionIds Nuevas especificaciones del producto.
     * @return El producto actualizado.
     */
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

    /**
     * Busca productos por nombre o descripción.
     *
     * @param nombre Nombre o descripción del producto.
     * @return Lista de productos que coinciden con el nombre o descripción.
     */
    @Transactional(readOnly = true)
    public List<Producto> buscarProductosPorNombre(String nombre) {
        return productoRepository.findByNombreContainingIgnoreCaseOrDescripcionContainingIgnoreCase(nombre, nombre);
    }

    /**
     * Edita la información de un producto.
     *
     * @param id ID del producto a editar.
     * @param nombre Nuevo nombre del producto.
     * @param descripcion Nueva descripción del producto.
     * @param precio Nuevo precio del producto.
     * @param stock Nuevo stock del producto.
     * @param imagen Nueva imagen del producto.
     * @return El producto editado.
     */
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

    /**
     * Convierte un producto a un DTO simplificado.
     *
     * @param producto Producto a convertir.
     * @return DTO simplificado del producto.
     */
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

    /**
     * Agrega una especificación a un producto.
     *
     * @param productoId ID del producto.
     * @param especificacionId ID de la especificación.
     * @param cantidad Cantidad de la especificación.
     * @return El producto actualizado.
     */
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

    /**
     * Elimina una especificación de un producto.
     *
     * @param productoId ID del producto.
     * @param especificacionId ID de la especificación.
     * @return El producto actualizado.
     */
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

    /**
     * Filtra los productos según las especificaciones y otros criterios.
     *
     * @param ram Tipos de RAM.
     * @param procesador Tipos de procesador.
     * @param tarjetaGrafica Tipos de tarjeta gráfica.
     * @param precioMin Precio mínimo.
     * @param precioMax Precio máximo.
     * @param enStock Indica si el producto está en stock.
     * @return Lista de productos filtrados.
     */
    @Transactional(readOnly = true)
    public List<ProductoSimplificadoDTO> filtrarProductos(Set<String> ram, Set<String> procesador, Set<String> tarjetaGrafica, Double precioMin, Double precioMax, Boolean enStock) {
        List<Producto> productos = productoRepository.findAll();
        return productos.stream()
                .filter(producto -> filtrarPorEspecificaciones(producto, ram, procesador, tarjetaGrafica))
                .filter(producto -> precioMin == null || producto.getPrecio() >= precioMin)
                .filter(producto -> precioMax == null || producto.getPrecio() <= precioMax)
                .filter(producto -> enStock == null || (enStock && producto.getStock() > 0))
                .map(this::convertirASimplificadoDTO)
                .collect(Collectors.toList());
    }

    /**
     * Filtra los productos según las especificaciones.
     *
     * @param producto Producto a filtrar.
     * @param ram Tipos de RAM.
     * @param procesador Tipos de procesador.
     * @param tarjetaGrafica Tipos de tarjeta gráfica.
     * @return Verdadero si el producto cumple con los criterios, falso en caso contrario.
     */
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
