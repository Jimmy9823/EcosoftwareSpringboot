package com.EcoSoftware.Scrum6.Controller;

import com.EcoSoftware.Scrum6.DTO.SolicitudRecoleccionDTO;
import com.EcoSoftware.Scrum6.Entity.SolicitudRecoleccionEntity;
import com.EcoSoftware.Scrum6.Service.SolicitudRecoleccionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/solicitudes")
@RequiredArgsConstructor
public class SolicitudRecoleccionController {

    private final SolicitudRecoleccionService solicitudService;

    @PostMapping
    public ResponseEntity<SolicitudRecoleccionDTO> crear(@RequestBody SolicitudRecoleccionEntity solicitud) {
        SolicitudRecoleccionEntity saved = solicitudService.crearSolicitud(solicitud);
        return ResponseEntity.ok(toDto(saved));
    }


    @GetMapping("/{id}")
    public ResponseEntity<SolicitudRecoleccionDTO> obtenerPorId(@PathVariable Long id) {
        return solicitudService.obtenerPorId(id)
                .map(s -> ResponseEntity.ok(toDto(s)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<SolicitudRecoleccionDTO>> listarTodas() {
        List<SolicitudRecoleccionDTO> dtos = solicitudService.listarTodas()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<SolicitudRecoleccionDTO>> listarPorEstado(@PathVariable String estado) {
        List<SolicitudRecoleccionDTO> dtos = solicitudService.listarPorEstado(estado)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @PostMapping("/{id}/aceptar/{recolectorId}")
    public ResponseEntity<SolicitudRecoleccionDTO> aceptar(
            @PathVariable Long id,
            @PathVariable Long recolectorId
    ) {
        SolicitudRecoleccionEntity updated = solicitudService.aceptarSolicitud(id, recolectorId);
        return ResponseEntity.ok(toDto(updated));
    }

    @PostMapping("/{id}/rechazar")
    public ResponseEntity<SolicitudRecoleccionDTO> rechazar(
            @PathVariable Long id,
            @RequestParam String motivo
    ) {
        SolicitudRecoleccionEntity updated = solicitudService.rechazarSolicitud(id, motivo);
        return ResponseEntity.ok(toDto(updated));
    }

    // Mapper de entidad → DTO
    private SolicitudRecoleccionDTO toDto(SolicitudRecoleccionEntity s) {
        SolicitudRecoleccionDTO dto = new SolicitudRecoleccionDTO();
        dto.setIdSolicitud(s.getIdSolicitud());
        dto.setUsuarioId(s.getUsuario() != null ? s.getUsuario().getIdUsuario() : null);
        dto.setAceptadaPorId(s.getAceptadaPor() != null ? s.getAceptadaPor().getIdUsuario() : null);
        dto.setTipoResiduo(s.getTipoResiduo());
        dto.setCantidad(s.getCantidad());
        dto.setEstadoPeticion(s.getEstadoPeticion());
        dto.setDescripcion(s.getDescripcion());
        dto.setLocalidad(s.getLocalidad());
        dto.setUbicacion(s.getUbicacion());
        dto.setEvidencia(s.getEvidencia());
        dto.setFechaCreacionSolicitud(s.getFechaCreacionSolicitud());
        dto.setFechaProgramada(s.getFechaProgramada());
        dto.setRecoleccionId(s.getRecoleccion() != null ? s.getRecoleccion().getIdRecoleccion() : null);
        return dto;
    }
}
