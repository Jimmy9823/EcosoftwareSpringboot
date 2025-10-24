package com.EcoSoftware.Scrum6.Implement;

import com.EcoSoftware.Scrum6.DTO.SolicitudRecoleccionDTO;
import com.EcoSoftware.Scrum6.Entity.RecoleccionEntity;
import com.EcoSoftware.Scrum6.Entity.SolicitudRecoleccionEntity;
import com.EcoSoftware.Scrum6.Entity.UsuarioEntity;
import com.EcoSoftware.Scrum6.Repository.SolicitudRecoleccionRepository;
import com.EcoSoftware.Scrum6.Repository.UsuarioRepository;
import com.EcoSoftware.Scrum6.Enums.EstadoPeticion;
import com.EcoSoftware.Scrum6.Enums.Localidad;
import com.EcoSoftware.Scrum6.Service.SolicitudRecoleccionService;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.Authentication;

import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

/**
 * Implementación del servicio para gestionar solicitudes de recolección.
 * Contiene métodos CRUD, generación de reportes (Excel y PDF)
 * y lógica de negocio para aceptar/rechazar solicitudes.
 */
@Service
@Transactional
public class SolicitudRecoleccionServiceImpl implements SolicitudRecoleccionService {

    private final SolicitudRecoleccionRepository solicitudRepository;
    private final UsuarioRepository usuarioRepository;

    // Constructor que inyecta los repositorios necesarios
    public SolicitudRecoleccionServiceImpl(SolicitudRecoleccionRepository solicitudRepository,
            UsuarioRepository usuarioRepository) {
        this.solicitudRepository = solicitudRepository;
        this.usuarioRepository = usuarioRepository;
    }

    // ==========================================================
    // Conversión entre Entity ↔ DTO
    // ==========================================================
    private SolicitudRecoleccionDTO entityToDTO(SolicitudRecoleccionEntity entity) {
        // Convierte una entidad en su versión DTO para transportar datos al frontend
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
        // Convierte un DTO en una entidad para guardar en base de datos
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

    // ==========================================================
    // Crear solicitud asociada al usuario logueado
    // ==========================================================
    @Override
    public SolicitudRecoleccionDTO crearSolicitudConUsuario(SolicitudRecoleccionDTO dto, String correoUsuario) {
        // 1️. Buscar usuario que hace la solicitud
        UsuarioEntity usuario = usuarioRepository.findByCorreo(correoUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con el correo: " + correoUsuario));

        // 2️. Crear y configurar la entidad
        SolicitudRecoleccionEntity entity = new SolicitudRecoleccionEntity();
        entity.setUsuario(usuario);
        entity.setTipoResiduo(dto.getTipoResiduo());
        entity.setCantidad(dto.getCantidad());
        entity.setDescripcion(dto.getDescripcion());
        entity.setLocalidad(dto.getLocalidad());
        entity.setUbicacion(dto.getUbicacion());
        entity.setEvidencia(dto.getEvidencia());
        entity.setFechaProgramada(dto.getFechaProgramada());
        entity.setEstadoPeticion(EstadoPeticion.Pendiente);
        entity.setFechaCreacionSolicitud(OffsetDateTime.now());

        // 3️. Guardar en BD
        SolicitudRecoleccionEntity saved = solicitudRepository.save(entity);
        return entityToDTO(saved);
    }

    // ==========================================================
    // Obtener una solicitud por ID
    // ==========================================================
    @Override
    public SolicitudRecoleccionDTO obtenerPorId(Long id) {
        SolicitudRecoleccionEntity entity = solicitudRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));
        return entityToDTO(entity);
    }

    // ==========================================================
    // Listar todas las solicitudes
    // ==========================================================
    @Override
    public List<SolicitudRecoleccionDTO> listarTodas() {
        return solicitudRepository.findAll().stream()
                .map(this::entityToDTO)
                .collect(Collectors.toList());
    }

    // ==========================================================
    // Listar solicitudes por estado (Pendiente, Aceptada, etc.)
    // ==========================================================
    @Override
    public List<SolicitudRecoleccionDTO> listarPorEstado(EstadoPeticion estado) {
        return solicitudRepository.findByEstadoPeticion(estado).stream()
                .map(this::entityToDTO)
                .collect(Collectors.toList());
    }

