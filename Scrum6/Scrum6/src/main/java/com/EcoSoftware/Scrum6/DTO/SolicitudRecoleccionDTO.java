package com.EcoSoftware.Scrum6.DTO;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import com.EcoSoftware.Scrum6.Entity.SolicitudRecoleccionEntity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SolicitudRecoleccionDTO {

    private Long id;

    @NotNull(message = "El tipo de residuo es obligatorio")
    private SolicitudRecoleccionEntity.TipoResiduo tipoResiduo;

    @NotNull(message = "La cantidad es obligatoria")
    private BigDecimal cantidad;

    private SolicitudRecoleccionEntity.EstadoPeticion estadoPeticion; 

    @NotBlank(message = "La descripción nos ayuda a entender mejor tu solicitud")
    private String descripcion;

    @NotBlank(message = "La ubicación es obligatoria")
    private String ubicacion;

    @NotBlank(message = "La evidencia es obligatoria")
    private String evidencia;  

    private OffsetDateTime fechaCreacion;   // generado automáticamente

    @NotNull(message = "La fecha programada es obligatoria")
    private OffsetDateTime fechaProgramada;

    private Long usuarioId; 
}

