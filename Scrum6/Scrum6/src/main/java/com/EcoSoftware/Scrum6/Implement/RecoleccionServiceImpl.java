package com.EcoSoftware.Scrum6.Implement;

import com.EcoSoftware.Scrum6.DTO.RecoleccionDTO;
import com.EcoSoftware.Scrum6.Entity.RecoleccionEntity;
import com.EcoSoftware.Scrum6.Enums.EstadoRecoleccion;
import com.EcoSoftware.Scrum6.Repository.RecoleccionRepository;
import com.EcoSoftware.Scrum6.Service.RecoleccionService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.EcoSoftware.Scrum6.Exception.RecoleccionCanceladaException;

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
    // Devuelve una recolección activa por su ID, lanza excepción si está cancelada
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
    // LISTAR TODAS LAS RECOLECCIONES ACTIVAS
    // ========================================================
    // Devuelve todas las recolecciones que no estén canceladas
    @Override
    public List<RecoleccionEntity> listarActivas() {
        return recoleccionRepository.findByEstadoNot(EstadoRecoleccion.Cancelada);
    }

    // ========================================================
    // LISTAR RECOLECCIONES POR RECOLECTOR
    // ========================================================
    // Lista recolecciones activas asignadas a un recolector específico
    @Override
    public List<RecoleccionEntity> listarPorRecolector(Long recolectorId) {
        return recoleccionRepository.findByRecolector_IdUsuarioAndEstadoNot(recolectorId, EstadoRecoleccion.Cancelada);
    }

    // ========================================================
    // LISTAR RECOLECCIONES POR RUTA
    // ========================================================
    // Lista recolecciones activas asociadas a una ruta determinada
    @Override
    public List<RecoleccionEntity> listarPorRuta(Long rutaId) {
        return recoleccionRepository.findByRuta_IdRutaAndEstadoNot(rutaId, EstadoRecoleccion.Cancelada);
    }

    // ========================================================
    // ACTUALIZAR ESTADO DE UNA RECOLECCIÓN
    // ========================================================
    // Cambia el estado de una recolección a un nuevo estado
    @Override
    @Transactional
    public RecoleccionEntity actualizarEstado(Long recoleccionId, EstadoRecoleccion nuevoEstado) {
        RecoleccionEntity recoleccion = recoleccionRepository.findById(recoleccionId)
                .orElseThrow(() -> new EntityNotFoundException("Recolección no encontrada con id: " + recoleccionId));

        recoleccion.setEstado(nuevoEstado);
        return recoleccionRepository.save(recoleccion);
    }

    // ========================================================
    // ACTUALIZAR DATOS DE UNA RECOLECCIÓN
    // ========================================================
    // Actualiza campos que el recolector puede modificar
    @Override
    @Transactional
    public RecoleccionEntity actualizarRecoleccion(Long id, RecoleccionDTO dto) {
        RecoleccionEntity recoleccion = recoleccionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Recolección no encontrada"));

        if (dto.getObservaciones() != null) {
            recoleccion.setObservaciones(dto.getObservaciones());
        }
        if (dto.getEvidencia() != null) {
            recoleccion.setEvidencia(dto.getEvidencia());
        }
        if (dto.getFechaRecoleccion() != null) {
            recoleccion.setFechaRecoleccion(dto.getFechaRecoleccion());
        }
        if (dto.getEstado() != null) {
            recoleccion.setEstado(dto.getEstado());
        }

        return recoleccionRepository.save(recoleccion);
    }

    // ========================================================
    // ELIMINAR RECOLECCIÓN (LÓGICAMENTE)
    // ========================================================
    // Marca una recolección como cancelada sin eliminarla físicamente
    @Override
    @Transactional
    public void eliminarLogicamente(Long recoleccionId) {
        RecoleccionEntity recoleccion = recoleccionRepository.findById(recoleccionId)
                .orElseThrow(() -> new EntityNotFoundException("Recolección no encontrada con id: " + recoleccionId));

        recoleccion.setEstado(EstadoRecoleccion.Cancelada);
        recoleccionRepository.save(recoleccion);
    }
}
