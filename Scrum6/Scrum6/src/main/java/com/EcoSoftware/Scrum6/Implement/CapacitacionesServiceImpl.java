package com.EcoSoftware.Scrum6.Implement;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
