package com.example.ventaComputadora.domain.DTO;

import com.example.ventaComputadora.domain.entity.Comentario;
import com.example.ventaComputadora.domain.entity.Favorito;
import com.example.ventaComputadora.domain.entity.Orden;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioDTO {
    private Long id;
    private String nombre;
    private String apellido;
    private String correo;
    private String telefono;
    private String dni;
    private String username;
    private List<Orden> ordenes;
    private List<Comentario> comentarios;
    private List<Favorito> favoritos;
    // Constructor simplificado
    public UsuarioDTO(Long id, String nombre, String apellido, String correo) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.correo = correo;
    }
}
