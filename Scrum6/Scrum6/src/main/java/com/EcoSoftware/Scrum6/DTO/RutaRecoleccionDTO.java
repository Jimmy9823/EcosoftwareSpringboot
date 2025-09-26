package com.EcoSoftware.Scrum6.DTO;

import lombok.Data;

import java.time.OffsetDateTime;
import java.util.List;

@Data
public class RutaRecoleccionDTO {
    private Long idRuta;
    private Long recolectorId;       // Usuario (empresa/reciclador) dueño de la ruta

    private List<Long> recoleccionesIds; // IDs de las recolecciones asignadas a esta ruta

    private String nombre;
    private String descripcion;
    private String zonasCubiertas;
    private OffsetDateTime fechaCreacion;
}
