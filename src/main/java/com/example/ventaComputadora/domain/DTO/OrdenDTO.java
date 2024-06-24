package com.example.ventaComputadora.domain.DTO;

import com.example.ventaComputadora.domain.entity.Usuario;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrdenDTO {
    private Long id;
    private UsuarioDTO usuario;
    private LocalDateTime fechaCreacion;
    private String estado;
    private Set<ProductoDTO> productos;
    private double montoTotal;
}
