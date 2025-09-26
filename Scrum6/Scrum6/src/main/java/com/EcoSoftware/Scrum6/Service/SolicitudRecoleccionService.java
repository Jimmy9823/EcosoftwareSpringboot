package com.EcoSoftware.Scrum6.Service;

import com.EcoSoftware.Scrum6.Entity.SolicitudRecoleccionEntity;
import java.util.List;
import java.util.Optional;

public interface SolicitudRecoleccionService {

    // Crear una nueva solicitud
    SolicitudRecoleccionEntity crearSolicitud(SolicitudRecoleccionEntity solicitud);

    // Buscar solicitud por ID
    Optional<SolicitudRecoleccionEntity> obtenerPorId(Long id);

    // Listar todas las solicitudes
    List<SolicitudRecoleccionEntity> listarTodas();

    // Listar solicitudes por estado (Pendiente, Aceptada, Rechazada)
    List<SolicitudRecoleccionEntity> listarPorEstado(String estado);

    // Aceptar solicitud → genera una Recolección
    SolicitudRecoleccionEntity aceptarSolicitud(Long solicitudId, Long recolectorId);

    // Rechazar solicitud
    SolicitudRecoleccionEntity rechazarSolicitud(Long solicitudId, String motivo);
}

