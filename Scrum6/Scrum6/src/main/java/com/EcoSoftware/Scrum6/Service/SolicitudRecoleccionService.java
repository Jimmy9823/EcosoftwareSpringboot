package com.EcoSoftware.Scrum6.Service;

import com.EcoSoftware.Scrum6.DTO.SolicitudRecoleccionDTO;
import com.EcoSoftware.Scrum6.Enums.EstadoPeticion;

import java.util.List;

public interface SolicitudRecoleccionService {

    // Crear una nueva solicitud (siempre inicia en estado Pendiente)
    SolicitudRecoleccionDTO crearSolicitud(SolicitudRecoleccionDTO solicitudDTO);

    // Obtener solicitud por ID
    SolicitudRecoleccionDTO obtenerPorId(Long id);

    // Listar todas las solicitudes
    List<SolicitudRecoleccionDTO> listarTodas();

    // Listar solicitudes por estado (Pendiente, Aceptada, Rechazada)
    List<SolicitudRecoleccionDTO> listarPorEstado(EstadoPeticion estado);

    // Aceptar una solicitud (solo si está pendiente) y asignarle un recolector
    SolicitudRecoleccionDTO aceptarSolicitud(Long solicitudId, Long recolectorId);

    // Rechazar una solicitud (solo si está pendiente) y opcionalmente registrar un motivo
    SolicitudRecoleccionDTO rechazarSolicitud(Long solicitudId, String motivo);

    // Actualizar datos de la solicitud (solo si está pendiente)
    SolicitudRecoleccionDTO actualizarSolicitud(SolicitudRecoleccionDTO solicitudDTO);
}


