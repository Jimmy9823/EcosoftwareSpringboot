package com.EcoSoftware.Scrum6.Controller;

import com.EcoSoftware.Scrum6.DTO.RecoleccionDTO;
import com.EcoSoftware.Scrum6.Entity.RecoleccionEntity;
import com.EcoSoftware.Scrum6.Enums.EstadoRecoleccion;
import com.EcoSoftware.Scrum6.Service.RecoleccionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/recolecciones")
@RequiredArgsConstructor
public class RecoleccionController {

    private final RecoleccionService recoleccionService;

    // Obtener una recolección por ID
    @GetMapping("/{id}")
    public ResponseEntity<RecoleccionDTO> obtenerPorId(@PathVariable Long id) {
        return recoleccionService.obtenerPorId(id)
                .map(r -> ResponseEntity.ok(toDto(r)))
                .orElse(ResponseEntity.notFound().build());
    }

    // Listar todas las recolecciones activas (no canceladas)
    @GetMapping("/activas")
    public ResponseEntity<List<RecoleccionDTO>> listarActivas() {
        return ResponseEntity.ok(
                recoleccionService.listarActivas().stream()
                        .map(this::toDto)
                        .collect(Collectors.toList())
        );
    }

    // Listar recolecciones de un recolector
    @GetMapping("/recolector/{id}")
    public ResponseEntity<List<RecoleccionDTO>> listarPorRecolector(@PathVariable Long id) {
        return ResponseEntity.ok(
                recoleccionService.listarPorRecolector(id).stream()
                        .map(this::toDto)
                        .collect(Collectors.toList())
        );
    }

    // Listar recolecciones de una ruta
    @GetMapping("/ruta/{id}")
    public ResponseEntity<List<RecoleccionDTO>> listarPorRuta(@PathVariable Long id) {
        return ResponseEntity.ok(
                recoleccionService.listarPorRuta(id).stream()
                        .map(this::toDto)
                        .collect(Collectors.toList())
        );
    }

    // Actualizar estado de una recolección
    @PutMapping("/{id}/estado")
    public ResponseEntity<RecoleccionDTO> actualizarEstado(
            @PathVariable Long id,
            @RequestParam EstadoRecoleccion estado
    ) {
        RecoleccionEntity actualizadaEstado = recoleccionService.actualizarEstado(id, estado);
        return ResponseEntity.ok(toDto(actualizadaEstado));
    }

    // Actualizar recolección
    @PutMapping("/{id}")
    public ResponseEntity<RecoleccionDTO> actualizarRecoleccion(
            @PathVariable Long id,
            @RequestBody RecoleccionDTO dto
    ) {
        RecoleccionEntity actualizar = recoleccionService.actualizarRecoleccion(id, dto);
        return ResponseEntity.ok(toDto(actualizar));
    }


    // Eliminar lógicamente (cambiar estado a Cancelada)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarLogicamente(@PathVariable Long id) {
        recoleccionService.eliminarLogicamente(id);
        return ResponseEntity.noContent().build();
    }

    // Mapper Entity → DTO
    private RecoleccionDTO toDto(RecoleccionEntity r) {
        RecoleccionDTO dto = new RecoleccionDTO();
        dto.setIdRecoleccion(r.getIdRecoleccion());
        dto.setSolicitudId(r.getSolicitud() != null ? r.getSolicitud().getIdSolicitud() : null);
        dto.setRecolectorId(r.getRecolector() != null ? r.getRecolector().getIdUsuario() : null);
        dto.setRutaId(r.getRuta() != null ? r.getRuta().getIdRuta() : null);
        dto.setEstado(r.getEstado());
        dto.setFechaRecoleccion(r.getFechaRecoleccion());
        dto.setObservaciones(r.getObservaciones());
        dto.setEvidencia(r.getEvidencia());
        dto.setFechaCreacionRecoleccion(r.getFechaCreacionRecoleccion());
        return dto;
    }
}
