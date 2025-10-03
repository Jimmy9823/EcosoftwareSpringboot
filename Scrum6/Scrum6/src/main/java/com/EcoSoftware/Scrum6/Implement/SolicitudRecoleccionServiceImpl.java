package com.EcoSoftware.Scrum6.Implement;

import com.EcoSoftware.Scrum6.DTO.SolicitudRecoleccionDTO;
import com.EcoSoftware.Scrum6.Entity.RecoleccionEntity;
import com.EcoSoftware.Scrum6.Entity.SolicitudRecoleccionEntity;
import com.EcoSoftware.Scrum6.Entity.UsuarioEntity;
import com.EcoSoftware.Scrum6.Repository.SolicitudRecoleccionRepository;
import com.EcoSoftware.Scrum6.Repository.UsuarioRepository;
import com.EcoSoftware.Scrum6.Service.SolicitudRecoleccionService;
import com.EcoSoftware.Scrum6.Enums.EstadoPeticion;
import com.EcoSoftware.Scrum6.Enums.EstadoRecoleccion;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class SolicitudRecoleccionServiceImpl implements SolicitudRecoleccionService {

    private final SolicitudRecoleccionRepository solicitudRepository;
    private final UsuarioRepository usuarioRepository;

    public SolicitudRecoleccionServiceImpl(SolicitudRecoleccionRepository solicitudRepository,
            UsuarioRepository usuarioRepository) {
        this.solicitudRepository = solicitudRepository;
        this.usuarioRepository = usuarioRepository;
    }

    // --- Conversión Entity ↔ DTO ---
    private SolicitudRecoleccionDTO entityToDTO(SolicitudRecoleccionEntity entity) {
        SolicitudRecoleccionDTO dto = new SolicitudRecoleccionDTO();
        dto.setIdSolicitud(entity.getIdSolicitud());
        dto.setUsuarioId(entity.getUsuario().getIdUsuario());
        dto.setAceptadaPorId(entity.getAceptadaPor() != null ? entity.getAceptadaPor().getIdUsuario() : null);
        dto.setTipoResiduo(entity.getTipoResiduo());
        dto.setCantidad(entity.getCantidad());
        dto.setEstadoPeticion(entity.getEstadoPeticion());
        dto.setDescripcion(entity.getDescripcion());
        dto.setLocalidad(entity.getLocalidad());
        dto.setUbicacion(entity.getUbicacion());
        dto.setEvidencia(entity.getEvidencia());
        dto.setFechaCreacionSolicitud(entity.getFechaCreacionSolicitud());
        dto.setFechaProgramada(entity.getFechaProgramada());
        dto.setRecoleccionId(entity.getRecoleccion() != null ? entity.getRecoleccion().getIdRecoleccion() : null);
        return dto;
    }

    private SolicitudRecoleccionEntity dtoToEntity(SolicitudRecoleccionDTO dto) {
        SolicitudRecoleccionEntity entity = new SolicitudRecoleccionEntity();

        UsuarioEntity usuario = usuarioRepository.findById(dto.getUsuarioId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        entity.setUsuario(usuario);

        if (dto.getAceptadaPorId() != null) {
            UsuarioEntity aceptadaPor = usuarioRepository.findById(dto.getAceptadaPorId())
                    .orElseThrow(() -> new RuntimeException("Usuario aceptador no encontrado"));
            entity.setAceptadaPor(aceptadaPor);
        }

        entity.setTipoResiduo(dto.getTipoResiduo());
        entity.setCantidad(dto.getCantidad());
        entity.setDescripcion(dto.getDescripcion());
        entity.setLocalidad(dto.getLocalidad());
        entity.setUbicacion(dto.getUbicacion());
        entity.setEvidencia(dto.getEvidencia());
        entity.setFechaProgramada(dto.getFechaProgramada());


        return entity;
    }

    // --- Métodos de servicio ---
    @Override
    public SolicitudRecoleccionDTO crearSolicitud(SolicitudRecoleccionDTO solicitudDTO) {
        SolicitudRecoleccionEntity entity = dtoToEntity(solicitudDTO);
        entity.setEstadoPeticion(EstadoPeticion.Pendiente); // siempre inicia pendiente
        SolicitudRecoleccionEntity saved = solicitudRepository.save(entity);
        return entityToDTO(saved);
    }

    @Override
    public SolicitudRecoleccionDTO obtenerPorId(Long id) {
        SolicitudRecoleccionEntity entity = solicitudRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));
        return entityToDTO(entity);
    }

    @Override
    public List<SolicitudRecoleccionDTO> listarTodas() {
        return solicitudRepository.findAll().stream()
                .map(this::entityToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<SolicitudRecoleccionDTO> listarPorEstado(EstadoPeticion estado) {
        return solicitudRepository.findByEstadoPeticion(estado).stream()
                .map(this::entityToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public SolicitudRecoleccionDTO aceptarSolicitud(Long solicitudId, Long recolectorId) {
        SolicitudRecoleccionEntity solicitud = solicitudRepository.findById(solicitudId)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));

        if (solicitud.getEstadoPeticion() != EstadoPeticion.Pendiente) {
            throw new RuntimeException("Solo se pueden aceptar solicitudes pendientes");
        }

        UsuarioEntity recolector = usuarioRepository.findById(recolectorId)
                .orElseThrow(() -> new RuntimeException("Recolector no encontrado"));

        // Actualizar la solicitud
        solicitud.setAceptadaPor(recolector);
        solicitud.setEstadoPeticion(EstadoPeticion.Aceptada);

        // --- Crear la recolección vinculada ---
        RecoleccionEntity recoleccion = new RecoleccionEntity();
        recoleccion.setSolicitud(solicitud);
        recoleccion.setRecolector(recolector);
        recoleccion.setEstado(EstadoRecoleccion.En_Progreso);
        recoleccion.setFechaRecoleccion(solicitud.getFechaProgramada());
        recoleccion.setEvidencia(solicitud.getEvidencia());
        recoleccion.setObservaciones("Recolección iniciada");

        // Vincular con la solicitud
        solicitud.setRecoleccion(recoleccion);

        // Guardar la solicitud
        SolicitudRecoleccionEntity saved = solicitudRepository.save(solicitud);

        return entityToDTO(saved);
    }

    @Override
    public SolicitudRecoleccionDTO rechazarSolicitud(Long solicitudId, String motivo) {
        SolicitudRecoleccionEntity solicitud = solicitudRepository.findById(solicitudId)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));

        if (solicitud.getEstadoPeticion() != EstadoPeticion.Pendiente) {
            throw new RuntimeException("Solo se pueden rechazar solicitudes pendientes");
        }

        solicitud.setEstadoPeticion(EstadoPeticion.Rechazada);
        return entityToDTO(solicitudRepository.save(solicitud));
    }

    @Override
    public SolicitudRecoleccionDTO actualizarSolicitud(SolicitudRecoleccionDTO dto) {
        SolicitudRecoleccionEntity solicitud = solicitudRepository.findById(dto.getIdSolicitud())
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));

        if (solicitud.getEstadoPeticion() != EstadoPeticion.Pendiente) {
            throw new RuntimeException("Solo se pueden actualizar solicitudes pendientes");
        }

        // Actualizamos los campos permitidos
        solicitud.setTipoResiduo(dto.getTipoResiduo());
        solicitud.setCantidad(dto.getCantidad());
        solicitud.setDescripcion(dto.getDescripcion());
        solicitud.setLocalidad(dto.getLocalidad());
        solicitud.setUbicacion(dto.getUbicacion());
        solicitud.setEvidencia(dto.getEvidencia());
        solicitud.setFechaProgramada(dto.getFechaProgramada());

        return entityToDTO(solicitudRepository.save(solicitud));
    }
}
