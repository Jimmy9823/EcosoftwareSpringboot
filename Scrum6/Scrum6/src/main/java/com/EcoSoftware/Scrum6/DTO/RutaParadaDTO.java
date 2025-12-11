package com.EcoSoftware.Scrum6.DTO;


import java.time.LocalDateTime;

import com.EcoSoftware.Scrum6.Enums.EstadoParada;

import lombok.Data;

@Data
public class RutaParadaDTO {
    private Long idParada;
    private Long rutaId;
    private Long recoleccionId;

    private Integer orden;

    private Double latitude;
    private Double longitude;

    private EstadoParada estado;
    private LocalDateTime fechaAtencion;
}
