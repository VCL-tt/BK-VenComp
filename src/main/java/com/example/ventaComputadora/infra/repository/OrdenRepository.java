package com.example.ventaComputadora.infra.repository;

import com.example.ventaComputadora.domain.entity.enums.EstadoOrden;
import com.example.ventaComputadora.domain.entity.Orden;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrdenRepository extends JpaRepository<Orden, Long> {

    List<Orden> findByUsuarioIdAndEstado(Long usuarioId, EstadoOrden estado);
    List<Orden> findByUsuarioId(Long usuarioId);
}