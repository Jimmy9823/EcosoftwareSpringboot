package com.EcoSoftware.Scrum6.Controller;

import com.EcoSoftware.Scrum6.DTO.SolicitudRecoleccionDTO;
import com.EcoSoftware.Scrum6.Enums.Localidad;
import com.EcoSoftware.Scrum6.Enums.EstadoPeticion;
import com.EcoSoftware.Scrum6.Service.SolicitudRecoleccionService;
import com.itextpdf.text.DocumentException;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
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


    // ===========================
    // EXPORTACIONES
    // ===========================

    @GetMapping("/export/excel")
    public void exportToExcel(
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) String localidad,
            @RequestParam(required = false) String fechaDesde, // yyyy-MM-dd
            @RequestParam(required = false) String fechaHasta, // yyyy-MM-dd
            HttpServletResponse response) throws IOException {

        EstadoPeticion estadoEnum = parseEstado(estado);
        Localidad localidadEnum = parseLocalidad(localidad);

        LocalDateTime inicio = parseFechaInicio(fechaDesde);
        LocalDateTime fin = parseFechaFin(fechaHasta);

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=solicitudes.xlsx");
        solicitudService.generarReporteExcel(estadoEnum, localidadEnum, inicio, fin, response.getOutputStream());
    }

    @GetMapping("/export/pdf")
    public void exportToPDF(
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) String localidad,
            @RequestParam(required = false) String fechaDesde, // yyyy-MM-dd
            @RequestParam(required = false) String fechaHasta, // yyyy-MM-dd
            HttpServletResponse response) throws IOException, DocumentException {

        EstadoPeticion estadoEnum = parseEstado(estado);
        Localidad localidadEnum = parseLocalidad(localidad);

        LocalDateTime inicio = parseFechaInicio(fechaDesde);
        LocalDateTime fin = parseFechaFin(fechaHasta);

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=solicitudes.pdf");
        solicitudService.generarReportePDF(estadoEnum, localidadEnum, inicio, fin, response.getOutputStream());
    }

    // -----------------------------
    // Helpers de parsing (case-insensitive)
    // -----------------------------
    private EstadoPeticion parseEstado(String estado) {
        if (estado == null || estado.isBlank()) return null;
        for (EstadoPeticion ep : EstadoPeticion.values()) {
            if (ep.name().equalsIgnoreCase(estado.trim())) return ep;
        }
        return null;
    }

    private Localidad parseLocalidad(String localidad) {
        if (localidad == null || localidad.isBlank()) return null;
        for (Localidad l : Localidad.values()) {
            if (l.name().equalsIgnoreCase(localidad.trim())) return l;
        }
        return null;
    }

    private LocalDateTime parseFechaInicio(String fechaDesde) {
        if (fechaDesde == null || fechaDesde.isBlank()) return null;
        LocalDate d = LocalDate.parse(fechaDesde); // espera yyyy-MM-dd
        return d.atStartOfDay();
    }

    private LocalDateTime parseFechaFin(String fechaHasta) {
        if (fechaHasta == null || fechaHasta.isBlank()) return null;
        LocalDate d = LocalDate.parse(fechaHasta); // espera yyyy-MM-dd
        return LocalDateTime.of(d, LocalTime.MAX); // hasta final del día
    }
}
