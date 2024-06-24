package com.example.ventaComputadora.infra.repository;

import com.example.ventaComputadora.domain.entity.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {
    List<Producto> findByNombreContainingIgnoreCaseOrDescripcionContainingIgnoreCase(String nombre, String descripcion);
    List<Producto> findAllByOrderByNombreAsc();
    Optional<Producto> findByNombreIgnoreCase(String nombre);
}
