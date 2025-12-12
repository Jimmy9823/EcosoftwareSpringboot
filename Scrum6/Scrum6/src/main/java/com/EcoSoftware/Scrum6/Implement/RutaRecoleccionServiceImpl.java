package com.EcoSoftware.Scrum6.Implement;

import com.EcoSoftware.Scrum6.DTO.CrearRutaDTO;
import com.EcoSoftware.Scrum6.DTO.RutaParadaDTO;
import com.EcoSoftware.Scrum6.DTO.RutaRecoleccionDTO;
import com.EcoSoftware.Scrum6.Entity.RecoleccionEntity;
import com.EcoSoftware.Scrum6.Entity.RutaParadaEntity;
import com.EcoSoftware.Scrum6.Entity.RutaRecoleccionEntity;
import com.EcoSoftware.Scrum6.Entity.SolicitudRecoleccionEntity;
import com.EcoSoftware.Scrum6.Entity.UsuarioEntity;
import com.EcoSoftware.Scrum6.Enums.EstadoParada;
import com.EcoSoftware.Scrum6.Repository.RecoleccionRepository;
import com.EcoSoftware.Scrum6.Repository.RutaParadaRepository;
import com.EcoSoftware.Scrum6.Repository.RutaRecoleccionRepository;
import com.EcoSoftware.Scrum6.Repository.UsuarioRepository;
import com.EcoSoftware.Scrum6.Service.RutaRecoleccionService;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RutaRecoleccionServiceImpl implements RutaRecoleccionService {

    private final RutaRecoleccionRepository rutaRepo;
    private final RutaParadaRepository paradaRepo;
    private final RecoleccionRepository recoleccionRepo;
    private final UsuarioRepository usuarioRepo;

    // ---------- crear ruta simple (mantener compatibilidad) ----------
    @Override
    @Transactional
    public RutaRecoleccionDTO crearRuta(RutaRecoleccionDTO rutaDTO) {
        RutaRecoleccionEntity ruta = dtoToEntity(rutaDTO);
        UsuarioEntity usuario = obtenerUsuarioAutenticado();
        ruta.setRecolector(usuario);
        RutaRecoleccionEntity saved = rutaRepo.save(ruta);
        return entityToDto(saved);
    }

    // ---------- crear ruta completa: crear ruta + asignar recolecciones + crear paradas ----------
    @Override
    @Transactional
    public RutaRecoleccionDTO crearRutaCompleta(CrearRutaDTO dto) {
        UsuarioEntity usuario = obtenerUsuarioAutenticado();

        // 1) crear la ruta
        RutaRecoleccionEntity ruta = new RutaRecoleccionEntity();
        ruta.setNombre(dto.getNombre());
        ruta.setDescripcion(dto.getDescripcion());
        ruta.setZonasCubiertas(dto.getZonasCubiertas());
        ruta.setRecolector(usuario);
        ruta.setParadas(new ArrayList<>());
        ruta = rutaRepo.save(ruta);

        // 2) obtener las recolecciones solicitadas
        List<RecoleccionEntity> recolecciones = recoleccionRepo.findAllById(dto.getRecoleccionesSeleccionadas());
        if (recolecciones.isEmpty()) {
            throw new IllegalArgumentException("No se encontraron recolecciones válidas.");
        }

        // validar y filtrar: pertenecen al mismo recolector, no canceladas y sin ruta
        List<RecoleccionEntity> validas = new ArrayList<>();
        for (RecoleccionEntity r : recolecciones) {
            if (!Objects.equals(r.getRecolector().getIdUsuario(), usuario.getIdUsuario())) {
                throw new IllegalStateException("La recolección " + r.getIdRecoleccion() + " no pertenece al recolector autenticado.");
            }
            if (r.getEstado() == null) {
                throw new IllegalStateException("La recolección " + r.getIdRecoleccion() + " tiene estado inválido.");
            }
            // No permitir canceladas
            try {
                // EstadoCancelado comparacion por enum name
                if (r.getEstado().name().equals("Cancelada")) {
                    throw new IllegalStateException("La recolección " + r.getIdRecoleccion() + " está cancelada.");
                }
            } catch (Exception ignore) {}

            if (r.getRuta() != null) {
                throw new IllegalStateException("La recolección " + r.getIdRecoleccion() + " ya está asignada a otra ruta.");
            }
            validas.add(r);
        }

        // 3) calcular orden: si viene idRecoleccionInicio, esa será orden 1
        Long inicioId = dto.getIdRecoleccionInicio();
        int orden = 1;
        // first put inicio if provided
        List<RecoleccionEntity> ordenadas = new ArrayList<>();

        if (inicioId != null) {
            Optional<RecoleccionEntity> inicioOpt = validas.stream()
                    .filter(x -> Objects.equals(x.getIdRecoleccion(), inicioId))
                    .findFirst();
            if (inicioOpt.isEmpty()) {
                throw new IllegalArgumentException("La recolección de inicio indicada no está dentro de las seleccionadas.");
            }
            ordenadas.add(inicioOpt.get());
        }

        // add remaining excluding the inicio
        validas.stream()
                .filter(r -> inicioId == null || !Objects.equals(r.getIdRecoleccion(), inicioId))
                .forEach(ordenadas::add);

        // 4) asignar ruta, orden y crear paradas
        for (RecoleccionEntity r : ordenadas) {
            r.setRuta(ruta);
            r.setOrdenParada(orden++);
            recoleccionRepo.save(r);

            // obtener lat/lng de la solicitud (puede ser null)
            Double lat = null;
            Double lng = null;
            SolicitudRecoleccionEntity sol = r.getSolicitud();
            if (sol != null) {
                if (sol.getLatitude() != null) lat = sol.getLatitude().doubleValue();
                if (sol.getLongitude() != null) lng = sol.getLongitude().doubleValue();
            }

            RutaParadaEntity parada = new RutaParadaEntity();
            parada.setRuta(ruta);
            parada.setRecoleccion(r);
            parada.setOrden(r.getOrdenParada());
            parada.setEstado(EstadoParada.Pendiente);
            // si lat/lng no disponibles, colocamos 0.0 (frontend puede pedir geocodificación)
            parada.setLatitude(lat != null ? lat : 0.0);
            parada.setLongitude(lng != null ? lng : 0.0);

            parada = paradaRepo.save(parada);
            ruta.getParadas().add(parada);
        }

        ruta = rutaRepo.save(ruta);
        return entityToDto(ruta);
    }

    @Override
    public Optional<RutaRecoleccionDTO> obtenerPorId(Long id) {
        return rutaRepo.findById(id).map(this::entityToDto);
    }

    @Override
    public List<RutaRecoleccionDTO> listarTodas() {
        return rutaRepo.findAll().stream().map(this::entityToDto).collect(Collectors.toList());
    }

    @Override
    public List<RutaRecoleccionDTO> listarPorRecolector(Long recolectorId) {
        return rutaRepo.findByRecolector_IdUsuario(recolectorId).stream()
                .map(this::entityToDto).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void eliminarRuta(Long id) {
        RutaRecoleccionEntity ruta = rutaRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Ruta no encontrada"));
        // antes de borrar, limpiar relación en recolecciones
        ruta.getParadas().forEach(p -> {
            RecoleccionEntity r = p.getRecoleccion();
            if (r != null) {
                r.setRuta(null);
                r.setOrdenParada(null);
                recoleccionRepo.save(r);
            }
        });
        rutaRepo.delete(ruta);
    }

    // ---------- utilitarios: mapeos y helper ----------
    private UsuarioEntity obtenerUsuarioAutenticado() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String correo = auth.getName();
        return usuarioRepo.findByCorreo(correo)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    private RutaRecoleccionDTO entityToDto(RutaRecoleccionEntity ruta) {
        RutaRecoleccionDTO dto = new RutaRecoleccionDTO();
        dto.setIdRuta(ruta.getIdRuta());
        dto.setNombre(ruta.getNombre());
        dto.setDescripcion(ruta.getDescripcion());
        dto.setZonasCubiertas(ruta.getZonasCubiertas());
        dto.setFechaCreacion(ruta.getFechaCreacion());
        dto.setRecolectorId(ruta.getRecolector() != null ? ruta.getRecolector().getIdUsuario() : null);

        // mapear paradas
        if (ruta.getParadas() != null) {
            List<RutaParadaDTO> paradas = ruta.getParadas().stream()
                    .sorted(Comparator.comparingInt(RutaParadaEntity::getOrden))
                    .map(p -> {
                        RutaParadaDTO pd = new RutaParadaDTO();
                        pd.setIdParada(p.getIdParada());
                        pd.setRutaId(p.getRuta() != null ? p.getRuta().getIdRuta() : null);
                        pd.setRecoleccionId(p.getRecoleccion() != null ? p.getRecoleccion().getIdRecoleccion() : null);
                        pd.setOrden(p.getOrden());
                        pd.setLatitude(p.getLatitude());
                        pd.setLongitude(p.getLongitude());
                        pd.setEstado(p.getEstado());
                        pd.setFechaAtencion(p.getFechaAtencion());
                        return pd;
                    })
                    .collect(Collectors.toList());
            dto.setParadas(paradas);
        } else {
            dto.setParadas(Collections.emptyList());
        }

        return dto;
    }

    private RutaRecoleccionEntity dtoToEntity(RutaRecoleccionDTO dto) {
        RutaRecoleccionEntity e = new RutaRecoleccionEntity();
        e.setNombre(dto.getNombre());
        e.setDescripcion(dto.getDescripcion());
        e.setZonasCubiertas(dto.getZonasCubiertas());
        return e;
    }
}
