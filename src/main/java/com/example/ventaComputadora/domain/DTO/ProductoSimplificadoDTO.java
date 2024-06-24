package com.example.ventaComputadora.domain.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductoSimplificadoDTO {
    private Long id;
    private String nombre;
    private double precio;
    private String descripcion;
    private String imagen;
    private int stock;
    private Set<EspecificacionSimplificadaDTO> especificacionesDisponibles;
    private Set<ComentarioDTO> comentarios;
    private Set<String> favoritos;
}
