package com.EcoSoftware.Scrum6.Implement;

import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.EcoSoftware.Scrum6.DTO.UsuarioDTO;
import com.EcoSoftware.Scrum6.DTO.UsuarioEditarDTO;
import com.EcoSoftware.Scrum6.Entity.RolEntity;
import com.EcoSoftware.Scrum6.Entity.UsuarioEntity;
import com.EcoSoftware.Scrum6.Repository.RolRepository;
import com.EcoSoftware.Scrum6.Repository.UsuarioRepository;
import com.EcoSoftware.Scrum6.Service.UsuarioService;
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
public class UsuarioServiceImpl implements UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RolRepository rolRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private com.EcoSoftware.Scrum6.Service.EmailService emailService;



    @Override
    public List<UsuarioDTO> listarUsuarios() {
        return usuarioRepository.findAll()
                .stream()
                .map(this::convertirADTO)
                .toList();
    }

    @Override
    public UsuarioDTO obtenerUsuarioPorId(Long idUsuario) {
        UsuarioEntity usuarioEntity = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Persona no encontrada con ID: " + idUsuario));
        return convertirADTO(usuarioEntity);
    }

    @Override
public UsuarioDTO crearUsuario(UsuarioDTO usuarioDTO) {
    if (usuarioDTO.getRolId() == null) {
        throw new RuntimeException("El rol es obligatorio");
    }

    // Mapear el DTO a la entidad
    UsuarioEntity entity = modelMapper.map(usuarioDTO, UsuarioEntity.class);

    // Buscar el rol
    RolEntity rol = rolRepository.findById(usuarioDTO.getRolId())
            .orElseThrow(() -> new RuntimeException("Rol no encontrado con id " + usuarioDTO.getRolId()));
    entity.setRol(rol);

    // Cifrar la contraseña antes de guardar
    entity.setContrasena(passwordEncoder.encode(usuarioDTO.getContrasena()));

    // Asignar valores por defecto
    if (entity.getEstado() == null) {
        entity.setEstado(true);
    }
    if (entity.getFechaCreacion() == null) {
        entity.setFechaCreacion(LocalDateTime.now());
    }
    entity.setFechaActualizacion(LocalDateTime.now());

    // Guardar el usuario
    UsuarioEntity saved = usuarioRepository.save(entity);

    // Enviar correo de bienvenida
    String asunto = "¡Bienvenido a EcoSoftware!";
    String contenido = "Hola " + saved.getNombre() + ",\n\n"
            + "Te damos la bienvenida a EcoSoftware, la plataforma para la gestión responsable de residuos.\n\n"
            + "Tu cuenta ha sido creada exitosamente. Ahora puedes acceder y comenzar a contribuir al cuidado del medio ambiente.\n\n"
            + "Si tienes dudas o necesitas ayuda, puedes contactarnos en cualquier momento.\n\n"
            + "¡Gracias por unirte a nuestra comunidad!\n\n"
            + "EcoSoftware - Gestión de Residuos";
    emailService.enviarCorreo(saved.getCorreo(), asunto, contenido);

    // Devolver el DTO sin exponer la contraseña
    UsuarioDTO result = modelMapper.map(saved, UsuarioDTO.class);
    result.setContrasena(null); //  Importante: no devolver contraseñas
    return result;
}


    @Override
    public UsuarioEditarDTO actualizarUsuario(Long idUsuario, UsuarioEditarDTO usuarioDTO) {
        UsuarioEntity usuarioExistente = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + idUsuario));

        String rol = usuarioExistente.getRol().getNombre();
        String zonaOriginal = usuarioExistente.getZona_de_trabajo();
        Integer cantidadMinima = usuarioExistente.getCantidad_minima();
        modelMapper.map(usuarioDTO, usuarioExistente);

        if (rol.equals("Ciudadano")) {
            usuarioExistente.setZona_de_trabajo(zonaOriginal);
            usuarioExistente.setCantidad_minima(cantidadMinima);
        }

        UsuarioEntity usuarioActualizado = usuarioRepository.save(usuarioExistente);
        return convertirAEditarUsuarioDTO(usuarioActualizado);
    }

    @Override
    public void eliminarPersona(Long idUsuario) {
        UsuarioEntity usuarioEntity = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + idUsuario));
        usuarioRepository.delete(usuarioEntity);
    }

    @Override
    public void eliminacionPorEstado(Long idUsuario) {
        int filasActualizadas = usuarioRepository.eliminacionLogica(idUsuario);
        if (filasActualizadas == 0) {
            throw new RuntimeException("Usuario no encontrado con ID: " + idUsuario);
        }
    }

    @Override
    public List<UsuarioDTO> encontrarPorNombre(String nombre) {
        Optional<UsuarioEntity> reExactos = usuarioRepository.findByNombreAndEstadoTrue(nombre);
        if (!reExactos.isEmpty()) {
            return reExactos.stream().map(this::convertirADTO).toList();
        } else {
            List<UsuarioEntity> parecidos = usuarioRepository.findByNombreContainingIgnoreCaseAndEstadoTrue(nombre);
            return parecidos.stream().map(this::convertirADTO).toList();
        }
    }

    @Override
    public List<UsuarioDTO> encontrarPorDocumento(String documento) {
        List<UsuarioEntity> usuarios = usuarioRepository.findByCedulaAndEstadoTrue(documento).stream().toList();
        if (usuarios.isEmpty()) {
            usuarios = usuarioRepository.findByNitAndEstadoTrue(documento).stream().toList();
        }
        if (usuarios.isEmpty()) {
            usuarios = usuarioRepository.findByCedulaContainingIgnoreCaseAndEstadoTrue(documento);
        }
        if (usuarios.isEmpty()) {
            usuarios = usuarioRepository.findByNitContainingIgnoreCaseAndEstadoTrue(documento);
        }
        if (usuarios.isEmpty()) {
            throw new RuntimeException("numero de documento no encontrado");
        }
        return usuarios.stream().map(this::convertirADTO).toList();
    }

    @Override
    public List<UsuarioDTO> encontrarPorCorreo(String correo){
        List<UsuarioEntity> buscarCorreo = usuarioRepository.findByCorreoAndEstadoTrue(correo).stream().toList();
        if (buscarCorreo.isEmpty()) {
            buscarCorreo = usuarioRepository.findByCorreoContainingIgnoreCaseAndEstadoTrue(correo);
        }
        if (buscarCorreo.isEmpty()) {
            throw new RuntimeException("Correo no encontrado");
        }

        return buscarCorreo.stream().map(this::convertirADTO).toList();
    }

    public UsuarioDTO convertirADTO(UsuarioEntity usuarioEntity) {
        return modelMapper.map(usuarioEntity, UsuarioDTO.class);
    }

    public UsuarioEditarDTO convertirAEditarUsuarioDTO(UsuarioEntity usuarioEntity) {
        return modelMapper.map(usuarioEntity, UsuarioEditarDTO.class);
    }

    // =====================================================
    //  MÉTODO AUXILIAR para filtrar usuarios reutilizable
    // =====================================================
    private List<UsuarioDTO> obtenerUsuariosFiltrados(String nombre, String correo, String documento) {
        if ((nombre == null || nombre.isEmpty()) &&
                (correo == null || correo.isEmpty()) &&
                (documento == null || documento.isEmpty())) {
            return listarUsuarios();
        }
        return usuarioRepository.findByFiltros(nombre, correo, documento)
                .stream()
                .map(this::convertirADTO)
                .toList();
    }

    // ==============================
    // Exportar usuarios a Excel
    // ==============================
    @Override
    public void exportUsuariosToExcel(String nombre, String correo, String documento, OutputStream os) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Usuarios");

        String[] headers = {"ID", "Rol ID", "Nombre", "Correo", "Cédula", "Teléfono", "NIT", "Dirección",
                "Barrio", "Localidad", "Zona de Trabajo", "Horario", "Certificaciones",
                "Cantidad Mínima", "Estado", "Fecha Creación"};

        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            headerRow.createCell(i).setCellValue(headers[i]);
        }

        List<UsuarioDTO> usuarios = obtenerUsuariosFiltrados(nombre, correo, documento);

        int rowNum = 1;
        for (UsuarioDTO usuario : usuarios) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(usuario.getIdUsuario());
            row.createCell(1).setCellValue(usuario.getRolId() != null ? usuario.getRolId() : 0);
            row.createCell(2).setCellValue(usuario.getNombre());
            row.createCell(3).setCellValue(usuario.getCorreo());
            row.createCell(4).setCellValue(usuario.getCedula());
            row.createCell(5).setCellValue(usuario.getTelefono());
            row.createCell(6).setCellValue(usuario.getNit() != null ? usuario.getNit() : "");
            row.createCell(7).setCellValue(usuario.getDireccion() != null ? usuario.getDireccion() : "");
            row.createCell(8).setCellValue(usuario.getBarrio());
            row.createCell(9).setCellValue(usuario.getLocalidad());
            row.createCell(10).setCellValue(usuario.getZona_de_trabajo() != null ? usuario.getZona_de_trabajo() : "");
            row.createCell(11).setCellValue(usuario.getHorario() != null ? usuario.getHorario() : "");
            row.createCell(12).setCellValue(usuario.getCertificaciones() != null ? usuario.getCertificaciones() : "");
            row.createCell(13).setCellValue(usuario.getCantidad_minima() != null ? usuario.getCantidad_minima() : 0);
            row.createCell(14).setCellValue(usuario.getEstado() != null && usuario.getEstado() ? "Activo" : "Inactivo");
            row.createCell(15).setCellValue(usuario.getFechaCreacion() != null ? usuario.getFechaCreacion().toString() : "");
        }

        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }

        workbook.write(os);
        workbook.close();
    }

    // ==============================
    // Exportar usuarios a PDF
    // ==============================
    @Override
    public void exportUsuariosToPDF(String nombre, String correo, String documento, OutputStream os) throws IOException, DocumentException {
        Document document = new Document(PageSize.A4.rotate());
        PdfWriter.getInstance(document, os);
        document.open();

        document.add(new Paragraph("Reporte de Usuarios"));
        document.add(new Paragraph(" "));

        String[] headers = {"ID", "Rol ID", "Nombre", "Correo", "Cédula", "Teléfono", "NIT", "Dirección",
                "Barrio", "Localidad", "Zona de Trabajo", "Horario", "Certificaciones",
                "Cantidad Mínima", "Estado", "Fecha Creación"};

        PdfPTable table = new PdfPTable(headers.length);
        table.setWidthPercentage(100);

        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header));
            cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            table.addCell(cell);
        }

        List<UsuarioDTO> usuarios = obtenerUsuariosFiltrados(nombre, correo, documento);

        for (UsuarioDTO usuario : usuarios) {
            table.addCell(usuario.getIdUsuario() != null ? usuario.getIdUsuario().toString() : "");
            table.addCell(usuario.getRolId() != null ? usuario.getRolId().toString() : "");
            table.addCell(usuario.getNombre());
            table.addCell(usuario.getCorreo());
            table.addCell(usuario.getCedula());
            table.addCell(usuario.getTelefono());
            table.addCell(usuario.getNit() != null ? usuario.getNit() : "");
            table.addCell(usuario.getDireccion() != null ? usuario.getDireccion() : "");
            table.addCell(usuario.getBarrio());
            table.addCell(usuario.getLocalidad());
            table.addCell(usuario.getZona_de_trabajo() != null ? usuario.getZona_de_trabajo() : "");
            table.addCell(usuario.getHorario() != null ? usuario.getHorario() : "");
            table.addCell(usuario.getCertificaciones() != null ? usuario.getCertificaciones() : "");
            table.addCell(usuario.getCantidad_minima() != null ? usuario.getCantidad_minima().toString() : "");
            table.addCell(usuario.getEstado() != null && usuario.getEstado() ? "Activo" : "Inactivo");
            table.addCell(usuario.getFechaCreacion() != null ? usuario.getFechaCreacion().toString() : "");
        }

        document.add(table);
        document.close();
    }
}
