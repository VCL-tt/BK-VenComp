package com.example.ventaComputadora.infra.repository;

import com.example.ventaComputadora.domain.entity.Especificacion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EspecificacionRepository extends JpaRepository<Especificacion, Long> {
    List<Especificacion> findByNombreContainingIgnoreCase(String nombre);
    Optional<Especificacion> findByNombreIgnoreCase(String nombre);
    List<Especificacion> findByMarcaContainingIgnoreCase(String marca);
    List<Especificacion> findByTipoContainingIgnoreCase(String tipo);
    List<Especificacion> findByMarcaContainingIgnoreCaseAndTipoContainingIgnoreCase(String marca, String tipo);
}