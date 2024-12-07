package com.example.ventaComputadora.services;

import com.example.ventaComputadora.domain.DTO.ProductoCrearDTO;
import com.example.ventaComputadora.domain.DTO.ProductoDTO;
import com.example.ventaComputadora.domain.DTO.ProductoSimplificadoDTO;
import com.example.ventaComputadora.domain.entity.Producto;
import com.example.ventaComputadora.domain.entity.enums.CategoriaProducto;

import java.util.List;

public interface ProductoService {

    // Método para listar todos los productos
    List<ProductoSimplificadoDTO> listarProductos();

    // Método para filtrar productos por categoría
    List<ProductoSimplificadoDTO> listarProductosPorCategoria(CategoriaProducto categoria);

    Producto crearProducto(ProductoCrearDTO productoCrearDTO);

    Producto obtenerProductoPorId(Long id);

    ProductoSimplificadoDTO convertirASimplificadoDTO(Producto producto);
}
