package com.EcoSoftware.Scrum6.Controller;

import com.EcoSoftware.Scrum6.DTO.SolicitudRecoleccionDTO;
import com.EcoSoftware.Scrum6.Enums.Localidad;
import com.EcoSoftware.Scrum6.Enums.EstadoPeticion;
import com.EcoSoftware.Scrum6.Service.SolicitudRecoleccionService;
import com.itextpdf.text.DocumentException;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

/**
 * Controlador REST que gestiona las operaciones relacionadas con las solicitudes de recolección.
 * Define los endpoints para crear, listar, actualizar, aceptar, rechazar y exportar solicitudes.
 */
@RestController
@RequestMapping("/api/solicitudes")
public class SolicitudRecoleccionController {

    // Inyección del servicio que maneja la lógica de negocio
    private final SolicitudRecoleccionService solicitudService;

    // Constructor que inyecta el servicio de solicitudes
    public SolicitudRecoleccionController(SolicitudRecoleccionService solicitudService) {
        this.solicitudService = solicitudService;
    }

    // ========================================================
    // CREAR SOLICITUD - ID del usuario se obtiene del token
    // ========================================================
    @PostMapping
    public ResponseEntity<SolicitudRecoleccionDTO> crearSolicitud(@RequestBody SolicitudRecoleccionDTO dto) {
        // Obtiene el usuario autenticado desde el contexto de seguridad
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String correoUsuario = auth.getName(); // El correo se extrae del token JWT
        // Envía la solicitud al servicio con el correo del usuario autenticado
        return ResponseEntity.ok(solicitudService.crearSolicitudConUsuario(dto, correoUsuario));
    }

    // ========================================================
    // OBTENER SOLICITUD POR ID
    // ========================================================
    @GetMapping("/{id}")
    public ResponseEntity<SolicitudRecoleccionDTO> obtenerPorId(@PathVariable Long id) {
        // Retorna la solicitud correspondiente al ID recibido
        return ResponseEntity.ok(solicitudService.obtenerPorId(id));
    }

    // ========================================================
    // LISTAR TODAS LAS SOLICITUDES (ADMIN)
    // ========================================================
    @GetMapping
    public ResponseEntity<List<SolicitudRecoleccionDTO>> listarTodas() {
        // Retorna la lista completa de solicitudes
        return ResponseEntity.ok(solicitudService.listarTodas());
    }

    // ========================================================
    // LISTAR SOLICITUDES POR ESTADO
    // ========================================================
    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<SolicitudRecoleccionDTO>> listarPorEstado(@PathVariable EstadoPeticion estado) {
        // Retorna las solicitudes filtradas por su estado
        return ResponseEntity.ok(solicitudService.listarPorEstado(estado));
    }

    // ========================================================
    // ACEPTAR SOLICITUD (Recolector)
    // ========================================================
    @PostMapping("/{id}/aceptar")
    public ResponseEntity<SolicitudRecoleccionDTO> aceptarSolicitud(@PathVariable Long id) {
        // Llama al servicio para aceptar la solicitud indicada
        return ResponseEntity.ok(solicitudService.aceptarSolicitud(id));
    }

    // ========================================================
    // RECHAZAR SOLICITUD
    // ========================================================
    @PostMapping("/{id}/rechazar")
    public ResponseEntity<SolicitudRecoleccionDTO> rechazarSolicitud(
            @PathVariable Long id,
            @RequestParam String motivo) {
        // Llama al servicio para rechazar la solicitud con un motivo
        return ResponseEntity.ok(solicitudService.rechazarSolicitud(id, motivo));
    }

    // ========================================================
    // ACTUALIZAR SOLICITUD (solo del usuario logueado)
    // ========================================================
    @PutMapping("/{id}")
    public ResponseEntity<SolicitudRecoleccionDTO> actualizarSolicitud(
            @PathVariable Long id,
            @RequestBody SolicitudRecoleccionDTO dto) {

        // Obtiene el correo del usuario autenticado
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String correoUsuario = auth.getName();

        // Asigna el ID recibido a la solicitud antes de actualizar
        dto.setIdSolicitud(id);

        // Actualiza la solicitud asociada al usuario autenticado
        return ResponseEntity.ok(solicitudService.actualizarSolicitudConUsuario(dto, correoUsuario));
    }

    // ========================================================
    // EXPORTACIONES (Excel / PDF)
    // ========================================================

    // Generar reporte Excel de solicitudes filtradas
    @GetMapping("/export/excel")
    public void exportToExcel(
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) String localidad,
            @RequestParam(required = false) String fechaDesde,
            @RequestParam(required = false) String fechaHasta,
            HttpServletResponse response) throws IOException {

        // Conversión de parámetros opcionales a enums y fechas
        EstadoPeticion estadoEnum = parseEstado(estado);
        Localidad localidadEnum = parseLocalidad(localidad);
        LocalDateTime inicio = parseFechaInicio(fechaDesde);
        LocalDateTime fin = parseFechaFin(fechaHasta);

        // Configuración de encabezados y tipo de archivo Excel
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=solicitudes.xlsx");

        // Generación del reporte
        solicitudService.generarReporteExcel(estadoEnum, localidadEnum, inicio, fin, response.getOutputStream());
    }

    // Generar reporte PDF de solicitudes filtradas
    @GetMapping("/export/pdf")
    public void exportToPDF(
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) String localidad,
            @RequestParam(required = false) String fechaDesde,
            @RequestParam(required = false) String fechaHasta,
            HttpServletResponse response) throws IOException, DocumentException {

        // Conversión de parámetros opcionales a enums y fechas
        EstadoPeticion estadoEnum = parseEstado(estado);
        Localidad localidadEnum = parseLocalidad(localidad);
        LocalDateTime inicio = parseFechaInicio(fechaDesde);
        LocalDateTime fin = parseFechaFin(fechaHasta);

        // Configuración de encabezados y tipo de archivo PDF
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=solicitudes.pdf");

        // Generación del reporte
        solicitudService.generarReportePDF(estadoEnum, localidadEnum, inicio, fin, response.getOutputStream());
    }

    // ========================================================
    // MÉTODOS AUXILIARES (Helpers)
    // ========================================================

    // Convierte una cadena en el Enum EstadoPeticion, si es válida
    private EstadoPeticion parseEstado(String estado) {
        if (estado == null || estado.isBlank()) return null;
        for (EstadoPeticion ep : EstadoPeticion.values()) {
            if (ep.name().equalsIgnoreCase(estado.trim())) return ep;
        }
        return null;
    }

    // Convierte una cadena en el Enum Localidad, si es válida
    private Localidad parseLocalidad(String localidad) {
        if (localidad == null || localidad.isBlank()) return null;
        for (Localidad l : Localidad.values()) {
            if (l.name().equalsIgnoreCase(localidad.trim())) return l;
        }
        return null;
    }

    // Convierte una fecha en formato String al inicio del día
    private LocalDateTime parseFechaInicio(String fechaDesde) {
        if (fechaDesde == null || fechaDesde.isBlank()) return null;
        LocalDate d = LocalDate.parse(fechaDesde);
        return d.atStartOfDay();
    }

    // Convierte una fecha en formato String al final del día
    private LocalDateTime parseFechaFin(String fechaHasta) {
        if (fechaHasta == null || fechaHasta.isBlank()) return null;
        LocalDate d = LocalDate.parse(fechaHasta);
        return LocalDateTime.of(d, LocalTime.MAX);
    }
}
