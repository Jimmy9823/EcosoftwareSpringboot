package com.EcoSoftware.Scrum6.Service;

import com.EcoSoftware.Scrum6.DTO.RutaRecoleccionDTO;
import com.EcoSoftware.Scrum6.DTO.CrearRutaDTO;

import java.util.List;
import java.util.Optional;

public interface RutaRecoleccionService {
    RutaRecoleccionDTO crearRuta(RutaRecoleccionDTO rutaDTO);
    RutaRecoleccionDTO crearRutaCompleta(CrearRutaDTO dto);
    Optional<RutaRecoleccionDTO> obtenerPorId(Long id);
    List<RutaRecoleccionDTO> listarTodas();
    List<RutaRecoleccionDTO> listarPorRecolector(Long recolectorId);
    void eliminarRuta(Long id);
}
