package com.EcoSoftware.Scrum6.Controller;

import com.EcoSoftware.Scrum6.DTO.SolicitudRecoleccionDTO;
import com.EcoSoftware.Scrum6.Service.SolicitudRecoleccionService;
import com.EcoSoftware.Scrum6.Enums.EstadoPeticion;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/solicitudes")
public class SolicitudRecoleccionController {

    private final SolicitudRecoleccionService solicitudService;

    public SolicitudRecoleccionController(SolicitudRecoleccionService solicitudService) {
        this.solicitudService = solicitudService;
    }

    // Crear nueva solicitud
    @PostMapping
    public ResponseEntity<SolicitudRecoleccionDTO> crearSolicitud(@RequestBody SolicitudRecoleccionDTO dto) {
        return ResponseEntity.ok(solicitudService.crearSolicitud(dto));
    }

    // Obtener solicitud por ID
    @GetMapping("/{id}")
    public ResponseEntity<SolicitudRecoleccionDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(solicitudService.obtenerPorId(id));
    }

    // Listar todas las solicitudes
    @GetMapping
    public ResponseEntity<List<SolicitudRecoleccionDTO>> listarTodas() {
        return ResponseEntity.ok(solicitudService.listarTodas());
    }

    // Listar solicitudes por estado
    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<SolicitudRecoleccionDTO>> listarPorEstado(@PathVariable EstadoPeticion estado) {
        return ResponseEntity.ok(solicitudService.listarPorEstado(estado));
    }

    // Aceptar solicitud (solo si está pendiente)
    @PostMapping("/{id}/aceptar/{recolectorId}")
    public ResponseEntity<SolicitudRecoleccionDTO> aceptarSolicitud(
            @PathVariable Long id,
            @PathVariable Long recolectorId) {
        return ResponseEntity.ok(solicitudService.aceptarSolicitud(id, recolectorId));
    }

    // Rechazar solicitud (solo si está pendiente)
    @PostMapping("/{id}/rechazar")
    public ResponseEntity<SolicitudRecoleccionDTO> rechazarSolicitud(
            @PathVariable Long id,
            @RequestParam String motivo) {
        return ResponseEntity.ok(solicitudService.rechazarSolicitud(id, motivo));
    }

    // Actualizar solicitud (solo si está pendiente)
    @PutMapping("/{id}")
    public ResponseEntity<SolicitudRecoleccionDTO> actualizarSolicitud(
            @PathVariable Long id,
            @RequestBody SolicitudRecoleccionDTO dto) {

        dto.setIdSolicitud(id); 
        return ResponseEntity.ok(solicitudService.actualizarSolicitud(dto));
    }
}
