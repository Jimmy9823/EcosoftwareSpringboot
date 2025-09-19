package com.EcoSoftware.Scrum6.Repository;

import java.time.OffsetDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.EcoSoftware.Scrum6.Entity.SolicitudRecoleccionEntity;

@Repository
public interface SolicitudRecoleccionRepository extends JpaRepository<SolicitudRecoleccionEntity, Long> {

    // Buscar por usuario
    List<SolicitudRecoleccionEntity> findByUsuarioId(Long usuarioId);

    // Buscar por estado
    List<SolicitudRecoleccionEntity> findByEstadoPeticion(SolicitudRecoleccionEntity.EstadoPeticion estado);

    // Buscar por tipo de residuo
    List<SolicitudRecoleccionEntity> findByTipoResiduo(SolicitudRecoleccionEntity.TipoResiduo tipoResiduo);

    // Buscar por fecha programada exacta
    List<SolicitudRecoleccionEntity> findByFechaProgramada(OffsetDateTime fechaProgramada);

    // Buscar por rango de fechas (ejemplo: solicitudes entre dos días)
    List<SolicitudRecoleccionEntity> findByFechaProgramadaBetween(OffsetDateTime inicio, OffsetDateTime fin);
}
