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
    public Optional<RecoleccionEntity> obtenerPorId(Long id) {
        return recoleccionRepository.findById(id)
                .map(r -> {
                    if (r.getEstado() == EstadoRecoleccion.Cancelada) {
                        throw new RecoleccionCanceladaException("Recolección cancelada");
                    }
                    return r;
                });
    }



    @Override
    public List<RecoleccionEntity> listarActivas() {
        // Usa el repo que filtra por estado != Cancelada
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
    @Transactional
    public RecoleccionEntity actualizarEstado(Long recoleccionId, EstadoRecoleccion nuevoEstado) {
        RecoleccionEntity recoleccion = recoleccionRepository.findById(recoleccionId)
                .orElseThrow(() -> new EntityNotFoundException("Recolección no encontrada con id: " + recoleccionId));

        recoleccion.setEstado(nuevoEstado);
        return recoleccionRepository.save(recoleccion);
    }

    @Override
    @Transactional
    public RecoleccionEntity actualizarRecoleccion(Long id, RecoleccionDTO dto) {
        RecoleccionEntity recoleccion = recoleccionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Recolección no encontrada"));

        // Solo actualizamos lo que el recolector puede modificar
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
            recoleccion.setEstado(dto.getEstado()); // Pendiente, Completada, etc.
        }

        return recoleccionRepository.save(recoleccion);
    }


    @Override
    @Transactional
    public void eliminarLogicamente(Long recoleccionId) {
        RecoleccionEntity recoleccion = recoleccionRepository.findById(recoleccionId)
                .orElseThrow(() -> new EntityNotFoundException("Recolección no encontrada con id: " + recoleccionId));

        recoleccion.setEstado(EstadoRecoleccion.Cancelada);
        recoleccionRepository.save(recoleccion);
    }
}

