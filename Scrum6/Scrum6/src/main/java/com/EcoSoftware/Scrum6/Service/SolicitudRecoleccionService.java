package com.EcoSoftware.Scrum6.Service;

import java.time.OffsetDateTime;
import java.util.List;

import com.EcoSoftware.Scrum6.DTO.SolicitudRecoleccionDTO;
import com.EcoSoftware.Scrum6.Entity.SolicitudRecoleccionEntity;

public interface SolicitudRecoleccionService {

    /*CRUD de Solicitud Recolección */

    //Crear Solicitud de Recolección
    SolicitudRecoleccionDTO crearSolicitud(SolicitudRecoleccionDTO solicitudRecoleccionDTO);

    //Buscar Solicitud de Recolección por ID
    SolicitudRecoleccionDTO obtenerPorId(Long id);

    //Listar todas las Solicitudes de Recolección
    List<SolicitudRecoleccionDTO> listarTodas();

    //Actualizar Solicitud de Recolección
    SolicitudRecoleccionDTO actualizarSolicitud(Long id, SolicitudRecoleccionDTO solicitudRecoleccionDTO);

    //Eliminar Solicitud de Recolección
    void eliminarSolicitud(Long id);
    

    /* Tipos de listar para Solicitudes de Recolección */

    //Listar Solicitudes de Recolección por usuario(Ciudadano)
    List<SolicitudRecoleccionDTO> listarPorUsuario(Long usuarioId);

    //Listar Solicitudes de Recolección por estado
    List<SolicitudRecoleccionDTO> listarPorEstado(SolicitudRecoleccionEntity.EstadoPeticion estado);

    //Listar Solicitudes de Recolección por fecha
    List<SolicitudRecoleccionDTO> listarPorFecha(OffsetDateTime fechaProgramada);

    //Listar Solicitudes de Recolección por tipo de residuo
    List<SolicitudRecoleccionDTO> listarPorTipoResiduo(SolicitudRecoleccionEntity.TipoResiduo tipoResiduo);

} 
