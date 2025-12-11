package com.EcoSoftware.Scrum6.Implement;

import com.EcoSoftware.Scrum6.DTO.RutaParadaDTO;
import com.EcoSoftware.Scrum6.Entity.RecoleccionEntity;
import com.EcoSoftware.Scrum6.Entity.RutaParadaEntity;
import com.EcoSoftware.Scrum6.Entity.RutaRecoleccionEntity;
import com.EcoSoftware.Scrum6.Enums.EstadoParada;
import com.EcoSoftware.Scrum6.Repository.RecoleccionRepository;
import com.EcoSoftware.Scrum6.Repository.RutaParadaRepository;
import com.EcoSoftware.Scrum6.Repository.RutaRecoleccionRepository;
import com.EcoSoftware.Scrum6.Service.RutaParadaService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RutaParadaServiceImpl implements RutaParadaService {

    private final RutaParadaRepository rutaParadaRepo;
    private final RutaRecoleccionRepository rutaRepo;
    private final RecoleccionRepository recoleccionRepo;

    // -------------------------------------------------------------------
    // AGREGAR PARADA
    // -------------------------------------------------------------------
    @Override
    @Transactional
    public RutaParadaDTO agregarParada(Long rutaId, Long recoleccionId, Double lat, Double lng) {

        RutaRecoleccionEntity ruta = rutaRepo.findById(rutaId)
                .orElseThrow(() -> new RuntimeException("Ruta no encontrada"));

        RecoleccionEntity reco = recoleccionRepo.findById(recoleccionId)
                .orElseThrow(() -> new RuntimeException("Recolección no encontrada"));

        // Validar que esa recolección no esté asignada a otra ruta
        if (rutaParadaRepo.existsByRecoleccion(reco)) {
            throw new RuntimeException("La recolección ya pertenece a otra ruta");
        }

        // Obtener orden siguiente
        Integer nuevoOrden = rutaParadaRepo.countByRuta(ruta) + 1;

        RutaParadaEntity parada = new RutaParadaEntity();
        parada.setRuta(ruta);
        parada.setRecoleccion(reco);
        parada.setLatitude(lat);
        parada.setLongitude(lng);
        parada.setOrden(nuevoOrden);
        parada.setEstado(EstadoParada.Pendiente);

        rutaParadaRepo.save(parada);

        return mapToDTO(parada);
    }

    // -------------------------------------------------------------------
    // MOVER ARRIBA
    // -------------------------------------------------------------------
    @Override
    @Transactional
    public RutaParadaDTO moverArriba(Long paradaId) {

        RutaParadaEntity actual = rutaParadaRepo.findById(paradaId)
                .orElseThrow(() -> new RuntimeException("Parada no encontrada"));

        if (actual.getOrden() == 1) {
            throw new RuntimeException("La parada ya está en la primera posición");
        }

        // Buscar parada anterior
        RutaParadaEntity anterior =
                rutaParadaRepo.findByRutaAndOrden(actual.getRuta(), actual.getOrden() - 1)
                        .orElseThrow(() -> new RuntimeException("Parada anterior no encontrada"));

        // Intercambiar orden
        Integer temp = actual.getOrden();
        actual.setOrden(anterior.getOrden());
        anterior.setOrden(temp);

        rutaParadaRepo.save(actual);
        rutaParadaRepo.save(anterior);

        return mapToDTO(actual);
    }

    // -------------------------------------------------------------------
    // MOVER ABAJO
    // -------------------------------------------------------------------
    @Override
    @Transactional
    public RutaParadaDTO moverAbajo(Long paradaId) {

        RutaParadaEntity actual = rutaParadaRepo.findById(paradaId)
                .orElseThrow(() -> new RuntimeException("Parada no encontrada"));

        int total = rutaParadaRepo.countByRuta(actual.getRuta());

        if (actual.getOrden() == total) {
            throw new RuntimeException("La parada ya está en la última posición");
        }

        RutaParadaEntity siguiente =
                rutaParadaRepo.findByRutaAndOrden(actual.getRuta(), actual.getOrden() + 1)
                        .orElseThrow(() -> new RuntimeException("Parada siguiente no encontrada"));

        // Swap
        Integer temp = actual.getOrden();
        actual.setOrden(siguiente.getOrden());
        siguiente.setOrden(temp);

        rutaParadaRepo.save(actual);
        rutaParadaRepo.save(siguiente);

        return mapToDTO(actual);
    }

    // -------------------------------------------------------------------
    // RECALCULAR ORDEN
    // -------------------------------------------------------------------
    @Override
    @Transactional
    public void recalcularOrden(Long rutaId) {

        RutaRecoleccionEntity ruta = rutaRepo.findById(rutaId)
                .orElseThrow(() -> new RuntimeException("Ruta no encontrada"));

        List<RutaParadaEntity> paradas =
                rutaParadaRepo.findAllByRutaOrderByOrden(ruta);

        int index = 1;
        for (RutaParadaEntity p : paradas) {
            p.setOrden(index++);
        }

        rutaParadaRepo.saveAll(paradas);
    }

    // -------------------------------------------------------------------
    // LISTAR PARADAS
    // -------------------------------------------------------------------
    @Override
    public List<RutaParadaDTO> listarParadas(Long rutaId) {

        RutaRecoleccionEntity ruta = rutaRepo.findById(rutaId)
                .orElseThrow(() -> new RuntimeException("Ruta no encontrada"));

        return rutaParadaRepo.findAllByRutaOrderByOrden(ruta)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // -------------------------------------------------------------------
    // ACTUALIZAR ESTADO
    // -------------------------------------------------------------------
    @Override
    @Transactional
    public RutaParadaDTO actualizarEstado(Long paradaId, EstadoParada nuevoEstado) {

        RutaParadaEntity parada = rutaParadaRepo.findById(paradaId)
                .orElseThrow(() -> new RuntimeException("Parada no encontrada"));

        parada.setEstado(nuevoEstado);
        rutaParadaRepo.save(parada);

        return mapToDTO(parada);
    }

    // -------------------------------------------------------------------
    // MAPEO ENTITY → DTO
    // -------------------------------------------------------------------
    private RutaParadaDTO mapToDTO(RutaParadaEntity p) {
        RutaParadaDTO dto = new RutaParadaDTO();

        dto.setIdParada(p.getIdParada());
        dto.setRutaId(p.getRuta().getIdRuta());
        dto.setRecoleccionId(p.getRecoleccion().getIdRecoleccion());
        dto.setOrden(p.getOrden());
        dto.setLatitude(p.getLatitude());
        dto.setLongitude(p.getLongitude());
        dto.setEstado(p.getEstado());
        dto.setFechaAtencion(p.getFechaAtencion());

        return dto;
    }
}
