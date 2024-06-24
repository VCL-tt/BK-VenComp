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

@Service
@RequiredArgsConstructor
public class EspecificacionService {
    private final EspecificacionRepository especificacionRepository;
    private final ProductoRepository productoRepository;

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

    @Transactional
    public void eliminarEspecificacion(Long especificacionId) {
        if (productoRepository.existsById(especificacionId)) {
            throw new DataIntegrityViolationException("No se puede eliminar la especificación porque está asociada a un producto.");
        }
        especificacionRepository.deleteById(especificacionId);
    }

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

    @Transactional(readOnly = true)
    public List<EspecificacionDTO> buscarEspecificacionesPorNombre(String nombre) {
        return especificacionRepository.findByNombreContainingIgnoreCase(nombre)
                .stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<EspecificacionDTO> buscarEspecificacionesPorMarcaYTipo(String marca, String tipo) {
        return especificacionRepository.findByMarcaContainingIgnoreCaseAndTipoContainingIgnoreCase(marca, tipo)
                .stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<EspecificacionDTO> listarEspecificaciones() {
        return especificacionRepository.findAll()
                .stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

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
