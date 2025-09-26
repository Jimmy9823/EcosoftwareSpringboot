package com.EcoSoftware.Scrum6.DTO;

import com.EcoSoftware.Scrum6.Enums.EstadoPeticion;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class EstadoDTO {
    @NotNull(message = "El estado es obligatorio")
    private EstadoPeticion estado;
}
