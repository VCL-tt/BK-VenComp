package com.example.ventaComputadora.domain.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "especificaciones")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Especificacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false)
    private double precioAdicional;

    @Column(length = 5000)
    private String descripcion;

    @Column(nullable = false, length = 100)
    private String marca; // Nuevo campo agregado

    @Column(nullable = false, length = 100)
    private String tipo; // Nuevo campo agregado

    @OneToMany(mappedBy = "especificacion", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ProductoEspecificacion> productoEspecificaciones = new HashSet<>();
}
