package com.example.ventaComputadora.infra.repository;

import com.example.ventaComputadora.domain.entity.Producto;
import com.example.ventaComputadora.domain.entity.enums.CategoriaProducto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductoRepository extends JpaRepository<Producto, Long> {
    // Cambiar el nombre del método para que coincida con la propiedad en la entidad Producto
    List<Producto> findByCategoria(CategoriaProducto categoria);
}
