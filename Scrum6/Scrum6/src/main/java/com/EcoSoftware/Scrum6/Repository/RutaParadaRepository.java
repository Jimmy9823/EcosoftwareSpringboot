package com.EcoSoftware.Scrum6.Repository;

import com.EcoSoftware.Scrum6.Entity.RutaParadaEntity;
import com.EcoSoftware.Scrum6.Entity.RutaRecoleccionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RutaParadaRepository extends JpaRepository<RutaParadaEntity, Long> {

    List<RutaParadaEntity> findAllByRutaOrderByOrden(RutaRecoleccionEntity ruta);

    int countByRuta(RutaRecoleccionEntity ruta);

    Optional<RutaParadaEntity> findByRutaAndOrden(RutaRecoleccionEntity ruta, Integer orden);

    boolean existsByRecoleccion(com.EcoSoftware.Scrum6.Entity.RecoleccionEntity recoleccion);
}
