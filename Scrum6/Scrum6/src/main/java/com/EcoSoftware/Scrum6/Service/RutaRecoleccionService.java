package com.EcoSoftware.Scrum6.Service;

import com.EcoSoftware.Scrum6.DTO.RutaRecoleccionDTO;
import java.util.List;
import java.util.Optional;

public interface RutaRecoleccionService {

    // Crear una nueva ruta
    RutaRecoleccionDTO crearRuta(RutaRecoleccionDTO rutaDTO);

    // Buscar una ruta por su ID
    Optional<RutaRecoleccionDTO> obtenerPorId(Long id);

    // Listar todas las rutas
    List<RutaRecoleccionDTO> listarTodas();

    // Listar rutas de un recolector específico
    List<RutaRecoleccionDTO> listarPorRecolector(Long recolectorId);

    // Asignar recolecciones a una ruta
    RutaRecoleccionDTO asignarRecolecciones(Long rutaId, List<Long> recoleccionIds);

    // Establecer punto de inicio (orden = 1)
    RutaRecoleccionDTO establecerPuntoInicio(Long rutaId, Long idRecoleccionInicio);

    // Actualizar estado en ruta
    RutaRecoleccionDTO actualizarEstadoEnRuta(Long rutaId, Long recoleccionId, String nuevoEstado);

    // Actualizar ruta
    RutaRecoleccionDTO actualizarRuta(Long id, RutaRecoleccionDTO rutaActualizada);

    // Eliminar
    void eliminarRuta(Long id);
}
