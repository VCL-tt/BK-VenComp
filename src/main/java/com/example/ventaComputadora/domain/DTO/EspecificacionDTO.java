package com.example.ventaComputadora.domain.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EspecificacionDTO {
    private Long id;
    private String nombre;
    private String descripcion;
    private double precioAdicional;
    private String marca;
    private String tipo;
}
