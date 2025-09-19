package com.EcoSoftware.Scrum6.Implement;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.EcoSoftware.Scrum6.DTO.SolicitudRecoleccionDTO;
import com.EcoSoftware.Scrum6.Entity.SolicitudRecoleccionEntity;
import com.EcoSoftware.Scrum6.Entity.SolicitudRecoleccionEntity.EstadoPeticion;
import com.EcoSoftware.Scrum6.Entity.SolicitudRecoleccionEntity.TipoResiduo;
import com.EcoSoftware.Scrum6.Repository.SolicitudRecoleccionRepository;
import com.EcoSoftware.Scrum6.Service.SolicitudRecoleccionService;

@Service
public class SolicitudRecoleccionServiceImpl implements SolicitudRecoleccionService {

    @Autowired
    private SolicitudRecoleccionRepository solicitudRecoleccionRepository;

    /* Métodos del Servicio */

    // Crear una nueva solicitud de recolección
    @Override
    public SolicitudRecoleccionDTO crearSolicitud(SolicitudRecoleccionDTO solicitudRecoleccionDTO) {
        SolicitudRecoleccionEntity solicitudRecoleccionEntity = convertirDTOAEntidad(solicitudRecoleccionDTO);
        SolicitudRecoleccionEntity nuevaSolicitud = solicitudRecoleccionRepository.save(solicitudRecoleccionEntity);
        return convertirEntidadADTO(nuevaSolicitud);
    }

