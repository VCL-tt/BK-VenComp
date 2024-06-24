package com.example.ventaComputadora.domain.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "producto_especificaciones")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class ProductoEspecificacion {
    @EmbeddedId
    private ProductoEspecificacionId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("productoId")
    private Producto producto;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("especificacionId")
    private Especificacion especificacion;

    private int cantidad;

    public ProductoEspecificacion(Producto producto, Especificacion especificacion, int cantidad) {
        this.id = new ProductoEspecificacionId(producto.getId(), especificacion.getId());
        this.producto = producto;
        this.especificacion = especificacion;
        this.cantidad = cantidad;
    }
}
