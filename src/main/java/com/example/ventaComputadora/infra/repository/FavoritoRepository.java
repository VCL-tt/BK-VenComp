package com.example.ventaComputadora.infra.repository;

import com.example.ventaComputadora.domain.entity.Favorito;
import com.example.ventaComputadora.domain.entity.Producto;
import com.example.ventaComputadora.domain.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FavoritoRepository extends JpaRepository<Favorito, Long> {
    List<Favorito> findAllByUsuario(Usuario usuario);
    Optional<Favorito> findByUsuarioAndProducto(Usuario usuario, Producto producto);
    boolean existsByUsuarioAndProducto(Usuario usuario, Producto producto);
}