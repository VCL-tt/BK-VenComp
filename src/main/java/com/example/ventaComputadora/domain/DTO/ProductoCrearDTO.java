package com.example.ventaComputadora.domain.DTO;

import com.example.ventaComputadora.domain.entity.enums.CategoriaProducto;
import com.example.ventaComputadora.domain.entity.enums.TipoProducto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductoCrearDTO {
    private String nombre;
    private String descripcion;
    private double precio;
    private int stock;
    private String imagen;
    private CategoriaProducto categoria;
    private TipoProducto tipo;
}
