package com.example.ventaComputadora.domain.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ComentarioDTO {
    private Long id;
    private String nombreProducto;
    private String contenido;
    private Long usuarioId;
    private String nombreUsuario;
    private LocalDateTime fechaCreacion;
}
