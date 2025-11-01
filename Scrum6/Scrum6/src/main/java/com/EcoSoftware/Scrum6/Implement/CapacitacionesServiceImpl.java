package com.EcoSoftware.Scrum6.Implement;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.EcoSoftware.Scrum6.DTO.CapacitacionesDTO.CapacitacionDTO;
import com.EcoSoftware.Scrum6.DTO.CapacitacionesDTO.InscripcionDTO;
import com.EcoSoftware.Scrum6.DTO.CapacitacionesDTO.ModuloDTO;
import com.EcoSoftware.Scrum6.DTO.CapacitacionesDTO.ProgresoDTO;
import com.EcoSoftware.Scrum6.Entity.CapacitacionEntity;
import com.EcoSoftware.Scrum6.Entity.InscripcionEntity;
import com.EcoSoftware.Scrum6.Entity.ModuloEntity;
import com.EcoSoftware.Scrum6.Entity.ProgresoEntity;
import com.EcoSoftware.Scrum6.Entity.UsuarioEntity;
import com.EcoSoftware.Scrum6.Enums.EstadoCurso;
import com.EcoSoftware.Scrum6.Repository.CapacitacionRepository;
import com.EcoSoftware.Scrum6.Repository.InscripcionRepository;
import com.EcoSoftware.Scrum6.Repository.ModuloRepository;
import com.EcoSoftware.Scrum6.Repository.ProgresoRepository;
import com.EcoSoftware.Scrum6.Repository.UsuarioRepository;
import com.EcoSoftware.Scrum6.Service.CapacitacionesService;

@Service
@Transactional
public class CapacitacionesServiceImpl implements CapacitacionesService {

    @Autowired
    private CapacitacionRepository capacitacionRepository;

    @Autowired
    private ModuloRepository moduloRepository;

    @Autowired
    private InscripcionRepository inscripcionRepository;

