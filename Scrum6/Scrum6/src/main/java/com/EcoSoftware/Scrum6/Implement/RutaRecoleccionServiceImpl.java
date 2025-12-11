package com.EcoSoftware.Scrum6.Implement;

import com.EcoSoftware.Scrum6.DTO.RutaRecoleccionDTO;
import com.EcoSoftware.Scrum6.Entity.RecoleccionEntity;
import com.EcoSoftware.Scrum6.Entity.RutaRecoleccionEntity;
import com.EcoSoftware.Scrum6.Entity.UsuarioEntity;
import com.EcoSoftware.Scrum6.Enums.EstadoRecoleccion;
import com.EcoSoftware.Scrum6.Repository.RecoleccionRepository;
import com.EcoSoftware.Scrum6.Repository.RutaRecoleccionRepository;
import com.EcoSoftware.Scrum6.Repository.UsuarioRepository;
import com.EcoSoftware.Scrum6.Service.RutaRecoleccionService;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RutaRecoleccionServiceImpl implements RutaRecoleccionService {

    private final RutaRecoleccionRepository rutaRepository;
    private final RecoleccionRepository recoleccionRepository;
    private final UsuarioRepository usuarioRepository;

    // ---------------- CREAR RUTA ----------------
    @Override
    @Transactional
    public RutaRecoleccionDTO crearRuta(RutaRecoleccionDTO rutaDTO) {
        RutaRecoleccionEntity ruta = dtoToEntity(rutaDTO);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String correoUsuario = auth.getName();

        UsuarioEntity usuario = usuarioRepository.findByCorreo(correoUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        ruta.setRecolector(usuario);
        RutaRecoleccionEntity saved = rutaRepository.save(ruta);

        return entityToDto(saved);
    }

    // ---------------- OBTENER POR ID ----------------
    @Override
    public Optional<RutaRecoleccionDTO> obtenerPorId(Long id) {
        return rutaRepository.findById(id)
                .map(this::entityToDto);
    }

    // ---------------- LISTAR TODAS ----------------
    @Override
    public List<RutaRecoleccionDTO> listarTodas() {
        return rutaRepository.findAll().stream()
                .map(this::entityToDto)
                .collect(Collectors.toList());
    }

    // ---------------- LISTAR POR RECOLECTOR ----------------
    @Override
    public List<RutaRecoleccionDTO> listarPorRecolector(Long recolectorId) {
        return rutaRepository.findByRecolector_IdUsuario(recolectorId)
                .stream()
                .map(this::entityToDto)
                .collect(Collectors.toList());
    }

    // ---------------- ASIGNAR RECOLECCIONES ----------------
    @Override
    @Transactional
    public RutaRecoleccionDTO asignarRecolecciones(Long rutaId, List<Long> recoleccionIds) {
        RutaRecoleccionEntity ruta = rutaRepository.findById(rutaId)
                .orElseThrow(() -> new EntityNotFoundException("Ruta no encontrada"));

        List<RecoleccionEntity> recolecciones = recoleccionRepository.findAllById(recoleccionIds);

        if (recolecciones.isEmpty()) {
            throw new IllegalArgumentException("No existen recolecciones válidas en la lista");
        }

        for (RecoleccionEntity r : recolecciones) {
            if (r.getRuta() != null) {
                throw new IllegalStateException("La recolección " + r.getIdRecoleccion() + " ya pertenece a una ruta");
            }
            r.setRuta(ruta);
        }

        recoleccionRepository.saveAll(recolecciones);
        ruta.getRecolecciones().addAll(recolecciones);

        return entityToDto(rutaRepository.save(ruta));
    }

    // ---------------- ESTABLECER PUNTO DE INICIO ----------------
    @Override
    @Transactional
    public RutaRecoleccionDTO establecerPuntoInicio(Long rutaId, Long idRecoleccionInicio) {
        RutaRecoleccionEntity ruta = rutaRepository.findById(rutaId)
                .orElseThrow(() -> new EntityNotFoundException("Ruta no encontrada"));

        RecoleccionEntity recoInicio = recoleccionRepository.findById(idRecoleccionInicio)
                .orElseThrow(() -> new EntityNotFoundException("Recolección no encontrada"));

        ruta.getRecolecciones().forEach(r -> r.setOrdenParada(null));
        recoInicio.setOrdenParada(1);
        recoleccionRepository.save(recoInicio);

        return entityToDto(ruta);
    }

    // ---------------- ACTUALIZAR ESTADO ----------------
    @Override
    @Transactional
    public RutaRecoleccionDTO actualizarEstadoEnRuta(Long rutaId, Long recoleccionId, String nuevoEstado) {
        RutaRecoleccionEntity ruta = rutaRepository.findById(rutaId)
                .orElseThrow(() -> new EntityNotFoundException("Ruta no encontrada"));

        RecoleccionEntity reco = recoleccionRepository.findById(recoleccionId)
                .orElseThrow(() -> new EntityNotFoundException("Recolección no encontrada"));

        if (!rutaId.equals(reco.getRuta().getIdRuta())) {
            throw new IllegalStateException("La recolección no pertenece a esta ruta");
        }

        EstadoRecoleccion estadoEnum;
        try {
            estadoEnum = EstadoRecoleccion.valueOf(nuevoEstado);
        } catch (Exception e) {
            throw new IllegalArgumentException("Estado inválido: " + nuevoEstado);
        }

        reco.setEstado(estadoEnum);
        recoleccionRepository.save(reco);

        return entityToDto(ruta);
    }

    // ---------------- ACTUALIZAR RUTA ----------------
    @Override
    @Transactional
    public RutaRecoleccionDTO actualizarRuta(Long id, RutaRecoleccionDTO rutaDTO) {
        RutaRecoleccionEntity ruta = rutaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Ruta no encontrada con id: " + id));

        ruta.setNombre(rutaDTO.getNombre());
        ruta.setDescripcion(rutaDTO.getDescripcion());
        ruta.setZonasCubiertas(rutaDTO.getZonasCubiertas());

        return entityToDto(rutaRepository.save(ruta));
    }

    // ---------------- ELIMINAR RUTA ----------------
    @Override
    @Transactional
    public void eliminarRuta(Long id) {
        RutaRecoleccionEntity ruta = rutaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Ruta no encontrada con id: " + id));
        rutaRepository.delete(ruta);
    }

    // ---------------- MAPPERS ----------------
    private RutaRecoleccionDTO entityToDto(RutaRecoleccionEntity ruta) {
        RutaRecoleccionDTO dto = new RutaRecoleccionDTO();
        dto.setIdRuta(ruta.getIdRuta());
        dto.setNombre(ruta.getNombre());
        dto.setDescripcion(ruta.getDescripcion());
        dto.setZonasCubiertas(ruta.getZonasCubiertas());
        dto.setFechaCreacion(ruta.getFechaCreacion());
        dto.setRecolectorId(ruta.getRecolector() != null ? ruta.getRecolector().getIdUsuario() : null);
        return dto;
    }

    private RutaRecoleccionEntity dtoToEntity(RutaRecoleccionDTO dto) {
        RutaRecoleccionEntity ruta = new RutaRecoleccionEntity();
        ruta.setNombre(dto.getNombre());
        ruta.setDescripcion(dto.getDescripcion());
        ruta.setZonasCubiertas(dto.getZonasCubiertas());
        return ruta;
    }
}
