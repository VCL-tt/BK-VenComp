package com.example.ventaComputadora.infra.repository;

import com.example.ventaComputadora.domain.entity.ProductoEspecificacion;
import com.example.ventaComputadora.domain.entity.ProductoEspecificacionId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductoEspecificacionRepository extends JpaRepository<ProductoEspecificacion, ProductoEspecificacionId> {
    boolean existsByEspecificacionId(Long especificacionId);
}