    @Override
    public SolicitudRecoleccionDTO obtenerPorId(Long id) {
        SolicitudRecoleccionEntity solicitudRecoleccionEntity = solicitudRecoleccionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));
        return convertirEntidadADTO(solicitudRecoleccionEntity);
    }

    @Override
    public List<SolicitudRecoleccionDTO> listarTodas() {
        List<SolicitudRecoleccionEntity> solicitudes = solicitudRecoleccionRepository.findAll();
        return solicitudes.stream()
                .map(this::convertirEntidadADTO)
                .collect(Collectors.toList());
    }

    @Override
    public SolicitudRecoleccionDTO actualizarSolicitud(Long id, SolicitudRecoleccionDTO solicitudRecoleccionDTO) {
        SolicitudRecoleccionEntity solicitudRecoleccionEntity = solicitudRecoleccionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));
        // Actualizar los campos de la entidad con los valores del DTO
        solicitudRecoleccionEntity.setTipoResiduo(solicitudRecoleccionDTO.getTipoResiduo());
        solicitudRecoleccionEntity.setCantidad(solicitudRecoleccionDTO.getCantidad());
        solicitudRecoleccionEntity.setEstadoPeticion(solicitudRecoleccionDTO.getEstadoPeticion());
        solicitudRecoleccionEntity.setDescripcion(solicitudRecoleccionDTO.getDescripcion());
        solicitudRecoleccionEntity.setUbicacion(solicitudRecoleccionDTO.getUbicacion());
        solicitudRecoleccionEntity.setEvidencia(solicitudRecoleccionDTO.getEvidencia());
        solicitudRecoleccionEntity.setFechaProgramada(solicitudRecoleccionDTO.getFechaProgramada());
        // Guardar los cambios en la base de datos
        SolicitudRecoleccionEntity solicitudActualizada = solicitudRecoleccionRepository.save(solicitudRecoleccionEntity);
        return convertirEntidadADTO(solicitudActualizada);
    }

    @Override
    public void eliminarSolicitud(Long id) {
        SolicitudRecoleccionEntity solicitudRecoleccionEntity = solicitudRecoleccionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));
        solicitudRecoleccionRepository.delete(solicitudRecoleccionEntity);        
    }

    @Override
    public List<SolicitudRecoleccionDTO> listarPorUsuario(Long usuarioId) {
        List<SolicitudRecoleccionEntity> solicitudes = solicitudRecoleccionRepository.findByUsuarioId(usuarioId);
        return solicitudes.stream()
                .map(this::convertirEntidadADTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<SolicitudRecoleccionDTO> listarPorEstado(EstadoPeticion estado) {
        List<SolicitudRecoleccionEntity> solicitudes = solicitudRecoleccionRepository.findByEstadoPeticion(estado);
        return solicitudes.stream()
                .map(this::convertirEntidadADTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<SolicitudRecoleccionDTO> listarPorFecha(OffsetDateTime fechaProgramada) {
        List<SolicitudRecoleccionEntity> solicitudes = solicitudRecoleccionRepository.findByFechaProgramada(fechaProgramada);
        return solicitudes.stream()
                .map(this::convertirEntidadADTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<SolicitudRecoleccionDTO> listarPorTipoResiduo(TipoResiduo tipoResiduo) {
        List<SolicitudRecoleccionEntity> solicitudes = solicitudRecoleccionRepository.findByTipoResiduo(tipoResiduo);
        return solicitudes.stream()
                .map(this::convertirEntidadADTO)
                .collect(Collectors.toList());
    }


    /*Metodo de Mapeo  */

    // Convertir de Entidad a DTO
    private SolicitudRecoleccionDTO convertirEntidadADTO(SolicitudRecoleccionEntity solicitudRecoleccionEntity) {
        SolicitudRecoleccionDTO solicitudRecoleccionDTO = new SolicitudRecoleccionDTO();
        solicitudRecoleccionDTO.setId(solicitudRecoleccionEntity.getId());
        solicitudRecoleccionDTO.setTipoResiduo(solicitudRecoleccionEntity.getTipoResiduo());
        solicitudRecoleccionDTO.setCantidad(solicitudRecoleccionEntity.getCantidad());
        solicitudRecoleccionDTO.setEstadoPeticion(solicitudRecoleccionEntity.getEstadoPeticion());
        solicitudRecoleccionDTO.setDescripcion(solicitudRecoleccionEntity.getDescripcion());
        solicitudRecoleccionDTO.setUbicacion(solicitudRecoleccionEntity.getUbicacion());
        solicitudRecoleccionDTO.setEvidencia(solicitudRecoleccionEntity.getEvidencia());
        solicitudRecoleccionDTO.setFechaCreacion(solicitudRecoleccionEntity.getFechaCreacion());
        solicitudRecoleccionDTO.setFechaProgramada(solicitudRecoleccionEntity.getFechaProgramada());
        solicitudRecoleccionDTO.setUsuarioId(solicitudRecoleccionEntity.getUsuario().getIdUsuario());

        // Mapear otros campos según sea necesario
        return solicitudRecoleccionDTO;
    }

    // Convertir de DTO a Entidad
    private SolicitudRecoleccionEntity convertirDTOAEntidad(SolicitudRecoleccionDTO solicitudRecoleccionDTO) {
        SolicitudRecoleccionEntity solicitudRecoleccionEntity = new SolicitudRecoleccionEntity();
        solicitudRecoleccionEntity.setId(solicitudRecoleccionDTO.getId());
        solicitudRecoleccionEntity.setTipoResiduo(solicitudRecoleccionDTO.getTipoResiduo());
        solicitudRecoleccionEntity.setCantidad(solicitudRecoleccionDTO.getCantidad());
        solicitudRecoleccionEntity.setEstadoPeticion(solicitudRecoleccionDTO.getEstadoPeticion());
        solicitudRecoleccionEntity.setDescripcion(solicitudRecoleccionDTO.getDescripcion());
        solicitudRecoleccionEntity.setUbicacion(solicitudRecoleccionDTO.getUbicacion());
        solicitudRecoleccionEntity.setEvidencia(solicitudRecoleccionDTO.getEvidencia());
        solicitudRecoleccionEntity.setFechaCreacion(solicitudRecoleccionDTO.getFechaCreacion());
        solicitudRecoleccionEntity.setFechaProgramada(solicitudRecoleccionDTO.getFechaProgramada());
        // La asignación del usuario debe manejarse en el servicio, ya que requiere cargar la entidad Usuario
        // Mapear otros campos según sea necesario
        return solicitudRecoleccionEntity;
    }



}
