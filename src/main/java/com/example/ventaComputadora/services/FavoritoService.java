package com.example.ventaComputadora.services;

import com.example.ventaComputadora.domain.DTO.FavoritoDTO;
import com.example.ventaComputadora.domain.entity.Favorito;
import com.example.ventaComputadora.domain.entity.Producto;
import com.example.ventaComputadora.domain.entity.Usuario;
import com.example.ventaComputadora.infra.repository.FavoritoRepository;
import com.example.ventaComputadora.infra.repository.ProductoRepository;
import com.example.ventaComputadora.infra.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FavoritoService {
    private final FavoritoRepository favoritoRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProductoRepository productoRepository;

    @Transactional
    public Favorito agregarAFavoritos(Long usuarioId, Long productoId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        Producto producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        if (favoritoRepository.existsByUsuarioAndProducto(usuario, producto)) {
            throw new RuntimeException("El producto ya está en favoritos");
        }

        Favorito favorito = Favorito.builder()
                .usuario(usuario)
                .producto(producto)
                .build();

        return favoritoRepository.save(favorito);
    }

    @Transactional(readOnly = true)
    public List<FavoritoDTO> listarFavoritosPorUsuario(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        return favoritoRepository.findAllByUsuario(usuario).stream()
                .map(favorito -> new FavoritoDTO(
                        favorito.getProducto().getId(),
                        favorito.getProducto().getNombre(),
                        favorito.getProducto().getDescripcion(),
                        favorito.getProducto().getPrecio(),
                        favorito.getProducto().getImagen()))
                .collect(Collectors.toList());
    }

    @Transactional
    public void eliminarDeFavoritos(Long favoritoId) {
        favoritoRepository.deleteById(favoritoId);
    }

    @Transactional
    public void eliminarDeFavoritosPorUsuarioYProducto(Long usuarioId, Long productoId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        Producto producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        Favorito favorito = favoritoRepository.findByUsuarioAndProducto(usuario, producto)
                .orElseThrow(() -> new RuntimeException("Favorito no encontrado"));

        favoritoRepository.delete(favorito);
    }

}
