package com.example.ventaComputadora.domain.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EspecificacionSimplificadaDTO {
    private Long id;
    private String nombre;
    private double precioAdicional;
    private int cantidad;
}