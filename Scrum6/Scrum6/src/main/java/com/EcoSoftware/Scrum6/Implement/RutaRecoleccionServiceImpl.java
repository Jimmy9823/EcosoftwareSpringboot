package com.EcoSoftware.Scrum6.Implement;

import com.EcoSoftware.Scrum6.Entity.RutaRecoleccionEntity;
import com.EcoSoftware.Scrum6.Repository.RutaRecoleccionRepository;
import com.EcoSoftware.Scrum6.Service.RutaRecoleccionService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RutaRecoleccionServiceImpl implements RutaRecoleccionService {

    private final RutaRecoleccionRepository rutaRepository;

    @Override
    public RutaRecoleccionEntity crearRuta(RutaRecoleccionEntity ruta) {
        return rutaRepository.save(ruta);
    }

    @Override
    public Optional<RutaRecoleccionEntity> obtenerPorId(Long id) {
        return rutaRepository.findById(id);
    }

    @Override
    public List<RutaRecoleccionEntity> listarTodas() {
        return rutaRepository.findAll();
    }

    @Override
    public List<RutaRecoleccionEntity> listarPorRecolector(Long recolectorId) {
        return rutaRepository.findByRecolector_IdUsuario(recolectorId);
    }

    @Override
    @Transactional
    public RutaRecoleccionEntity actualizarRuta(Long id, RutaRecoleccionEntity rutaActualizada) {
        RutaRecoleccionEntity existente = rutaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Ruta no encontrada con id: " + id));

        existente.setNombre(rutaActualizada.getNombre());
        existente.setDescripcion(rutaActualizada.getDescripcion());
        existente.setZonasCubiertas(rutaActualizada.getZonasCubiertas());
        // no actualizo recolector ni recolecciones aquí, para mantener consistencia

        return rutaRepository.save(existente);
    }

    @Override
    @Transactional
    public void eliminarRuta(Long id) {
        RutaRecoleccionEntity ruta = rutaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Ruta no encontrada con id: " + id));

        rutaRepository.delete(ruta);
    }
}
