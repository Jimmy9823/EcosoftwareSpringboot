package com.EcoSoftware.Scrum6.Implement;

import com.EcoSoftware.Scrum6.DTO.RecoleccionDTO;
import com.EcoSoftware.Scrum6.Entity.RecoleccionEntity;
import com.EcoSoftware.Scrum6.Enums.EstadoRecoleccion;
import com.EcoSoftware.Scrum6.Exception.RecoleccionCanceladaException;
import com.EcoSoftware.Scrum6.Repository.RecoleccionRepository;
import com.EcoSoftware.Scrum6.Service.RecoleccionService;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RecoleccionServiceImpl implements RecoleccionService {

    private final RecoleccionRepository recoleccionRepository;

    @Override
    public List<RecoleccionEntity> listarTodas() {
        return recoleccionRepository.findAll();
    }

    @Override
    public List<RecoleccionEntity> ListarTodasRecolector(Long recolectorId) {
        return recoleccionRepository.findByRecolector_IdUsuario(recolectorId);
    }

    // ========================================================
    // OBTENER RECOLECCIÓN POR ID
    // ========================================================
    @Override
    public Optional<RecoleccionEntity> obtenerPorId(Long id) {
        return recoleccionRepository.findById(id)
                .map(r -> {
                    if (r.getEstado() == EstadoRecoleccion.Cancelada) {
                        throw new RecoleccionCanceladaException("Recolección cancelada");
                    }
                    return r;
                });
    }

    // ========================================================
    // LISTAR ACTIVAS
    // ========================================================
    @Override
    public List<RecoleccionEntity> listarActivas() {
        return recoleccionRepository.findByEstadoNot(EstadoRecoleccion.Cancelada);
    }

    @Override
    public List<RecoleccionEntity> listarPorRecolector(Long recolectorId) {
        return recoleccionRepository.findByRecolector_IdUsuarioAndEstadoNot(recolectorId, EstadoRecoleccion.Cancelada);
    }

    @Override
    public List<RecoleccionEntity> listarPorRuta(Long rutaId) {
        return recoleccionRepository.findByRuta_IdRutaAndEstadoNot(rutaId, EstadoRecoleccion.Cancelada);
    }

    @Override
    public List<RecoleccionEntity> listarSinRutaPorRecolector(Long recolectorId) {
        return recoleccionRepository.findByRecolector_IdUsuarioAndRutaIsNullAndEstadoNot(
                recolectorId, EstadoRecoleccion.Cancelada
        );
    }

    // ========================================================
    // CONTROL ESTRICTO DE CAMBIO DE ESTADO
    // ========================================================
    @Override
@Transactional
public RecoleccionEntity actualizarEstado(Long recoleccionId, EstadoRecoleccion nuevoEstado) {

    RecoleccionEntity r = recoleccionRepository.findById(recoleccionId)
            .orElseThrow(() -> new RuntimeException("Recolección no encontrada"));

    EstadoRecoleccion anterior = r.getEstado();

    if (anterior == EstadoRecoleccion.Cancelada ||
        anterior == EstadoRecoleccion.Fallida ||
        anterior == EstadoRecoleccion.Completada) {
        throw new IllegalStateException("No se puede modificar una recolección finalizada/cancelada/fallida");
    }

    switch (anterior) {
        case Pendiente:
            if (!(nuevoEstado == EstadoRecoleccion.En_Progreso ||
                  nuevoEstado == EstadoRecoleccion.Cancelada)) {
                throw new IllegalStateException("Una recolección pendiente solo puede pasar a En_Progreso o Cancelada");
            }
            break;

        case En_Progreso:
            if (!(nuevoEstado == EstadoRecoleccion.Completada ||
                  nuevoEstado == EstadoRecoleccion.Fallida)) {
                throw new IllegalStateException("Una recolección en progreso solo puede ser Completada o Fallida");
            }
            break;

        default:
            throw new IllegalStateException("Transición no permitida");
    }

    r.setEstado(nuevoEstado);

    // Si se marca como completada, fijar fecha de recolección si no existe
    if (nuevoEstado == EstadoRecoleccion.Completada && r.getFechaRecoleccion() == null) {
        r.setFechaRecoleccion(java.time.LocalDateTime.now());
    }

    return recoleccionRepository.save(r);
}

    // ========================================================
    // ACTUALIZAR DATOS (NO PERMITE CAMBIAR ESTADO)
    // ========================================================
    @Override
    @Transactional
    public RecoleccionEntity actualizarRecoleccion(Long id, RecoleccionDTO dto) {
        RecoleccionEntity r = recoleccionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Recolección no encontrada"));

        // No permitir cambios si está finalizada
        if (r.getEstado() == EstadoRecoleccion.Cancelada ||
            r.getEstado() == EstadoRecoleccion.Fallida ||
            r.getEstado() == EstadoRecoleccion.Completada) {
            throw new IllegalStateException("No se pueden modificar datos de una recolección cerrada");
        }

        if (dto.getObservaciones() != null) r.setObservaciones(dto.getObservaciones());
        if (dto.getEvidencia() != null) r.setEvidencia(dto.getEvidencia());
        if (dto.getFechaRecoleccion() != null) r.setFechaRecoleccion(dto.getFechaRecoleccion());

        // Estado solo puede cambiarse por actualizarEstado()
        if (dto.getEstado() != null) {
            throw new IllegalStateException("El estado solo puede cambiarse mediante actualizarEstado()");
        }

        return recoleccionRepository.save(r);
    }

    // ========================================================
    // ELIMINAR LÓGICAMENTE = CANCELAR
    // ========================================================
    @Override
    @Transactional
    public void eliminarLogicamente(Long recoleccionId) {
        RecoleccionEntity r = recoleccionRepository.findById(recoleccionId)
                .orElseThrow(() -> new EntityNotFoundException("Recolección no encontrada"));

        if (r.getRuta() != null) {
            throw new IllegalStateException("No se puede cancelar una recolección que ya está en una ruta");
        }

        r.setEstado(EstadoRecoleccion.Cancelada);
        recoleccionRepository.save(r);
    }
}
