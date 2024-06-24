package com.example.ventaComputadora.infra.repository;

import com.example.ventaComputadora.domain.entity.Comentario;
import com.example.ventaComputadora.domain.entity.Producto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ComentarioRepository extends JpaRepository<Comentario, Long> {
    List<Comentario> findAllByProducto(Producto producto);
}