    @Autowired
    private ProgresoRepository progresoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    /**
     * Carga masiva de capacitaciones desde un archivo Excel.
     * Solo crea nuevas capacitaciones, no actualiza las existentes.
     */
    @Override
    public void cargarCapacitacionesDesdeExcel(MultipartFile file) {
        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rows = sheet.iterator();
            List<CapacitacionEntity> capacitaciones = new ArrayList<>();

            // Saltar la primera fila (cabeceras)
            if (rows.hasNext()) {
                rows.next();
            }

            while (rows.hasNext()) {
                Row row = rows.next();

                // Leer columnas (en orden)
                String nombre = getCellValue(row.getCell(0));
                String descripcion = getCellValue(row.getCell(1));
                String numeroDeClases = getCellValue(row.getCell(2));
                String duracion = getCellValue(row.getCell(3));
                String imagen = getCellValue(row.getCell(4));

                // Validación básica: no agregar filas vacías
                if (nombre == null || nombre.isBlank())
                    continue;

                CapacitacionEntity c = new CapacitacionEntity();
                c.setNombre(nombre);
                c.setDescripcion(descripcion);
                c.setNumeroDeClases(numeroDeClases);
                c.setDuracion(duracion);
                c.setImagen(imagen);

                capacitaciones.add(c);
            }

            capacitacionRepository.saveAll(capacitaciones);

        } catch (IOException e) {
            throw new RuntimeException("Error al procesar el archivo Excel", e);
        }
    }

    /**
     * Genera una plantilla Excel con los campos requeridos para la carga masiva.
     * Devuelve un arreglo de bytes que se puede descargar desde el frontend.
     */
    @Override
    public byte[] generarPlantillaExcel() {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Plantilla Capacitaciones");
            Row header = sheet.createRow(0);

            // Encabezados
            header.createCell(0).setCellValue("Nombre");
            header.createCell(1).setCellValue("Descripción");
            header.createCell(2).setCellValue("Número de Clases");
            header.createCell(3).setCellValue("Duración");
            header.createCell(4).setCellValue("Imagen");

            // Ajustar tamaño de columnas
            for (int i = 0; i <= 4; i++) {
                sheet.autoSizeColumn(i);
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            return out.toByteArray();

        } catch (IOException e) {
            throw new RuntimeException("Error al generar la plantilla Excel", e);
        }
    }

    /**
     * Método auxiliar para leer valores de celda como String.
     */
    private String getCellValue(Cell cell) {
        if (cell == null)
            return null;
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue().trim();
            case NUMERIC -> String.valueOf((int) cell.getNumericCellValue());
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            default -> null;
        };
    }

    // ===========================
    // CAPACITACION
    // ===========================
    @Override
    public CapacitacionDTO crearCapacitacion(CapacitacionDTO dto) {
        CapacitacionEntity entidad = new CapacitacionEntity();
        entidad.setNombre(dto.getNombre());
        entidad.setDescripcion(dto.getDescripcion());
        entidad.setNumeroDeClases(dto.getNumeroDeClases());
        entidad.setDuracion(dto.getDuracion());
        CapacitacionEntity saved = capacitacionRepository.save(entidad);
        dto.setId(saved.getId());
        return dto;
    }

    @Override
    public CapacitacionDTO actualizarCapacitacion(Long id, CapacitacionDTO dto) {
        CapacitacionEntity entidad = capacitacionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Capacitación no encontrada"));
        entidad.setNombre(dto.getNombre());
        entidad.setDescripcion(dto.getDescripcion());
        entidad.setNumeroDeClases(dto.getNumeroDeClases());
        entidad.setDuracion(dto.getDuracion());
        capacitacionRepository.save(entidad);
        dto.setId(entidad.getId());
        return dto;
    }

    @Override
    public void eliminarCapacitacion(Long id) {
        capacitacionRepository.deleteById(id);
    }

    @Override
    public CapacitacionDTO obtenerCapacitacionPorId(Long id) {
        CapacitacionEntity entidad = capacitacionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Capacitación no encontrada"));
        CapacitacionDTO dto = new CapacitacionDTO();
        dto.setId(entidad.getId());
        dto.setNombre(entidad.getNombre());
        dto.setDescripcion(entidad.getDescripcion());
        dto.setNumeroDeClases(entidad.getNumeroDeClases());
        dto.setDuracion(entidad.getDuracion());
        return dto;
    }

    @Override
    public List<CapacitacionDTO> listarTodasCapacitaciones() {
        return capacitacionRepository.findAll().stream().map(entidad -> {
            CapacitacionDTO dto = new CapacitacionDTO();
            dto.setId(entidad.getId());
            dto.setNombre(entidad.getNombre());
            dto.setDescripcion(entidad.getDescripcion());
            dto.setNumeroDeClases(entidad.getNumeroDeClases());
            dto.setDuracion(entidad.getDuracion());
            return dto;
        }).collect(Collectors.toList());
    }

    // ===========================
    // MODULO
    // ===========================
    @Override
    public ModuloDTO crearModulo(ModuloDTO dto) {
        ModuloEntity entidad = new ModuloEntity();
        entidad.setDescripcion(dto.getDescripcion());
        entidad.setDuracion(dto.getDuracion());

        CapacitacionEntity curso = capacitacionRepository.findById(dto.getCapacitacionId())
                .orElseThrow(() -> new RuntimeException("Capacitación no encontrada"));
        entidad.setCapacitacion(curso);

        ModuloEntity saved = moduloRepository.save(entidad);
        dto.setId(saved.getId());
        return dto;
    }

    @Override
    public ModuloDTO actualizarModulo(Long id, ModuloDTO dto) {
        ModuloEntity entidad = moduloRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Módulo no encontrado"));
        entidad.setDescripcion(dto.getDescripcion());
        entidad.setDuracion(dto.getDuracion());
        moduloRepository.save(entidad);
        dto.setId(entidad.getId());
        return dto;
    }

    @Override
    public void eliminarModulo(Long id) {
        moduloRepository.deleteById(id);
    }

    @Override
    public List<ModuloDTO> listarModulosPorCapacitacion(Long capacitacionId) {
        return moduloRepository.findByCapacitacionId(capacitacionId).stream().map(entidad -> {
            ModuloDTO dto = new ModuloDTO();
            dto.setId(entidad.getId());
            dto.setDescripcion(entidad.getDescripcion());
            dto.setDuracion(entidad.getDuracion());
            dto.setCapacitacionId(entidad.getCapacitacion().getId());
            return dto;
        }).collect(Collectors.toList());
    }

    // ===========================
    // CARGA MASIVA DE MÓDULOS
    // ===========================
    @Override
    public byte[] generarPlantillaModulosExcel() {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Plantilla Módulos");
            Row header = sheet.createRow(0);

            header.createCell(0).setCellValue("Duración");
            header.createCell(1).setCellValue("Descripción");

            for (int i = 0; i <= 1; i++) {
                sheet.autoSizeColumn(i);
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            return out.toByteArray();

        } catch (IOException e) {
            throw new RuntimeException("Error al generar la plantilla Excel de módulos", e);
        }
    }

    // ===========================
    // CARGA MASIVA DE MÓDULOS
    // ===========================
    @Override
    public void cargarModulosDesdeExcel(Long capacitacionId, MultipartFile file) {
        CapacitacionEntity capacitacion = capacitacionRepository.findById(capacitacionId)
                .orElseThrow(() -> new RuntimeException("Capacitación no encontrada"));

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rows = sheet.iterator();
            List<ModuloEntity> modulos = new ArrayList<>();

            if (rows.hasNext())
                rows.next(); // Saltar encabezado

            while (rows.hasNext()) {
                Row row = rows.next();

                String duracion = getCellValue(row.getCell(0));
                String descripcion = getCellValue(row.getCell(1));

                if ((duracion == null || duracion.isBlank()) &&
                        (descripcion == null || descripcion.isBlank()))
                    continue;

                ModuloEntity m = new ModuloEntity();
                m.setDuracion(duracion);
                m.setDescripcion(descripcion);
                m.setCapacitacion(capacitacion);

                modulos.add(m);
            }

            moduloRepository.saveAll(modulos);

        } catch (IOException e) {
            throw new RuntimeException("Error al procesar el archivo Excel de módulos", e);
        }
    }

    // ===========================
    // INSCRIPCION
    // ===========================
    @Override
    public InscripcionDTO inscribirse(Long usuarioId, Long cursoId) {
        UsuarioEntity usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        CapacitacionEntity curso = capacitacionRepository.findById(cursoId)
                .orElseThrow(() -> new RuntimeException("Capacitación no encontrada"));

        InscripcionEntity entidad = new InscripcionEntity();
        entidad.setUsuario(usuario);
        entidad.setCurso(curso);
        entidad.setFechaDeInscripcion(LocalDate.now());
        entidad.setEstadoCurso(EstadoCurso.Inscrito);

        InscripcionEntity saved = inscripcionRepository.save(entidad);

        InscripcionDTO dto = new InscripcionDTO();
        dto.setId(saved.getId());
        dto.setCursoId(curso.getId());
        dto.setUsuarioId(usuario.getIdUsuario());
        dto.setEstadoCurso(saved.getEstadoCurso());
        dto.setFechaDeInscripcion(saved.getFechaDeInscripcion());
        return dto;
    }

    @Override
    public InscripcionDTO actualizarEstadoInscripcion(Long id, EstadoCurso estadoCurso) {
        InscripcionEntity entidad = inscripcionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Inscripción no encontrada"));
        entidad.setEstadoCurso(estadoCurso);
        inscripcionRepository.save(entidad);

        InscripcionDTO dto = new InscripcionDTO();
        dto.setId(entidad.getId());
        dto.setCursoId(entidad.getCurso().getId());
        dto.setUsuarioId(entidad.getUsuario().getIdUsuario());
        dto.setEstadoCurso(entidad.getEstadoCurso());
        dto.setFechaDeInscripcion(entidad.getFechaDeInscripcion());
        return dto;
    }

    @Override
    public List<InscripcionDTO> listarInscripcionesPorUsuario(Long usuarioId) {
        return inscripcionRepository.findByUsuario_IdUsuario(usuarioId).stream().map(entidad -> {
            InscripcionDTO dto = new InscripcionDTO();
            dto.setId(entidad.getId());
            dto.setCursoId(entidad.getCurso().getId());
            dto.setUsuarioId(entidad.getUsuario().getIdUsuario());
            dto.setEstadoCurso(entidad.getEstadoCurso());
            dto.setFechaDeInscripcion(entidad.getFechaDeInscripcion());
            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    public List<InscripcionDTO> listarInscripcionesPorCurso(Long cursoId) {
        return inscripcionRepository.findByCursoId(cursoId).stream().map(entidad -> {
            InscripcionDTO dto = new InscripcionDTO();
            dto.setId(entidad.getId());
            dto.setCursoId(entidad.getCurso().getId());
            dto.setUsuarioId(entidad.getUsuario().getIdUsuario());
            dto.setEstadoCurso(entidad.getEstadoCurso());
            dto.setFechaDeInscripcion(entidad.getFechaDeInscripcion());
            return dto;
        }).collect(Collectors.toList());
    }

    // ===========================
    // PROGRESO
    // ===========================
    @Override
    public ProgresoDTO registrarProgreso(ProgresoDTO dto) {
        UsuarioEntity usuario = usuarioRepository.findById(dto.getUsuarioId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        CapacitacionEntity curso = capacitacionRepository.findById(dto.getCursoId())
                .orElseThrow(() -> new RuntimeException("Capacitación no encontrada"));

        ProgresoEntity entidad = new ProgresoEntity();
        entidad.setUsuario(usuario);
        entidad.setCurso(curso);
        entidad.setProgresoDelCurso(dto.getProgresoDelCurso());
        entidad.setModulosCompletados(dto.getModulosCompletados());
        entidad.setTiempoInvertido(dto.getTiempoInvertido());

        ProgresoEntity saved = progresoRepository.save(entidad);
        dto.setId(saved.getId());
        return dto;
    }

    @Override
    public ProgresoDTO actualizarProgreso(Long id, ProgresoDTO dto) {
        ProgresoEntity entidad = progresoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Progreso no encontrado"));
        entidad.setProgresoDelCurso(dto.getProgresoDelCurso());
        entidad.setModulosCompletados(dto.getModulosCompletados());
        entidad.setTiempoInvertido(dto.getTiempoInvertido());
        progresoRepository.save(entidad);
        dto.setId(entidad.getId());
        return dto;
    }

    @Override
    public List<ProgresoDTO> listarProgresosPorUsuario(Long usuarioId) {
        return progresoRepository.findByUsuario_IdUsuario(usuarioId).stream().map(entidad -> {
            ProgresoDTO dto = new ProgresoDTO();
            dto.setId(entidad.getId());
            dto.setCursoId(entidad.getCurso().getId());
            dto.setUsuarioId(entidad.getUsuario().getIdUsuario());
            dto.setProgresoDelCurso(entidad.getProgresoDelCurso());
            dto.setModulosCompletados(entidad.getModulosCompletados());
            dto.setTiempoInvertido(entidad.getTiempoInvertido());
            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    public List<ProgresoDTO> listarProgresosPorCurso(Long cursoId) {
        return progresoRepository.findByCursoId(cursoId).stream().map(entidad -> {
            ProgresoDTO dto = new ProgresoDTO();
            dto.setId(entidad.getId());
            dto.setCursoId(entidad.getCurso().getId());
            dto.setUsuarioId(entidad.getUsuario().getIdUsuario());
            dto.setProgresoDelCurso(entidad.getProgresoDelCurso());
            dto.setModulosCompletados(entidad.getModulosCompletados());
            dto.setTiempoInvertido(entidad.getTiempoInvertido());
            return dto;
        }).collect(Collectors.toList());
    }
}
