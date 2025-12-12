package com.EcoSoftware.Scrum6.Service;

import com.EcoSoftware.Scrum6.DTO.RutaParadaDTO;

import java.util.List;

public interface RutaParadaService {

    RutaParadaDTO obtenerPorId(Long id);

    List<RutaParadaDTO> obtenerPorRuta(Long rutaId);

    RutaParadaDTO actualizarEstado(Long idParada, String nuevoEstado);
}
