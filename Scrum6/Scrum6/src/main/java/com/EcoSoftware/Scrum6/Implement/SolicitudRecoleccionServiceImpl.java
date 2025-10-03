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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDateTime;
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

    // --- Conversión Entity ↔ DTO --- (mantengo tus métodos)
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

    // --- Métodos ya existentes (mantengo tal cual) ---
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

        solicitud.setAceptadaPor(recolector);
        solicitud.setEstadoPeticion(EstadoPeticion.Aceptada);

        RecoleccionEntity recoleccion = new RecoleccionEntity();
        recoleccion.setSolicitud(solicitud);
        recoleccion.setRecolector(recolector);
        recoleccion.setEstado(com.EcoSoftware.Scrum6.Enums.EstadoRecoleccion.En_Progreso);
        recoleccion.setFechaRecoleccion(solicitud.getFechaProgramada());
        recoleccion.setEvidencia(solicitud.getEvidencia());
        recoleccion.setObservaciones("Recolección iniciada");

        solicitud.setRecoleccion(recoleccion);

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

        solicitud.setTipoResiduo(dto.getTipoResiduo());
        solicitud.setCantidad(dto.getCantidad());
        solicitud.setDescripcion(dto.getDescripcion());
        solicitud.setLocalidad(dto.getLocalidad());
        solicitud.setUbicacion(dto.getUbicacion());
        solicitud.setEvidencia(dto.getEvidencia());
        solicitud.setFechaProgramada(dto.getFechaProgramada());

        return entityToDTO(solicitudRepository.save(solicitud));
    }

    // =====================================================
    // MÉTODO AUXILIAR: obtener solicitudes filtradas
    // =====================================================
    private List<SolicitudRecoleccionDTO> obtenerSolicitudesFiltradas(EstadoPeticion estado,
                                                                      Localidad localidad,
                                                                      LocalDateTime fechaInicio,
                                                                      LocalDateTime fechaFin) {
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

        List<SolicitudRecoleccionDTO> dtos = entities.stream()
                .map(this::entityToDTO)
                .collect(Collectors.toList());

        // Si no hay filtro por fechas, devolvemos la lista tal cual
        if (fechaInicio == null && fechaFin == null) {
            return dtos;
        }

        // Aplicar filtrado por rango de fechas (creación O programada)
        return dtos.stream().filter(dto -> {
            boolean inRange = false;

            // fechaCreacionSolicitud es OffsetDateTime en DTO -> convertimos a LocalDateTime
            if (dto.getFechaCreacionSolicitud() != null) {
                LocalDateTime fc = dto.getFechaCreacionSolicitud().toLocalDateTime();
                boolean cond = true;
                if (fechaInicio != null && fc.isBefore(fechaInicio)) cond = false;
                if (fechaFin != null && fc.isAfter(fechaFin)) cond = false;
                if (cond) inRange = true;
            }

            // Si no quedó en rango, verificamos fechaProgramada
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

    // ==============================
    // Generar reporte Excel
    // ==============================
    @Override
    public void generarReporteExcel(EstadoPeticion estado,
                                    Localidad localidad,
                                    LocalDateTime fechaInicio,
                                    LocalDateTime fechaFin,
                                    OutputStream os) throws IOException {

        List<SolicitudRecoleccionDTO> solicitudes = obtenerSolicitudesFiltradas(estado, localidad, fechaInicio, fechaFin);

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Solicitudes");

        String[] headers = {"ID", "UsuarioId", "AceptadaPorId", "TipoResiduo", "Cantidad",
                "EstadoPeticion", "Descripcion", "Localidad", "Ubicacion", "Evidencia",
                "FechaCreacionSolicitud", "FechaProgramada", "RecoleccionId"};

        // Header row
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            headerRow.createCell(i).setCellValue(headers[i]);
        }

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
            row.createCell(10).setCellValue(s.getFechaCreacionSolicitud() != null ? s.getFechaCreacionSolicitud().toString() : "");
            row.createCell(11).setCellValue(s.getFechaProgramada() != null ? s.getFechaProgramada().toString() : "");
            row.createCell(12).setCellValue(s.getRecoleccionId() != null ? s.getRecoleccionId() : 0);
        }

        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }

        workbook.write(os);
        workbook.close();
    }

    // ==============================
    // Generar reporte PDF
    // ==============================
    @Override
    public void generarReportePDF(EstadoPeticion estado,
                                  Localidad localidad,
                                  LocalDateTime fechaInicio,
                                  LocalDateTime fechaFin,
                                  OutputStream os) throws IOException, DocumentException {

        List<SolicitudRecoleccionDTO> solicitudes = obtenerSolicitudesFiltradas(estado, localidad, fechaInicio, fechaFin);

        Document document = new Document(PageSize.A4.rotate());
        PdfWriter.getInstance(document, os);
        document.open();

        document.add(new Paragraph("Reporte de Solicitudes"));
        document.add(new Paragraph(" "));

        String[] headers = {"ID", "UsuarioId", "AceptadaPorId", "TipoResiduo", "Cantidad",
                "EstadoPeticion", "Descripcion", "Localidad", "Ubicacion",
                "FechaCreacionSolicitud", "FechaProgramada", "RecoleccionId"};

        PdfPTable table = new PdfPTable(headers.length);
        table.setWidthPercentage(100);

        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header));
            cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            table.addCell(cell);
        }

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

        document.add(table);
        document.close();
    }
}
