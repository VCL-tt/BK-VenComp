package com.example.ventaComputadora.domain.entity;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class ProductoEspecificacionId implements Serializable {

    private Long productoId;
    private Long especificacionId;

    public ProductoEspecificacionId() {}

    public ProductoEspecificacionId(Long productoId, Long especificacionId) {
        this.productoId = productoId;
        this.especificacionId = especificacionId;
    }

    // Getters, setters, equals, and hashCode methods

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProductoEspecificacionId that = (ProductoEspecificacionId) o;
        return Objects.equals(productoId, that.productoId) &&
                Objects.equals(especificacionId, that.especificacionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productoId, especificacionId);
    }
}