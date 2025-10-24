package com.EcoSoftware.Scrum6.Repository;

import com.EcoSoftware.Scrum6.Entity.RutaRecoleccionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RutaRecoleccionRepository extends JpaRepository<RutaRecoleccionEntity, Long> {

    // Rutas creadas por un recolector
    List<RutaRecoleccionEntity> findByRecolector_IdUsuario(Long recolectorId);

    // Buscar rutas por nombre 
    List<RutaRecoleccionEntity> findByNombreContainingIgnoreCase(String nombre);
}