    // ==========================================================
    // Aceptar solicitud (Recolector la toma)
    // ==========================================================
    @Override
    public SolicitudRecoleccionDTO aceptarSolicitud(Long solicitudId) {
        // 1️. Buscar solicitud
        SolicitudRecoleccionEntity solicitud = solicitudRepository.findById(solicitudId)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));

        // 2️. Validar que esté pendiente
        if (solicitud.getEstadoPeticion() != EstadoPeticion.Pendiente) {
            throw new RuntimeException("Solo se pueden aceptar solicitudes pendientes");
        }

        // 3️. Obtener recolector desde el contexto de seguridad (token)
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String correoRecolector = auth.getName();

        // 4️. Buscar recolector en BD
        UsuarioEntity recolector = usuarioRepository.findByCorreo(correoRecolector)
                .orElseThrow(() -> new RuntimeException("Recolector no encontrado"));

        // 5️. Actualizar solicitud
        solicitud.setAceptadaPor(recolector);
        solicitud.setEstadoPeticion(EstadoPeticion.Aceptada);

        // 6️. Crear recolección asociada
        RecoleccionEntity recoleccion = new RecoleccionEntity();
        recoleccion.setSolicitud(solicitud);
        recoleccion.setRecolector(recolector);
        recoleccion.setEstado(com.EcoSoftware.Scrum6.Enums.EstadoRecoleccion.En_Progreso);
        recoleccion.setFechaRecoleccion(solicitud.getFechaProgramada());
        recoleccion.setEvidencia(solicitud.getEvidencia());
        recoleccion.setObservaciones("Recolección iniciada");

        solicitud.setRecoleccion(recoleccion);

        // 7️. Guardar cambios
        SolicitudRecoleccionEntity saved = solicitudRepository.save(solicitud);
        return entityToDTO(saved);
    }

    // ==========================================================
    // Rechazar solicitud (con motivo)
    // ==========================================================
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

    // ==========================================================
    // Actualizar solicitud (solo si está pendiente y pertenece al usuario)
    // ==========================================================
    @Override
    public SolicitudRecoleccionDTO actualizarSolicitudConUsuario(SolicitudRecoleccionDTO dto, String correoUsuario) {
        // 1️. Buscar usuario logueado
        UsuarioEntity usuario = usuarioRepository.findByCorreo(correoUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con el correo: " + correoUsuario));

        // 2️. Buscar solicitud
        SolicitudRecoleccionEntity solicitud = solicitudRepository.findById(dto.getIdSolicitud())
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));

        // 3️. Validar propiedad
        if (!solicitud.getUsuario().getIdUsuario().equals(usuario.getIdUsuario())) {
            throw new RuntimeException("No tienes permiso para modificar esta solicitud");
        }

        // 4️. Validar estado
        if (solicitud.getEstadoPeticion() != EstadoPeticion.Pendiente) {
            throw new RuntimeException("Solo se pueden actualizar solicitudes pendientes");
        }

        // 5️. Actualizar campos
        solicitud.setTipoResiduo(dto.getTipoResiduo());
        solicitud.setCantidad(dto.getCantidad());
        solicitud.setDescripcion(dto.getDescripcion());
        solicitud.setLocalidad(dto.getLocalidad());
        solicitud.setUbicacion(dto.getUbicacion());
        solicitud.setEvidencia(dto.getEvidencia());
        solicitud.setFechaProgramada(dto.getFechaProgramada());

        SolicitudRecoleccionEntity saved = solicitudRepository.save(solicitud);
        return entityToDTO(saved);
    }

    // ==========================================================
    // Método auxiliar para filtrar solicitudes
    // ==========================================================
    private List<SolicitudRecoleccionDTO> obtenerSolicitudesFiltradas(EstadoPeticion estado,
            Localidad localidad,
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin) {

        // 1️. Consultar según filtros
        List<SolicitudRecoleccionEntity> entities;
        if (estado != null && localidad != null) {
            entities = solicitudRepository.findByLocalidadAndEstadoPeticion(localidad, estado);
        } else if (estado != null) {
            entities = solicitudRepository.findByEstadoPeticion(estado);
        } else if (localidad != null) {
            entities = solicitudRepository.findByLocalidad(localidad);
        } else {
            entities = solicitudRepository.findAll();
        }

        // 2️. Convertir a DTO
        List<SolicitudRecoleccionDTO> dtos = entities.stream()
                .map(this::entityToDTO)
                .collect(Collectors.toList());

        // 3️. Filtrar por fechas (opcional)
        if (fechaInicio == null && fechaFin == null) return dtos;

        return dtos.stream().filter(dto -> {
            boolean inRange = false;

            // Filtrar por fecha de creación
            if (dto.getFechaCreacionSolicitud() != null) {
                LocalDateTime fc = dto.getFechaCreacionSolicitud().toLocalDateTime();
                boolean cond = true;
                if (fechaInicio != null && fc.isBefore(fechaInicio)) cond = false;
                if (fechaFin != null && fc.isAfter(fechaFin)) cond = false;
                if (cond) inRange = true;
            }

            // Si no entra, revisar fecha programada
            if (!inRange && dto.getFechaProgramada() != null) {
                LocalDateTime fp = dto.getFechaProgramada();
                boolean cond = true;
                if (fechaInicio != null && fp.isBefore(fechaInicio)) cond = false;
                if (fechaFin != null && fp.isAfter(fechaFin)) cond = false;
                if (cond) inRange = true;
            }

            return inRange;
        }).collect(Collectors.toList());
    }

    // ==========================================================
    // Generar reporte Excel
    // ==========================================================
    @Override
    public void generarReporteExcel(EstadoPeticion estado,
            Localidad localidad,
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin,
            OutputStream os) throws IOException {

        // 1️. Obtener datos filtrados
        List<SolicitudRecoleccionDTO> solicitudes = obtenerSolicitudesFiltradas(estado, localidad, fechaInicio, fechaFin);

        // 2️. Crear libro de Excel
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Solicitudes");

        // 3️. Definir encabezados
        String[] headers = { "ID", "UsuarioId", "AceptadaPorId", "TipoResiduo", "Cantidad",
                "EstadoPeticion", "Descripcion", "Localidad", "Ubicacion", "Evidencia",
                "FechaCreacionSolicitud", "FechaProgramada", "RecoleccionId" };

        // 4️. Crear fila de encabezado
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            headerRow.createCell(i).setCellValue(headers[i]);
        }

        // 5️. Llenar filas con datos
        int rowNum = 1;
        for (SolicitudRecoleccionDTO s : solicitudes) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(s.getIdSolicitud() != null ? s.getIdSolicitud() : 0);
            row.createCell(1).setCellValue(s.getUsuarioId() != null ? s.getUsuarioId() : 0);
            row.createCell(2).setCellValue(s.getAceptadaPorId() != null ? s.getAceptadaPorId() : 0);
            row.createCell(3).setCellValue(s.getTipoResiduo() != null ? s.getTipoResiduo().name() : "");
            row.createCell(4).setCellValue(s.getCantidad() != null ? s.getCantidad() : "");
            row.createCell(5).setCellValue(s.getEstadoPeticion() != null ? s.getEstadoPeticion().name() : "");
            row.createCell(6).setCellValue(s.getDescripcion() != null ? s.getDescripcion() : "");
            row.createCell(7).setCellValue(s.getLocalidad() != null ? s.getLocalidad().name() : "");
            row.createCell(8).setCellValue(s.getUbicacion() != null ? s.getUbicacion() : "");
            row.createCell(9).setCellValue(s.getEvidencia() != null ? s.getEvidencia() : "");
            row.createCell(10).setCellValue(
                    s.getFechaCreacionSolicitud() != null ? s.getFechaCreacionSolicitud().toString() : "");
            row.createCell(11).setCellValue(s.getFechaProgramada() != null ? s.getFechaProgramada().toString() : "");
            row.createCell(12).setCellValue(s.getRecoleccionId() != null ? s.getRecoleccionId() : 0);
        }

        // 6️. Ajustar ancho de columnas
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }

        // 7️. Escribir en flujo de salida
        workbook.write(os);
        workbook.close();
    }

    // ==========================================================
    // Generar reporte PDF
    // ==========================================================
    @Override
    public void generarReportePDF(EstadoPeticion estado,
            Localidad localidad,
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin,
            OutputStream os) throws IOException, DocumentException {

        // 1️. Obtener datos filtrados
        List<SolicitudRecoleccionDTO> solicitudes = obtenerSolicitudesFiltradas(estado, localidad, fechaInicio, fechaFin);

        // 2️. Configurar documento PDF
        Document document = new Document(PageSize.A4.rotate());
        PdfWriter.getInstance(document, os);
        document.open();

        // 3️. Título
        document.add(new Paragraph("Reporte de Solicitudes"));
        document.add(new Paragraph(" "));

        // 4️. Encabezados de tabla
        String[] headers = { "ID", "UsuarioId", "AceptadaPorId", "TipoResiduo", "Cantidad",
                "EstadoPeticion", "Descripcion", "Localidad", "Ubicacion",
                "FechaCreacionSolicitud", "FechaProgramada", "RecoleccionId" };

        PdfPTable table = new PdfPTable(headers.length);
        table.setWidthPercentage(100);

        // 5️. Crear celdas de encabezado
        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header));
            cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            table.addCell(cell);
        }

        // 6️. Llenar filas con datos
        for (SolicitudRecoleccionDTO s : solicitudes) {
            table.addCell(s.getIdSolicitud() != null ? s.getIdSolicitud().toString() : "");
            table.addCell(s.getUsuarioId() != null ? s.getUsuarioId().toString() : "");
            table.addCell(s.getAceptadaPorId() != null ? s.getAceptadaPorId().toString() : "");
            table.addCell(s.getTipoResiduo() != null ? s.getTipoResiduo().name() : "");
            table.addCell(s.getCantidad() != null ? s.getCantidad() : "");
            table.addCell(s.getEstadoPeticion() != null ? s.getEstadoPeticion().name() : "");
            table.addCell(s.getDescripcion() != null ? s.getDescripcion() : "");
            table.addCell(s.getLocalidad() != null ? s.getLocalidad().name() : "");
            table.addCell(s.getUbicacion() != null ? s.getUbicacion() : "");
            table.addCell(s.getFechaCreacionSolicitud() != null ? s.getFechaCreacionSolicitud().toString() : "");
            table.addCell(s.getFechaProgramada() != null ? s.getFechaProgramada().toString() : "");
            table.addCell(s.getRecoleccionId() != null ? s.getRecoleccionId().toString() : "");
        }

        // 7️. Agregar tabla al documento
        document.add(table);
        document.close();
    }
}
