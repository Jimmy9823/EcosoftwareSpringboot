package com.EcoSoftware.Scrum6.DTO;

import com.EcoSoftware.Scrum6.Enums.Localidad;
import com.EcoSoftware.Scrum6.Enums.TipoResiduo;
import com.EcoSoftware.Scrum6.Enums.EstadoPeticion;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
public class SolicitudRecoleccionDTO {
    private Long idSolicitud;
    private Long usuarioId;          // Id del ciudadano que crea la solicitud
    private Long aceptadaPorId;      // Id del usuario que acepta la solicitud (empresa/reciclador)

    private TipoResiduo tipoResiduo;
    private String cantidad;
    private EstadoPeticion estadoPeticion;
    private String descripcion;
    private Localidad localidad;
    private String ubicacion;
    private String evidencia;
    private OffsetDateTime fechaCreacionSolicitud;
    private OffsetDateTime fechaProgramada;

    private Long recoleccionId;      // Relación con la recolección generada
}
