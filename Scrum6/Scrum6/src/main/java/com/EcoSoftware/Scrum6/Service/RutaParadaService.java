package com.EcoSoftware.Scrum6.Service;

import com.EcoSoftware.Scrum6.DTO.RutaParadaDTO;
import com.EcoSoftware.Scrum6.Enums.EstadoParada;
import java.util.List;

public interface RutaParadaService {

    RutaParadaDTO agregarParada(Long rutaId, Long recoleccionId, Double lat, Double lng);

    RutaParadaDTO moverArriba(Long paradaId);

    RutaParadaDTO moverAbajo(Long paradaId);

    void recalcularOrden(Long rutaId);

    List<RutaParadaDTO> listarParadas(Long rutaId);

    RutaParadaDTO actualizarEstado(Long paradaId, EstadoParada nuevoEstado);
}

