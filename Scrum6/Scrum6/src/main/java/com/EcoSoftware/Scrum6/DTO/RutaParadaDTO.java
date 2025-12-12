package com.EcoSoftware.Scrum6.DTO;

import com.EcoSoftware.Scrum6.Enums.EstadoParada;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
public class RutaParadaDTO {
    private Long idParada;
    private Long rutaId;
    private Long recoleccionId;
    private Integer orden;
    private EstadoParada estado;
    private OffsetDateTime fechaAtencion;
    private Double latitude;
    private Double longitude;
}
