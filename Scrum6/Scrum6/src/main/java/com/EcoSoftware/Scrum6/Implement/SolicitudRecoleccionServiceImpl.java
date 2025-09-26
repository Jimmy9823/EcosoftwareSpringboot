package com.EcoSoftware.Scrum6.Implement;

import com.EcoSoftware.Scrum6.Entity.RecoleccionEntity;
import com.EcoSoftware.Scrum6.Entity.SolicitudRecoleccionEntity;
import com.EcoSoftware.Scrum6.Entity.UsuarioEntity;
import com.EcoSoftware.Scrum6.Enums.EstadoPeticion;
import com.EcoSoftware.Scrum6.Enums.EstadoRecoleccion;
import com.EcoSoftware.Scrum6.Repository.RecoleccionRepository;
import com.EcoSoftware.Scrum6.Repository.SolicitudRecoleccionRepository;
import com.EcoSoftware.Scrum6.Repository.UsuarioRepository;
import com.EcoSoftware.Scrum6.Service.SolicitudRecoleccionService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SolicitudRecoleccionServiceImpl implements SolicitudRecoleccionService {

    // Los repositorios deben estar aquí
    private final SolicitudRecoleccionRepository solicitudRepository;
    private final RecoleccionRepository recoleccionRepository;
    private final UsuarioRepository usuarioRepository;

    @Override
    public SolicitudRecoleccionEntity crearSolicitud(SolicitudRecoleccionEntity solicitud) {
        solicitud.setEstadoPeticion(EstadoPeticion.Pendiente);
        return solicitudRepository.save(solicitud);
    }

    @Override
    public Optional<SolicitudRecoleccionEntity> obtenerPorId(Long id) {
        return solicitudRepository.findById(id);
    }

    @Override
    public List<SolicitudRecoleccionEntity> listarTodas() {
        return solicitudRepository.findAll();
    }

    @Override
    public List<SolicitudRecoleccionEntity> listarPorEstado(String estado) {
        EstadoPeticion estadoEnum = EstadoPeticion.valueOf(estado);
        return solicitudRepository.findByEstadoPeticion(estadoEnum);
    }

    @Override
    @Transactional
    public SolicitudRecoleccionEntity aceptarSolicitud(Long solicitudId, Long recolectorId) {
        // Buscar solicitud
        SolicitudRecoleccionEntity solicitud = solicitudRepository.findById(solicitudId)
                .orElseThrow(() -> new EntityNotFoundException("Solicitud no encontrada"));

        if (!solicitud.getEstadoPeticion().equals(EstadoPeticion.Pendiente)) {
            throw new IllegalStateException("La solicitud ya fue procesada");
        }

        // Buscar recolector/empresa que acepta
        UsuarioEntity recolector = usuarioRepository.findById(recolectorId)
                .orElseThrow(() -> new EntityNotFoundException("Recolector no encontrado"));

        // Cambiar estado de la solicitud y asignar recolector
        solicitud.setEstadoPeticion(EstadoPeticion.Aceptada);
        solicitud.setAceptadaPor(recolector);

        // Crear recolección asociada
        RecoleccionEntity recoleccion = new RecoleccionEntity();
        recoleccion.setSolicitud(solicitud);
        recoleccion.setRecolector(recolector);
        recoleccion.setEstado(EstadoRecoleccion.Pendiente);
        recoleccion.setFechaRecoleccion(solicitud.getFechaProgramada()); // 🔹 copiar fecha programada

        recoleccionRepository.save(recoleccion);

        // Asociar la recolección con la solicitud
        solicitud.setRecoleccion(recoleccion);

        return solicitudRepository.save(solicitud);
    }

    @Override
    public SolicitudRecoleccionEntity rechazarSolicitud(Long solicitudId, String motivo) {
        SolicitudRecoleccionEntity solicitud = solicitudRepository.findById(solicitudId)
                .orElseThrow(() -> new EntityNotFoundException("Solicitud no encontrada"));

        if (!solicitud.getEstadoPeticion().equals(EstadoPeticion.Pendiente)) {
            throw new IllegalStateException("La solicitud ya fue procesada");
        }

        solicitud.setEstadoPeticion(EstadoPeticion.Rechazada);
        // ⚡ Si quieres guardar el motivo, añade un campo en la entidad SolicitudRecoleccionEntity
        return solicitudRepository.save(solicitud);
    }
}
