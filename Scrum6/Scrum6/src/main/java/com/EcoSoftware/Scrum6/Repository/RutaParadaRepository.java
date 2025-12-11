package com.EcoSoftware.Scrum6.Repository;

import com.EcoSoftware.Scrum6.Entity.RutaParadaEntity;
import com.EcoSoftware.Scrum6.Entity.RutaRecoleccionEntity;
import com.EcoSoftware.Scrum6.Entity.RecoleccionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RutaParadaRepository extends JpaRepository<RutaParadaEntity, Long> {

    Integer countByRuta(RutaRecoleccionEntity ruta);

    boolean existsByRecoleccion(RecoleccionEntity recoleccion);
    boolean existsByRecoleccion_IdRecoleccion(Long idRecoleccion);

    List<RutaParadaEntity> findAllByRutaOrderByOrden(RutaRecoleccionEntity ruta);

    // Métodos necesarios para el service
    List<RutaParadaEntity> findByRuta_IdRutaOrderByOrdenAsc(Long rutaId);

    Optional<RutaParadaEntity> findTopByRuta_IdRutaOrderByOrdenDesc(Long rutaId);

    Optional<RutaParadaEntity> findByRutaAndOrden(RutaRecoleccionEntity ruta, Integer orden);

    Optional<RutaParadaEntity> findByIdParada(Long idParada);
}
