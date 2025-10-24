package com.EcoSoftware.Scrum6.Service;

import com.EcoSoftware.Scrum6.DTO.CapacitacionesDTO.*;
import com.EcoSoftware.Scrum6.Enums.EstadoCurso;

import java.util.List;

/**
 * Interfaz de servicios para el módulo de capacitaciones
 */
public interface CapacitacionesService {

    // ===========================
    // Capacitacion
    // ===========================
    CapacitacionDTO crearCapacitacion(CapacitacionDTO dto);

    CapacitacionDTO actualizarCapacitacion(Long id, CapacitacionDTO dto);

    void eliminarCapacitacion(Long id);

    CapacitacionDTO obtenerCapacitacionPorId(Long id);

    List<CapacitacionDTO> listarTodasCapacitaciones();

    // ===========================
    // Modulo
    // ===========================
    ModuloDTO crearModulo(ModuloDTO dto);

    ModuloDTO actualizarModulo(Long id, ModuloDTO dto);

    void eliminarModulo(Long id);

    List<ModuloDTO> listarModulosPorCapacitacion(Long capacitacionId);

    // ===========================
    // Inscripcion
    // ===========================
    InscripcionDTO inscribirse(Long usuarioId, Long cursoId);

    InscripcionDTO actualizarEstadoInscripcion(Long id, EstadoCurso estadoCurso);

    List<InscripcionDTO> listarInscripcionesPorUsuario(Long usuarioId);

    List<InscripcionDTO> listarInscripcionesPorCurso(Long cursoId);

    // ===========================
    // Progreso
    // ===========================
    ProgresoDTO registrarProgreso(ProgresoDTO dto);

    ProgresoDTO actualizarProgreso(Long id, ProgresoDTO dto);

    List<ProgresoDTO> listarProgresosPorUsuario(Long usuarioId);

    List<ProgresoDTO> listarProgresosPorCurso(Long cursoId);
}
