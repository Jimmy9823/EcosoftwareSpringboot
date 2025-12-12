package com.EcoSoftware.Scrum6.Implement;

import com.EcoSoftware.Scrum6.DTO.RutaParadaDTO;
import com.EcoSoftware.Scrum6.Entity.RutaParadaEntity;
import com.EcoSoftware.Scrum6.Enums.EstadoParada;
import com.EcoSoftware.Scrum6.Repository.RutaParadaRepository;
import com.EcoSoftware.Scrum6.Repository.RutaRecoleccionRepository;
import com.EcoSoftware.Scrum6.Service.RutaParadaService;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RutaParadaServiceImpl implements RutaParadaService {

    private final RutaParadaRepository paradaRepo;
    private final RutaRecoleccionRepository rutaRepo;

    @Override
    public RutaParadaDTO obtenerPorId(Long id) {
        RutaParadaEntity p = paradaRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Parada no encontrada"));
        return entityToDto(p);
    }

    @Override
    public List<RutaParadaDTO> obtenerPorRuta(Long rutaId) {
        var ruta = rutaRepo.findById(rutaId)
                .orElseThrow(() -> new EntityNotFoundException("Ruta no encontrada"));
        return paradaRepo.findAllByRutaOrderByOrden(ruta)
                .stream().map(this::entityToDto).collect(Collectors.toList());
    }

    @Override
    public RutaParadaDTO actualizarEstado(Long idParada, String nuevoEstado) {
        RutaParadaEntity p = paradaRepo.findById(idParada)
                .orElseThrow(() -> new EntityNotFoundException("Parada no encontrada"));

        p.setEstado(EstadoParada.valueOf(nuevoEstado));
        paradaRepo.save(p);

        return entityToDto(p);
    }

    private RutaParadaDTO entityToDto(RutaParadaEntity p) {
        RutaParadaDTO dto = new RutaParadaDTO();
        dto.setIdParada(p.getIdParada());
        dto.setRutaId(p.getRuta().getIdRuta());
        dto.setRecoleccionId(p.getRecoleccion().getIdRecoleccion());
        dto.setOrden(p.getOrden());
        dto.setEstado(p.getEstado());
        dto.setFechaAtencion(p.getFechaAtencion());
        dto.setLatitude(p.getLatitude());
        dto.setLongitude(p.getLongitude());
        return dto;
    }
}
