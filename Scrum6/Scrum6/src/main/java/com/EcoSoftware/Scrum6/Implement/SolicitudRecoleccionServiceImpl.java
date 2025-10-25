package com.EcoSoftware.Scrum6.Implement;

import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.EcoSoftware.Scrum6.DTO.SolicitudRecoleccionDTO;
import com.EcoSoftware.Scrum6.Entity.RecoleccionEntity;
import com.EcoSoftware.Scrum6.Entity.SolicitudRecoleccionEntity;
import com.EcoSoftware.Scrum6.Entity.UsuarioEntity;
import com.EcoSoftware.Scrum6.Enums.EstadoPeticion;
import com.EcoSoftware.Scrum6.Enums.Localidad;
import com.EcoSoftware.Scrum6.Repository.SolicitudRecoleccionRepository;
import com.EcoSoftware.Scrum6.Repository.UsuarioRepository;
import com.EcoSoftware.Scrum6.Service.EmailService;
import com.EcoSoftware.Scrum6.Service.SolicitudRecoleccionService;
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
    private final EmailService emailService;

    public SolicitudRecoleccionServiceImpl(SolicitudRecoleccionRepository solicitudRepository, UsuarioRepository usuarioRepository, EmailService emailService) {
        this.solicitudRepository = solicitudRepository;
        this.usuarioRepository = usuarioRepository;
        this.emailService = emailService;
    }

    /**
     * Convierte una entidad de SolicitudRecoleccionEntity a SolicitudRecoleccionDTO.
     * @param entity La entidad a convertir.
     * @return El DTO resultante.
     */
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

    // ==========================================================
    // Lógica para Correo Masivo (Método auxiliar)
    // ==========================================================
    
    /**
     * Notifica a todos los usuarios con rol 'RECICLADOR' sobre una nueva solicitud pendiente.
     * Se ajusta para usar findAll() y filtrar en memoria, sin modificar UsuarioRepository.
     */
    private void notificarRecicladoresNuevaSolicitud(SolicitudRecoleccionEntity nuevaSolicitud) {
        // 1. Obtener la lista de correos de los recicladores (Filtrando después de obtener todos)
        List<String> correosRecicladores;
        
        try {
            // Obtenemos todos los usuarios y filtramos aquellos cuyo rol sea "RECICLADOR"
            // Asume que UsuarioEntity.getRol().getNombre() existe y devuelve el nombre del rol como String.
            correosRecicladores = usuarioRepository.findAll().stream()
                                                    .filter(u -> u.getRol() != null && "RECICLADOR".equals(u.getRol().getNombre()))
                                                    .map(UsuarioEntity::getCorreo)
                                                    .collect(Collectors.toList());
        } catch (Exception e) {
            // Se captura cualquier error al acceder a la relación Rol o al campo nombre
            System.err.println("ERROR al obtener lista de recicladores para notificación masiva. Revise si la entidad UsuarioEntity tiene la relación correcta a RolEntity y si el campo del nombre del rol es 'nombre'. Mensaje: " + e.getMessage());
            return;
        }

        if (correosRecicladores.isEmpty()) {
            System.out.println("No hay recicladores registrados para notificar.");
            return;
        }

        String asunto = " ¡Nueva Solicitud Pendiente! ID: " + nuevaSolicitud.getIdSolicitud();
        String contenido = "Hola Reciclador,\n\n"
                + "Se ha registrado una **nueva solicitud de recolección pendiente** que requiere tu atención:\n"
                + " Tipo de residuo: " + nuevaSolicitud.getTipoResiduo() + "\n"
                + " Localidad: " + nuevaSolicitud.getLocalidad() + "\n"
                + " Fecha programada: " + (nuevaSolicitud.getFechaProgramada() != null ? nuevaSolicitud.getFechaProgramada().toString() : "N/A") + "\n"
                + " Por favor, accede al sistema para revisar y aceptar la solicitud.\n\n"
                + "EcoSoftware - Gestión de Residuos";

        // 2. Usar el servicio de envío masivo
        emailService.enviarCorreosMasivos(correosRecicladores, asunto, contenido);
    }

    // ==========================================================
    // Métodos CRUD
    // ==========================================================

    @Override
    public SolicitudRecoleccionDTO crearSolicitudConUsuario(SolicitudRecoleccionDTO dto, String correoUsuario) {
        UsuarioEntity usuario = usuarioRepository.findByCorreo(correoUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con el correo: " + correoUsuario));

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

        SolicitudRecoleccionEntity saved = solicitudRepository.save(entity);

        // Envío de correo al usuario solicitante (individual)
        String asunto = " Solicitud registrada correctamente";
        String contenido = "Hola " + usuario.getNombre() + ",\n\n"
                + "Tu solicitud de recolección ha sido registrada exitosamente con el ID: " + saved.getIdSolicitud() + "\n\n"
                + " Tipo de residuo: " + entity.getTipoResiduo() + "\n"
                + " Cantidad: " + entity.getCantidad() + "\n"
                + " Ubicación: " + entity.getUbicacion() + "\n"
                + " Fecha programada: " + (entity.getFechaProgramada() != null ? entity.getFechaProgramada().toString() : "N/A") + "\n\n"
                + "Gracias por contribuir al cuidado del medio ambiente.\n\n"
                + "EcoSoftware - Gestión de Residuos";
        emailService.enviarCorreo(usuario.getCorreo(), asunto, contenido);

        
        notificarRecicladoresNuevaSolicitud(saved);

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
    public SolicitudRecoleccionDTO aceptarSolicitud(Long solicitudId) {
        SolicitudRecoleccionEntity solicitud = solicitudRepository.findById(solicitudId)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));

        if (solicitud.getEstadoPeticion() != EstadoPeticion.Pendiente) {
            throw new RuntimeException("Solo se pueden aceptar solicitudes pendientes");
        }

        // Obtener el usuario reciclador/recolector autenticado
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String correoRecolector = auth.getName();
        UsuarioEntity recolector = usuarioRepository.findByCorreo(correoRecolector)
                .orElseThrow(() -> new RuntimeException("Recolector no encontrado: " + correoRecolector));

        // 1. Actualizar Solicitud
        solicitud.setAceptadaPor(recolector);
        solicitud.setEstadoPeticion(EstadoPeticion.Aceptada);

        // 2. Crear nueva Recoleccion
        RecoleccionEntity recoleccion = new RecoleccionEntity();
        recoleccion.setSolicitud(solicitud);
        recoleccion.setRecolector(recolector);
        recoleccion.setEstado(com.EcoSoftware.Scrum6.Enums.EstadoRecoleccion.En_Progreso);
        recoleccion.setFechaRecoleccion(solicitud.getFechaProgramada());
        recoleccion.setEvidencia(solicitud.getEvidencia());
        recoleccion.setObservaciones("Recolección iniciada y aceptada por: " + recolector.getNombre());
        solicitud.setRecoleccion(recoleccion); // Establecer la relación bidireccional

        // Guardar la solicitud (asumiendo CascadeType.ALL para RecoleccionEntity)
        SolicitudRecoleccionEntity saved = solicitudRepository.save(solicitud);
        
        // 3. Notificar al usuario (creador de la solicitud)
        UsuarioEntity usuarioSolicitante = solicitud.getUsuario();
        String asunto = "Solicitud de recolección aceptada";
        String contenido = "Hola " + usuarioSolicitante.getNombre() + ",\n\n"
                + "¡Buenas noticias! Tu solicitud de recolección (ID: " + solicitud.getIdSolicitud() + ") ha sido **Aceptada**.\n\n"
                + " Recolector asignado: " + recolector.getNombre() + "\n"
                + " Fecha programada: " + (solicitud.getFechaProgramada() != null ? solicitud.getFechaProgramada().toString() : "N/A") + "\n"
                + "Por favor, espera la recolección.\n\n"
                + "EcoSoftware - Gestión de Residuos";
        emailService.enviarCorreo(usuarioSolicitante.getCorreo(), asunto, contenido);

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
        SolicitudRecoleccionEntity saved = solicitudRepository.save(solicitud);

        // Notificar al usuario
        UsuarioEntity usuarioSolicitante = saved.getUsuario();
        String asunto = " Solicitud de recolección rechazada";
        String contenido = "Hola " + usuarioSolicitante.getNombre() + ",\n\n"
                + "Lamentamos informarte que tu solicitud de recolección (ID: " + saved.getIdSolicitud() + ") ha sido **Rechazada**.\n\n"
                + "Motivo del rechazo: " + (motivo != null ? motivo : "No especificado") + "\n\n"
                + "Por favor, revisa tu solicitud y vuelve a intentarlo si es necesario.\n\n"
                + "EcoSoftware - Gestión de Residuos";
        emailService.enviarCorreo(usuarioSolicitante.getCorreo(), asunto, contenido);
        
        return entityToDTO(saved);
    }

    @Override
    public SolicitudRecoleccionDTO actualizarSolicitudConUsuario(SolicitudRecoleccionDTO dto, String correoUsuario) {
        UsuarioEntity usuario = usuarioRepository.findByCorreo(correoUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con el correo: " + correoUsuario));

        SolicitudRecoleccionEntity solicitud = solicitudRepository.findById(dto.getIdSolicitud())
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));

        if (!solicitud.getUsuario().getIdUsuario().equals(usuario.getIdUsuario())) {
            throw new RuntimeException("No tienes permiso para modificar esta solicitud");
        }

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
        
        SolicitudRecoleccionEntity saved = solicitudRepository.save(solicitud);
        
        return entityToDTO(saved);
    }

    // ==========================================================
    // Método auxiliar para filtrar solicitudes
    // ==========================================================
    private List<SolicitudRecoleccionDTO> obtenerSolicitudesFiltradas(EstadoPeticion estado, Localidad localidad, LocalDateTime fechaInicio, LocalDateTime fechaFin) {
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
        if (fechaInicio == null && fechaFin == null) {
            return dtos;
        }

        return dtos.stream().filter(dto -> {
            boolean inRange = false;
            
            // Usar una función auxiliar para verificar el rango
            java.util.function.Function<LocalDateTime, Boolean> isDateInRange = (date) -> {
                boolean cond = true;
                if (fechaInicio != null && date.isBefore(fechaInicio)) cond = false;
                if (fechaFin != null && date.isAfter(fechaFin)) cond = false;
                return cond;
            };
            
            // Filtrar por fecha de creación (si existe)
            if (dto.getFechaCreacionSolicitud() != null) {
                LocalDateTime fc = dto.getFechaCreacionSolicitud().toLocalDateTime();
                if (isDateInRange.apply(fc)) inRange = true;
            }

            // Si no está en rango por fecha de creación, revisar fecha programada (si existe)
            if (!inRange && dto.getFechaProgramada() != null) {
                LocalDateTime fp = dto.getFechaProgramada();
                if (isDateInRange.apply(fp)) inRange = true;
            }

            return inRange;
        }).collect(Collectors.toList());
    }

    // ==========================================================
    // Generar reporte Excel
    // ==========================================================
    @Override
    public void generarReporteExcel(EstadoPeticion estado, Localidad localidad, LocalDateTime fechaInicio, LocalDateTime fechaFin, OutputStream os) throws IOException {
        // 1️. Obtener datos filtrados
        List<SolicitudRecoleccionDTO> solicitudes = obtenerSolicitudesFiltradas(estado, localidad, fechaInicio, fechaFin);

        // 2️. Crear libro de Excel
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Solicitudes");

        // 3️. Definir encabezados
        String[] headers = { 
            "ID", "UsuarioId", "AceptadaPorId", "TipoResiduo", "Cantidad", 
            "EstadoPeticion", "Descripcion", "Localidad", "Ubicacion", "Evidencia", 
            "FechaCreacionSolicitud", "FechaProgramada", "RecoleccionId" 
        };

        // 4️. Crear fila de encabezado
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            headerRow.createCell(i).setCellValue(headers[i]);
        }

        // 5️. Llenar filas con datos
        int rowNum = 1;
        for (SolicitudRecoleccionDTO s : solicitudes) {
            Row row = sheet.createRow(rowNum++);
            // Uso de Optional.ofNullable para manejar nulls de forma segura
            row.createCell(0).setCellValue(Optional.ofNullable(s.getIdSolicitud()).orElse(0L).doubleValue());
            row.createCell(1).setCellValue(Optional.ofNullable(s.getUsuarioId()).orElse(0L).doubleValue());
            row.createCell(2).setCellValue(Optional.ofNullable(s.getAceptadaPorId()).orElse(0L).doubleValue());
            row.createCell(3).setCellValue(Optional.ofNullable(s.getTipoResiduo()).map(Enum::name).orElse(""));
            row.createCell(4).setCellValue(Optional.ofNullable(s.getCantidad()).orElse(""));
            row.createCell(5).setCellValue(Optional.ofNullable(s.getEstadoPeticion()).map(Enum::name).orElse(""));
            row.createCell(6).setCellValue(Optional.ofNullable(s.getDescripcion()).orElse(""));
            row.createCell(7).setCellValue(Optional.ofNullable(s.getLocalidad()).map(Enum::name).orElse(""));
            row.createCell(8).setCellValue(Optional.ofNullable(s.getUbicacion()).orElse(""));
            row.createCell(9).setCellValue(Optional.ofNullable(s.getEvidencia()).orElse(""));
            row.createCell(10).setCellValue(Optional.ofNullable(s.getFechaCreacionSolicitud()).map(OffsetDateTime::toString).orElse(""));
            row.createCell(11).setCellValue(Optional.ofNullable(s.getFechaProgramada()).map(LocalDateTime::toString).orElse(""));
            row.createCell(12).setCellValue(Optional.ofNullable(s.getRecoleccionId()).orElse(0L).doubleValue());
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
    public void generarReportePDF(EstadoPeticion estado, Localidad localidad, LocalDateTime fechaInicio, LocalDateTime fechaFin, OutputStream os) throws IOException, DocumentException {
        // 1️. Obtener datos filtrados
        List<SolicitudRecoleccionDTO> solicitudes = obtenerSolicitudesFiltradas(estado, localidad, fechaInicio, fechaFin);

        // 2️. Configurar documento PDF
        Document document = new Document(PageSize.A4.rotate());
        PdfWriter.getInstance(document, os);
        document.open();

        // 3️. Título
        document.add(new Paragraph("Reporte de Solicitudes de Recolección"));
        document.add(new Paragraph(" "));

        // 4️. Encabezados de tabla
        String[] headers = { 
            "ID", "UsuarioId", "AceptadaPorId", "TipoResiduo", "Cantidad", 
            "EstadoPeticion", "Descripcion", "Localidad", "Ubicacion", 
            "FechaCreacionSolicitud", "FechaProgramada", "RecoleccionId" 
        };
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
            // Uso de Optional.ofNullable para manejar nulls de forma segura
            table.addCell(Optional.ofNullable(s.getIdSolicitud()).map(Object::toString).orElse(""));
            table.addCell(Optional.ofNullable(s.getUsuarioId()).map(Object::toString).orElse(""));
            table.addCell(Optional.ofNullable(s.getAceptadaPorId()).map(Object::toString).orElse(""));
            table.addCell(Optional.ofNullable(s.getTipoResiduo()).map(Enum::name).orElse(""));
            table.addCell(Optional.ofNullable(s.getCantidad()).orElse(""));
            table.addCell(Optional.ofNullable(s.getEstadoPeticion()).map(Enum::name).orElse(""));
            table.addCell(Optional.ofNullable(s.getDescripcion()).orElse(""));
            table.addCell(Optional.ofNullable(s.getLocalidad()).map(Enum::name).orElse(""));
            table.addCell(Optional.ofNullable(s.getUbicacion()).orElse(""));
            table.addCell(Optional.ofNullable(s.getFechaCreacionSolicitud()).map(OffsetDateTime::toString).orElse(""));
            table.addCell(Optional.ofNullable(s.getFechaProgramada()).map(LocalDateTime::toString).orElse(""));
            table.addCell(Optional.ofNullable(s.getRecoleccionId()).map(Object::toString).orElse(""));
        }

        // 7️. Agregar tabla al documento
        document.add(table);
        document.close();
    }
}