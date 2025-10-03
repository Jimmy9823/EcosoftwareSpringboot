package com.EcoSoftware.Scrum6.Repository;

import com.EcoSoftware.Scrum6.Entity.SolicitudRecoleccionEntity;
import com.EcoSoftware.Scrum6.Enums.EstadoPeticion;
import com.EcoSoftware.Scrum6.Enums.Localidad;
import com.EcoSoftware.Scrum6.Enums.TipoResiduo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SolicitudRecoleccionRepository extends JpaRepository<SolicitudRecoleccionEntity, Long> {

    // Buscar solicitudes por estado
    List<SolicitudRecoleccionEntity> findByEstadoPeticion(EstadoPeticion estadoPeticion);

    // Buscar solicitudes de un usuario (ciudadano)
    List<SolicitudRecoleccionEntity> findByUsuario_IdUsuario(Long usuarioId);

    // Buscar solicitudes aceptadas por un recolector/empresa
    List<SolicitudRecoleccionEntity> findByAceptadaPor_IdUsuario(Long usuarioId);

    // Buscar solicitudes por localidad
    List<SolicitudRecoleccionEntity> findByLocalidad(Localidad localidad);

    // Buscar solicitudes por tipo de residuo
    List<SolicitudRecoleccionEntity> findByTipoResiduo(TipoResiduo tipoResiduo);

    // Buscar solicitudes de un usuario en un estado específico
    List<SolicitudRecoleccionEntity> findByUsuario_IdUsuarioAndEstadoPeticion(Long usuarioId, EstadoPeticion estadoPeticion);

    // Buscar solicitudes por localidad y tipo de residuo
    List<SolicitudRecoleccionEntity> findByLocalidadAndTipoResiduo(Localidad localidad, TipoResiduo tipoResiduo);
    List<SolicitudRecoleccionEntity> findByLocalidadAndEstadoPeticion(Localidad localidad, EstadoPeticion estadoPeticion);
}
