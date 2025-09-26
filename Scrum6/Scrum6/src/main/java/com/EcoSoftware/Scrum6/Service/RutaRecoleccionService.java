package com.EcoSoftware.Scrum6.Service;

import com.EcoSoftware.Scrum6.Entity.RutaRecoleccionEntity;

import java.util.List;
import java.util.Optional;

public interface RutaRecoleccionService {

    // Crear una nueva ruta
    RutaRecoleccionEntity crearRuta(RutaRecoleccionEntity ruta);

    // Buscar una ruta por su ID
    Optional<RutaRecoleccionEntity> obtenerPorId(Long id);

    // Listar todas las rutas
    List<RutaRecoleccionEntity> listarTodas();

    // Listar rutas de un recolector específico
    List<RutaRecoleccionEntity> listarPorRecolector(Long recolectorId);

    // Actualizar información de una ruta
    RutaRecoleccionEntity actualizarRuta(Long id, RutaRecoleccionEntity rutaActualizada);

    // Eliminar una ruta físicamente (opcional, si no la quieres lógica)
    void eliminarRuta(Long id);
}

