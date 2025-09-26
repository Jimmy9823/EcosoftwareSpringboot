package com.EcoSoftware.Scrum6.Service;

import com.EcoSoftware.Scrum6.DTO.RecoleccionDTO;
import com.EcoSoftware.Scrum6.Entity.RecoleccionEntity;
import com.EcoSoftware.Scrum6.Enums.EstadoRecoleccion;

import java.util.List;
import java.util.Optional;

public interface RecoleccionService {

    // Buscar recolección por ID
    Optional<RecoleccionEntity> obtenerPorId(Long id);

    // Listar todas las recolecciones activas
    List<RecoleccionEntity> listarActivas();

    // Listar recolecciones activas de un recolector
    List<RecoleccionEntity> listarPorRecolector(Long recolectorId);

    // Listar recolecciones activas de una ruta
    List<RecoleccionEntity> listarPorRuta(Long rutaId);

    // Actualizar estado de recolección (ej: Pendiente → Completada → Cancelada)
    RecoleccionEntity actualizarEstado(Long recoleccionId, EstadoRecoleccion nuevoEstado);

    // Actualizar datos de recoleccion
    RecoleccionEntity actualizarRecoleccion(Long id, RecoleccionDTO dto);

    // Eliminar lógicamente una recolección (activo = false)
    void eliminarLogicamente(Long recoleccionId);
}


