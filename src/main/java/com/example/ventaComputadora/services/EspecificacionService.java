package com.example.ventaComputadora.services;

import com.example.ventaComputadora.domain.DTO.EspecificacionDTO;
import com.example.ventaComputadora.domain.entity.Especificacion;
import com.example.ventaComputadora.infra.repository.EspecificacionRepository;
import com.example.ventaComputadora.infra.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio para manejar las especificaciones de productos.
 */
@Service
@RequiredArgsConstructor
public class EspecificacionService {
    private final EspecificacionRepository especificacionRepository;
    private final ProductoRepository productoRepository;

    /**
     * Registra una nueva especificación.
     *
     * @param nombre Nombre de la especificación.
     * @param descripcion Descripción de la especificación.
     * @param precioAdicional Precio adicional de la especificación.
     * @param marca Marca de la especificación.
     * @param tipo Tipo de la especificación.
     * @return La especificación registrada.
     */
    @Transactional
    public Especificacion registrarEspecificacion(String nombre, String descripcion, double precioAdicional, String marca, String tipo) {
        Especificacion especificacion = Especificacion.builder()
                .nombre(nombre)
                .descripcion(descripcion)
                .precioAdicional(precioAdicional)
                .marca(marca)
                .tipo(tipo)
                .build();

        return especificacionRepository.save(especificacion);
    }

    /**
     * Elimina una especificación.
     *
     * @param especificacionId ID de la especificación a eliminar.
     */
    @Transactional
    public void eliminarEspecificacion(Long especificacionId) {
        if (productoRepository.existsById(especificacionId)) {
            throw new DataIntegrityViolationException("No se puede eliminar la especificación porque está asociada a un producto.");
        }
        especificacionRepository.deleteById(especificacionId);
    }

    /**
     * Actualiza una especificación existente.
     *
     * @param id ID de la especificación.
     * @param especificacion Nueva información de la especificación.
     * @return La especificación actualizada.
     */
    @Transactional
    public Especificacion actualizarEspecificacion(Long id, Especificacion especificacion) {
        Especificacion especificacionExistente = especificacionRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("Especificación no encontrada."));
        especificacionExistente.setNombre(especificacion.getNombre());
        especificacionExistente.setDescripcion(especificacion.getDescripcion());
        especificacionExistente.setPrecioAdicional(especificacion.getPrecioAdicional());
        especificacionExistente.setMarca(especificacion.getMarca());
        especificacionExistente.setTipo(especificacion.getTipo());
        return especificacionRepository.save(especificacionExistente);
    }

    /**
     * Busca especificaciones por nombre.
     *
     * @param nombre Nombre de la especificación.
     * @return Lista de especificaciones que coinciden con el nombre.
     */
    @Transactional(readOnly = true)
    public List<EspecificacionDTO> buscarEspecificacionesPorNombre(String nombre) {
        return especificacionRepository.findByNombreContainingIgnoreCase(nombre)
                .stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    /**
     * Busca especificaciones por marca y tipo.
     *
     * @param marca Marca de la especificación.
     * @param tipo Tipo de la especificación.
     * @return Lista de especificaciones que coinciden con la marca y tipo.
     */
    @Transactional(readOnly = true)
    public List<EspecificacionDTO> buscarEspecificacionesPorMarcaYTipo(String marca, String tipo) {
        return especificacionRepository.findByMarcaContainingIgnoreCaseAndTipoContainingIgnoreCase(marca, tipo)
                .stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    /**
     * Lista todas las especificaciones.
     *
     * @return Lista de todas las especificaciones.
     */
    @Transactional(readOnly = true)
    public List<EspecificacionDTO> listarEspecificaciones() {
        return especificacionRepository.findAll()
                .stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    /**
     * Convierte una especificación a un DTO.
     *
     * @param especificacion Especificación a convertir.
     * @return DTO de la especificación.
     */
    private EspecificacionDTO convertirADTO(Especificacion especificacion) {
        return new EspecificacionDTO(
                especificacion.getId(),
                especificacion.getNombre(),
                especificacion.getDescripcion(),
                especificacion.getPrecioAdicional(),
                especificacion.getMarca(),
                especificacion.getTipo()
        );
    }
}
