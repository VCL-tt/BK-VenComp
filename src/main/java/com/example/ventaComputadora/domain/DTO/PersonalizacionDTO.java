package com.example.ventaComputadora.domain.DTO;

import lombok.Data;
import java.util.Set;

@Data
public class PersonalizacionDTO {
    private Set<Long> especificacionIds;
}